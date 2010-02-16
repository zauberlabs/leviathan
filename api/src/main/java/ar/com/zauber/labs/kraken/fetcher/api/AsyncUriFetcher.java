/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zauber.com.ar/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
