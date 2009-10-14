/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.net.URI;
import java.util.Collection;

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
    BulkURIFetcherResponse fetch(Collection<URI> uris);
}
