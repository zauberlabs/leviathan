/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;


/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Feb 9, 2011
 */
public class ContentTypePredicateTest {
    private URI uri; 
    {
        try {
            uri = new URI("http://foo.com");
        } catch (URISyntaxException e) {
            throw new UnhandledException(e);
        }
    }
    private static final String CONTENT_TYPE = "Content-Type";
    
    @Test
    public final void withoutContentForbidden() throws Exception {
        final Predicate<URIFetcherResponse> predicate = 
            new ContentTypePredicate(Arrays.asList("text/html"
            ), false);
        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        
        assertFalse(predicate.evaluate(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri),
                new InmutableURIFetcherHttpResponse("", 200, headers))));
    }
    
    @Test
    public final void withoutContentAccepted() throws Exception {
        final Predicate<URIFetcherResponse> predicate = 
            new ContentTypePredicate(Arrays.asList("text/html"
            ), true);
        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        
        assertTrue(predicate.evaluate(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri),
                new InmutableURIFetcherHttpResponse("", 200, headers))));
    }
    
    
    @Test
    public final void htmlContentAccepted() throws Exception {
        final Predicate<URIFetcherResponse> predicate = 
            new ContentTypePredicate(Arrays.asList("text/html"
            ), true);
        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        headers.put(CONTENT_TYPE, Arrays.asList("text/html; charset=utf8"));
        assertTrue(predicate.evaluate(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri),
                new InmutableURIFetcherHttpResponse("", 200, headers))));
    }
    
    @Test
    public final void htmlContentForbidden() throws Exception {
        final Predicate<URIFetcherResponse> predicate = 
            new ContentTypePredicate(Arrays.asList("text/plain"
            ), true);
        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        headers.put(CONTENT_TYPE, Arrays.asList("text/html; charset=utf8"));
        assertFalse(predicate.evaluate(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri),
                new InmutableURIFetcherHttpResponse("", 200, headers))));
    }
}