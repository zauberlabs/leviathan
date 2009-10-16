/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.common.ExecutorServiceBulkURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableBulkURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.common.mock.FixedURIFetcher;


/**
 * Tests {@link ExecutorServiceBulkURIFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class ExecutorServiceBulkURIFetcherTest {

    /** */
    @Test
    public final void foo() throws Exception {
        final URI bar = new URI("http://bar");
        final URI lalara = new URI("http://lalara");
        final URI foo = new URI("http://foo");
        
        final Map<URI, String> map = new HashMap<URI, String>();
        final String resource = 
            "ar/com/zauber/labs/kraken/core/fetcher/impl/mock/noexiste.txt"; 
        map.put(bar, resource);
        map.put(lalara, resource);
        
        final BulkURIFetcherResponse response =
            new ExecutorServiceBulkURIFetcher(
                    Executors.newSingleThreadExecutor(),
                    new FixedURIFetcher(map)).fetch(
                            Arrays.asList(bar, lalara, foo));
        Assert.assertNotNull(response);
        
        Assert.assertEquals(new InmutableBulkURIFetcherResponse(Arrays.asList(
            new URIFetcherResponse[] {
                new InmutableURIFetcherResponse(bar,
                    new InmutableURIFetcherHttpResponse(
                        new InputStreamReader(
                            IOUtils.toInputStream("foo")), 200)),
                new InmutableURIFetcherResponse(lalara,
                    new InmutableURIFetcherHttpResponse(
                            new InputStreamReader(
                                IOUtils.toInputStream("foo")), 200)),
                new InmutableURIFetcherResponse(foo, 
                        new UnknownHostException("foo"))
        })), response);
    }
}
