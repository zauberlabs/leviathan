/**
 * Copyright (c) 2009-2015 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common;

import java.io.InputStream;
import java.net.URI;

import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;
/**
 * Clase base para los  {@link URIFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public abstract class AbstractURIFetcher implements URIFetcher {

    @Override
    public final FetchingTask createGet(final URI uri) {
        return createGet(new InmutableURIAndCtx(uri));
    }
    
    /** @see URIFetcher#get(URI) */
    public final URIFetcherResponse get(final URI uri) {
        return createGet(new InmutableURIAndCtx(uri)).execute();
    }
    
    @Override
    public final URIFetcherResponse get(final URIAndCtx uriAndCtx) {
        return createGet(uriAndCtx).execute();
    }
    
    @Override
    public final URIFetcherResponse post(final URIAndCtx uriAndCtx,
            final InputStream body) {
        return createPost(uriAndCtx, body).execute();
    }


    @Override
    public final URIFetcherResponse post(final URIAndCtx uriAndCtx, final UrlEncodedPostBody body) {
        return createPost(uriAndCtx, body).execute();
    }
}
