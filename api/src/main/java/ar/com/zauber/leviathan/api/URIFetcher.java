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
package ar.com.zauber.leviathan.api;

import java.io.InputStream;
import java.net.URI;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
/**
 * URI fetcher
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface URIFetcher {

    /**  @deprecated use {@link #createGet(URI)}.execute() */
    @Deprecated
    URIFetcherResponse get(URI uri);

    /** @deprecated use {@link #createGet(URIAndCtx)}.execute() */
    @Deprecated
    URIFetcherResponse get(URIAndCtx uri);

    /** @deprecated use {@link #post(URIAndCtx, InputStream)} */
    @Deprecated
    URIFetcherResponse post(URIAndCtx uri, InputStream body);

    /** @deprecated use {@link #post(URIAndCtx, UrlEncodedPostBody)} */
    @Deprecated
    URIFetcherResponse post(URIAndCtx uriAndCtx, UrlEncodedPostBody body);
    
    /** 
     * creates a GET task bound to this fetcher
     * <p>
     * This is a helper if you want to use fetchers (you can use directly your
     * {@link FetchingTask} implementation.
     * </p>
     */
    FetchingTask createGet(URI uri);
    
    /** 
     * creates a GET task bound to this fetcher
     * <p>
     * This is a helper if you want to use fetchers (you can use directly your
     * {@link FetchingTask} implementation.
     * </p>
     */
    FetchingTask createGet(URIAndCtx uriAndCtx);
    
    /** 
     * creates a POST task bound to this fetcher
     * <p>
     * This is a helper if you want to use fetchers (you can use directly your
     * {@link FetchingTask} implementation.
     * </p>
     */
    FetchingTask createPost(URIAndCtx uriAndCtx, InputStream body);
    
    /** 
     * creates a POST task bound to this fetcher
     * <p>
     * This is a helper if you want to use fetchers (you can use directly your
     * {@link FetchingTask} implementation.
     * </p>
     */
    FetchingTask createPost(URIAndCtx uriAndCtx, UrlEncodedPostBody body);
}
