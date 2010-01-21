/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.labs.kraken.fetcher.api.AsyncUriFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;

/**
 * {@link AsyncUriFetcher} que no es muy inteleginte. segurante bloquente.
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public class NotsoAsyncUriFetcher extends AbstractAsyncUriFetcher {
    private final URIFetcher uriFetcher;

    /** Creates the NotsoAsyncUriFetcher. */
    public NotsoAsyncUriFetcher(final URIFetcher uriFetcher) {
        Validate.notNull(uriFetcher);
        this.uriFetcher = uriFetcher;
    }
    
    /** @see AsyncUriFetcher#fetch(URIFetcherResponse.URIAndCtx, Closure) */
    public final void fetch(final URIAndCtx uriAndCtx, 
            final Closure<URIFetcherResponse> closure) {
        closure.execute(uriFetcher.fetch(uriAndCtx));
    }
}
