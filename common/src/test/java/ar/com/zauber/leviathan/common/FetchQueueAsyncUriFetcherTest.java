/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.async.DirectExecutorService;
import ar.com.zauber.leviathan.common.async.FetchJob;
import ar.com.zauber.leviathan.common.async.impl.BlockingQueueFetchQueue;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;


/**
 * Tests {@link FetchQueueAsyncUriFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class FetchQueueAsyncUriFetcherTest {

    /** intenta cerra un fetcher ya cerrado */
    @Test(timeout = 2000)
    public final void shutdownShiny() {
        final AsyncUriFetcher fetcher = new FetchQueueAsyncUriFetcher(
                new FixedURIFetcher(
                new HashMap<URI, String>()), 
                new BlockingQueueFetchQueue(new LinkedBlockingQueue<FetchJob>()),
                new DirectExecutorService());
        fetcher.shutdown();
    }
    
    /** 
     * Arma un fetcher, le da urls para fetchear.
     * y llama al shutdown. 
     */
    @Test(timeout = 2000)
    public final void pool() throws URISyntaxException {
        final AsyncUriFetcher fetcher = new FetchQueueAsyncUriFetcher(
                new FixedURIFetcher(
                new HashMap<URI, String>()), 
                new BlockingQueueFetchQueue(new LinkedBlockingQueue<FetchJob>()),
                Executors.newSingleThreadExecutor());
        final AtomicInteger i = new AtomicInteger(0);
        final Closure<URIFetcherResponse> closure = 
            new Closure<URIFetcherResponse>() {
            public void execute(final URIFetcherResponse t) {
                i.addAndGet(1);
            }
        };
        for(int j = 0; j < 100; j++) {
            fetcher.fetch(new URI("http://foo"), closure);
        }
        fetcher.shutdown();
        Assert.assertEquals(100, i.get());
    }
}
