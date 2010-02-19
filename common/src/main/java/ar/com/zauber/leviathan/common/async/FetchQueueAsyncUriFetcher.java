/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;
import ar.com.zauber.leviathan.common.async.impl.ClosureJob;

/**
 * {@link AsyncUriFetcher} que 
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class FetchQueueAsyncUriFetcher implements AsyncUriFetcher {
    private final URIFetcher fetcher;
    private final JobQueue fetchQueue;
    private final Thread scheduler;
    private final Logger logger = Logger.getLogger(JobQueue.class);
    
    /** */
    public FetchQueueAsyncUriFetcher(
            final URIFetcher fetcher,
            final JobQueue fetchQueue,
            final FetcherScheduler fetcherScheduler) {
        Validate.notNull(fetcher);
        Validate.notNull(fetcherScheduler);
        
        this.fetcher = fetcher;
        this.fetchQueue = fetchQueue;
        scheduler = new Thread(fetcherScheduler);
        scheduler.start();
    }
    
    /** @see AsyncUriFetcher#fetch(URI, Closure) */
    public final void fetch(final URI uri, 
            final Closure<URIFetcherResponse> closure) {
        fetch(new InmutableURIAndCtx(uri), closure);
    }

    /** @see AsyncUriFetcher#fetch(URIAndCtx, Closure) */
    public final void fetch(final URIAndCtx uriAndCtx, 
            final Closure<URIFetcherResponse> closure) {
        fetchQueue.add(new ClosureJob(uriAndCtx, closure, fetcher));
    }

    /** @see AsyncUriFetcher#shutdown() */
    public final void shutdown() {
        // no aceptamos más trabajos.
        fetchQueue.shutdown();
        
        boolean wait = true; 

        // esperamos que el scheduler consuma todos los trabajos
        while(wait && scheduler.isAlive()) {
            try {
                scheduler.join();
                wait = false;
            } catch (InterruptedException e) {
                logger.log(Level.WARN, "interrupted while shutting down");
                try {
                    // si justo tenemos un bug, evitamos comermos  todo el cpu
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    // nada que  hacer
                }
            }
        }
    }
}
