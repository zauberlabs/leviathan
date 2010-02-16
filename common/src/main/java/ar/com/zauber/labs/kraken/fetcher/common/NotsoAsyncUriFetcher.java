/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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
