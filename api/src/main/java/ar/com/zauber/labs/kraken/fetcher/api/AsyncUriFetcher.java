/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.net.URI;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;

/**
 * Obtención de una página de manera asincrónica
 * 
 * 
 * @author Marcelo Turrin
 * @since Jan 21, 2010
 */
public interface AsyncUriFetcher {

    /**
     * Busca la página deda por la uri indicada y al terminar llama al closure
     * con la respuesta
     * 
     * @param uri URIs to retrieve
     * @param closure closure donde se publica el resultado (el contenido de 
     *        la pagina, los errores; etc).
     */
    void fetch(URI uri, Closure<URIFetcherResponse> closure);
    
    /**
     * Busca la página deda por la uri indicada y al terminar llama al closure
     * con la respuesta
     * 
     * @param uriAndCtx URIs to retrieve
     * @param closure closure donde se publica el resultado (el contenido de 
     *        la pagina, los errores; etc).
     */
    void fetch(URIAndCtx uriAndCtx, Closure<URIFetcherResponse> closure);
}
