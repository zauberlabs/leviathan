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
