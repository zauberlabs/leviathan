/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * Clase base para los  {@link URIFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public abstract class AbstractURIFetcher implements URIFetcher {

    /** @see URIFetcher#fetch(URI) */
    public final URIFetcherResponse fetch(final URI uri) {
        return fetch(new InmutableURIAndCtx(uri));
    }

}
