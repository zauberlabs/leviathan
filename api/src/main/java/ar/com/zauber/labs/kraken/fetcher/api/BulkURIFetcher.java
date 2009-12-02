/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.net.URI;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;

/**
 * Obtiene paginas webs `al por mayor'.
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface BulkURIFetcher extends URIFetcher {

    /**
     * Bulk retriever. Depende de la implementación, pero se espera que
     * llamar a este metodo sea mucho más eficiente que llamar a fetch
     * con una sola uri.
     *
     * @param uris URIs to retrieve
     * @return the details of each fetch
     */
    BulkURIFetcherResponse fetch(Iterable<URI> uris);


    /**
     * Bulk retriever. Depende de la implementación, pero se espera que
     * llamar a este metodo sea mucho más eficiente que llamar a fetch
     * con una sola uri.
     *
     * @param uriAndCtxs URIs to retrieve
     * @return the details of each fetch
     */
    BulkURIFetcherResponse fetchCtx(Iterable<URIAndCtx> uriAndCtxs);



}
