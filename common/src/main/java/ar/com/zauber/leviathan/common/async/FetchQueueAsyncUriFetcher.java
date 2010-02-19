/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import java.net.URI;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;

/**
 * {@link AsyncUriFetcher} que 
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class FetchQueueAsyncUriFetcher implements AsyncUriFetcher {
    private final URIFetcher fetcher;
    private final JobQueue fetcherQueue;
    private final Thread inScheduler;
    private final JobQueue processingQueue;
    private final Thread outScheduler;
    
    private final Logger logger = Logger.getLogger(JobQueue.class);
    
    /** */
    public FetchQueueAsyncUriFetcher(
            final URIFetcher fetcher,
            final JobScheduler fetcherScheduler,
            final JobScheduler processingScheduler) {
        
        Validate.notNull(fetcher);
        Validate.notNull(fetcherScheduler);
        Validate.notNull(processingScheduler);
        
        
        // alertamos de posibles problemas debido a copy paste:
        // las queue y schedulers no pueden ser la misma instancia
        Validate.isTrue(fetcherScheduler.getQueue() 
                != processingScheduler.getQueue(),
                "Los schedulers de fetcher y procesamiento no pueden "
                + "compartir la misma queue");
        Validate.isTrue(fetcherScheduler != processingScheduler);
        
        this.fetcher = fetcher;
        this.fetcherQueue = fetcherScheduler.getQueue();
        this.processingQueue = processingScheduler.getQueue();
        
        inScheduler = new Thread(fetcherScheduler, "JobScheduler-IN");
        outScheduler = new Thread(processingScheduler, "JobScheduler-IN");
        
        inScheduler.start();
        outScheduler.start();
    }
    
    /** @see AsyncUriFetcher#fetch(URI, Closure) */
    public final void fetch(final URI uri, 
            final Closure<URIFetcherResponse> closure) {
        fetch(new InmutableURIAndCtx(uri), closure);
    }

    /** @see AsyncUriFetcher#fetch(URIAndCtx, Closure) */
    public final void fetch(final URIAndCtx uriAndCtx, 
            final Closure<URIFetcherResponse> closure) {
        
        fetcherQueue.add(new Job() {
            public void run() {
                final URIFetcherResponse r = fetcher.fetch(uriAndCtx);
                processingQueue.add(new Job() {
                    public void run() {
                        closure.execute(r);
                    }
                });
                // TODO: notificar a la fetcherQueue que ya se 
                // fetcheo el elemento. 
            }
        });
    }

    /** @see AsyncUriFetcher#shutdown() */
    public final void shutdown() {
        // no aceptamos más trabajos.
        fetcherQueue.shutdown();
        waitForTermination(inScheduler);
        processingQueue.shutdown();
        waitForTermination(outScheduler);
        
    }

    /** espera que termine un thread */
    private boolean waitForTermination(final Thread thread) {
        boolean wait = true;
        
        // esperamos que el scheduler consuma todos los trabajos
        while(wait && thread.isAlive()) {
            try {
                thread.join();
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
        return wait;
    }
}
