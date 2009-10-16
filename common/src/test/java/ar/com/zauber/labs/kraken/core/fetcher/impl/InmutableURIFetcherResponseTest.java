/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherResponse;


/**
 * Test
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class InmutableURIFetcherResponseTest {

    /** test failed */
    @Test
    public final void failed() throws Exception {
        final URIFetcherResponse r = new InmutableURIFetcherResponse(
                new URI("http://bar"),
                new IllegalArgumentException("foo"));
        assertFalse(r.isSucceeded());
        assertEquals(IllegalArgumentException.class, r.getError().getClass());
        assertEquals("foo", r.getError().getMessage());
        assertEquals(new URI("http://bar"), r.getURI());
        try {
            r.getHttpResponse();
            fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }
    
    /** test failed */
    @Test
    public final void success() throws Exception {
        final URIFetcherResponse r = new InmutableURIFetcherResponse(
                new URI("http://bar"),
                new InmutableURIFetcherHttpResponse(
                    new InputStreamReader(
                        IOUtils.toInputStream("foo")), 200));
        assertTrue(r.isSucceeded());
        assertEquals(200, r.getHttpResponse().getStatusCode());
        assertEquals("foo", IOUtils.toString(r.getHttpResponse().getContent()));
        assertEquals(new URI("http://bar"), r.getURI());
        
        try {
            r.getError();
            fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    /** test failed */
    @Test
    public final void equalsHttp() throws Exception {
        assertEquals(
            new InmutableURIFetcherResponse(new URI("http://bar"),
                new InmutableURIFetcherHttpResponse(
                    new InputStreamReader(IOUtils.toInputStream("foo")), 200)), 
            new InmutableURIFetcherResponse(new URI("http://bar"),
                new InmutableURIFetcherHttpResponse(
                    new InputStreamReader(
                        IOUtils.toInputStream("foo")), 200)));
    }
    /** test failed */
    @Test
    public final void equalsFail() throws Exception {
        assertEquals(
            new InmutableURIFetcherResponse(new URI("http://bar"),
                new IllegalArgumentException("a")), 
            new InmutableURIFetcherResponse(new URI("http://bar"),
                    new IllegalArgumentException("a")));
    }
    
}
