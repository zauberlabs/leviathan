/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


/**
 * Tests {@link InmutableURIFetcherHttpResponse
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class InmutableURIFetcherHttpResponseTest {

    /** test */
    @Test
    public final void testEquals()  {
        assertEquals(
            new InmutableURIFetcherHttpResponse(
                new InputStreamReader(
                    IOUtils.toInputStream("foo")), 200),
                            
            new InmutableURIFetcherHttpResponse(
                new InputStreamReader(
                    IOUtils.toInputStream("foo")), 200));
    }
    
    /** test */
    @Test
    public final void testNotEquals()  {
        assertFalse(
            new InmutableURIFetcherHttpResponse(
                new InputStreamReader(
                    IOUtils.toInputStream("foo")), 404).equals(
            new InmutableURIFetcherHttpResponse(
                new InputStreamReader(
                    IOUtils.toInputStream("foo")), 200)));
    }
}
