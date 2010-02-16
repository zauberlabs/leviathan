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
