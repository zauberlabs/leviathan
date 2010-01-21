/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.labs.kraken.fetcher.api.AsyncUriFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * Clase base para los {@link AsyncUriFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public abstract class AbstractAsyncUriFetcher implements AsyncUriFetcher {

    /** @see AsyncUriFetcher#fetch(URI, Closure) */
    public final void fetch(final URI uri, 
            final Closure<URIFetcherResponse> closure) {
        fetch(new InmutableURIAndCtx(uri), closure);
    }
}
