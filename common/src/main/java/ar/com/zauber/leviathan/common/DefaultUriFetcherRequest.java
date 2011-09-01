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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.zauber.leviathan.api.URIFetcherHttpResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.UriFetcherRequest;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 * 
 * 
 * @author flbulgarelli
 * @since Sep 1, 2011
 */
public class DefaultUriFetcherRequest implements UriFetcherRequest {

    private final URIAndCtx uriAndCtx;
    private final Map<String, List<String>> headers;

    /**
     * Creates the DefaultUriFetcherRequest.
     * 
     * @param uriAndCtx
     * @param headers
     */
    public DefaultUriFetcherRequest(final URIAndCtx uriAndCtx,
            final Map<String, List<String>> headers) {
        this.uriAndCtx = uriAndCtx;
        this.headers = headers;
    }

    @Override
    public final URIAndCtx getUriAndCtx() {
        return uriAndCtx;
    }

    @Override
    public final Map<String, List<String>> getHeaders() {
        return headers;
    }

    public static UriFetcherRequest from(final URIAndCtx uriAndCtx) {
        return new DefaultUriFetcherRequest(uriAndCtx, Collections
                .<String, List<String>> emptyMap());
    }

    public static UriFetcherRequest from(final URIAndCtx uriAndCtx,
            final HashMap<String, List<String>> headers) {
        return new DefaultUriFetcherRequest(uriAndCtx, headers);
    }

}
