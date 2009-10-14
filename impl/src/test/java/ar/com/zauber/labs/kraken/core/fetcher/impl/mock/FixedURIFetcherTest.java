/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.mock;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;


/**
 * Tests {@link FixedURIFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class FixedURIFetcherTest {
    private final URIFetcher fetcher;

    /** constructor */
    public FixedURIFetcherTest() throws URISyntaxException {
        final Map<URI, String> map = new HashMap<URI, String>();
        map.put(new URI("http://noexiste"), 
               "ar/com/zauber/labs/kraken/core/fetcher/impl/mock/noexiste.txt");
        fetcher = new FixedURIFetcher(map, Charset.forName("iso-8859-1"));
    }

    /** test found  */
    @Test
    public final void found() throws URISyntaxException, IOException {
        final URIFetcherResponse r = fetcher.fetch(new URI("http://noexiste"));
        Assert.assertNotNull(r);
        Assert.assertEquals(200, r.getHttpResponse().getStatusCode());
        Assert.assertEquals("ñoño", 
                IOUtils.toString(r.getHttpResponse().getContent()));
        
        
    }
    
    /** test found  */
    @Test
    public final void notfound() throws URISyntaxException, IOException {
        final URIFetcherResponse r = fetcher.fetch(new URI("http://lalarara"));
        Assert.assertNotNull(r);
        Assert.assertFalse(r.isSucceeded());
        Assert.assertEquals(UnknownHostException.class, r.getError().getClass());
    }
}
