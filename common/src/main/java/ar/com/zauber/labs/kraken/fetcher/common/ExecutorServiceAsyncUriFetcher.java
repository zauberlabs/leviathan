/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.labs.kraken.fetcher.api.AsyncUriFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;

/**
 * {@link AsyncUriFetcher} que utiliza un {@link ExecutorService} para no 
 * bloquearse. Segun el ExecutorService que se use (ver su queue) es lo que suceda
 * cuando todos los workers estan ocupados (se encolan; se descartan; etc) 
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public class ExecutorServiceAsyncUriFetcher implements AsyncUriFetcher {
    private final ExecutorService executorService;
    private URIFetcher fetcher;

    /** Creates the ExecutorServiceAsyncUriFetcher. */
    public ExecutorServiceAsyncUriFetcher(final ExecutorService executorService,
            final URIFetcher fetcher) {
        Validate.notNull(executorService);
        Validate.notNull(fetcher);
        
        this.executorService = executorService;
        this.fetcher = fetcher;
    }

    /** @see AsyncUriFetcher#fetch(URI, Closure) */
    public final void fetch(final URI uri, 
            final Closure<URIFetcherResponse> closure) {
        fetch(new InmutableURIAndCtx(uri), closure);
    }
    
    /** @see AsyncUriFetcher#fetch(URIFetcherResponse.URIAndCtx, Closure) */
    public final void fetch(final URIAndCtx uriAndCtx, 
            final Closure<URIFetcherResponse> closure) {
        Validate.notNull(uriAndCtx);
        Validate.notNull(closure);
        
        executorService.submit(new Runnable() {
            public void run() {
                closure.execute(fetcher.fetch(uriAndCtx));
            }
        });
    }
}


