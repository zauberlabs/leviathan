/**
 * Copyright (c) 2009-2011 Zauber S.A. <http://www.zaubersoftware.com/>
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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

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
    
    /** @see AsyncUriFetcher#fetch(URI, Closure) */
    public final void fetch(final URI uri,
            final Closure<URIFetcherResponse> closure) {
        get(uri, closure);
    }
    
    /** @see AsyncUriFetcher#fetch(URIAndCtx, Closure) */
    public final void fetch(final URIAndCtx uriAndCtx,
            final Closure<URIFetcherResponse> closure) {
        get(uriAndCtx, closure);
    }
    
    /** @see AsyncUriFetcher#get(URIFetcherResponse.URIAndCtx, Closure) */
    public final void get(final URIAndCtx uriAndCtx, 
            final Closure<URIFetcherResponse> closure) {
        incrementActiveJobs();
        try {
            closure.execute(uriFetcher.get(uriAndCtx));
        } finally {
            decrementActiveJobs();
        }
    }
    
    /**
     * @see AsyncUriFetcher#post(URIFetcherResponse.URIAndCtx, InputStream,
     *      Closure)
     */
    public final void post(final URIAndCtx uriAndCtx, final InputStream body,
            final Closure<URIFetcherResponse> closure) {
        incrementActiveJobs();
        try {
            closure.execute(uriFetcher.post(uriAndCtx, body));
        } finally {
            decrementActiveJobs();
        }
    }
    
    /** @see AsyncUriFetcher#post(URIFetcherResponse.URIAndCtx, Map, Closure) */
    public final void post(final URIAndCtx uriAndCtx,
            final Map<String, String> body,
            final Closure<URIFetcherResponse> closure) {
        incrementActiveJobs();
        try {
            final UrlEncodedPostBody encodedBody = addToBody(body);
            closure.execute(uriFetcher.post(uriAndCtx, encodedBody));
        } finally {
            decrementActiveJobs();
        }
    }

    /** Create a UrlEncodedPostBody for a Map<String, String>
     * @param body
     * @return
     */
    private UrlEncodedPostBody addToBody(final Map<String, String> body) {
        final UrlEncodedPostBody encodedBody = new UrlEncodedPostBody();
        for (Entry<String, String> entry : body.entrySet()) {
            encodedBody.addSimpleParameter(entry.getKey(), entry.getValue());
        }
        return encodedBody;
    }
    

    /** @see AsyncUriFetcher#shutdown() */
    public final void shutdown() {
        // nothing to do
    }
    
    /** @see AsyncUriFetcher#shutdownNow() */
    public final void shutdownNow() {
        // nothing to do
    }

    /** @see AsyncUriFetcher#post(URIAndCtx, UrlEncodedPostBody, Closure) */
    public final void post(final URIAndCtx uriAndCtx, final UrlEncodedPostBody body,
            final Closure<URIFetcherResponse> closure) {
        incrementActiveJobs();
        try {
            closure.execute(uriFetcher.post(uriAndCtx, body));
        } finally {
            decrementActiveJobs();
        }
    }
}
