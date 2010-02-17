/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.async.FetchJob;

/**
 * Implementación de {@link FetchJob} que utiliza un {@link Closure} para 
 * difererir el procesamiento de la ejecución.
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class ClosureFetchJob implements FetchJob {
    private final URIAndCtx uriAndCtx;
    private final Closure<URIFetcherResponse> closure;
    private final URIFetcher fetcher;

    /**
     * Creates the FetchJob.
     *
     * @param uriAndCtx URI y contexto que se desea obtener
     * @param closure   Accion a realizar una vez que se obtiene el recurso
     * @param fetcher   Fetcher que se utilizará para obtener el recurso
     */
    public ClosureFetchJob(final URIAndCtx uriAndCtx, 
            final Closure<URIFetcherResponse> closure,
            final URIFetcher fetcher) {
        Validate.notNull(uriAndCtx);
        Validate.notNull(closure);
        Validate.notNull(fetcher);
        
        this.uriAndCtx = uriAndCtx;
        this.closure = closure;
        this.fetcher = fetcher;
    }

    /** @see Runnable#run() */
    public final void run() {
        closure.execute(fetcher.fetch(uriAndCtx));        
    }
}
