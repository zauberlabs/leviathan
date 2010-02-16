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
package ar.com.zauber.leviathan.common;

import java.net.URI;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcherHttpResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * {@link URIFetcherHttpResponse} que recibe un ctx, y otro
 * {@link URIFetcherHttpResponse} en quien delega todos los mensajes salvo el
 * {@link #getURIAndCtx()}
 *
 * @author Mariano Semelman
 * @since Dec 4, 2009
 */
public class CtxDecorableURIFetcherResponse implements URIFetcherResponse {
    private final URIFetcherResponse target;
    private final URIAndCtx uriAndCtx;


    /** Creates the CtxDecorableURIFetcherResponse. */
    public CtxDecorableURIFetcherResponse(final URIFetcherResponse target,
            final URIAndCtx uriAndCtx) {
        Validate.notNull(target);
        Validate.notNull(uriAndCtx);

        this.target = target;
        this.uriAndCtx = uriAndCtx;
    }

    /** @see URIFetcherResponse#getURIAndCtx() */
    public final URIAndCtx getURIAndCtx() {
        return uriAndCtx;
    }

    /** @see URIFetcherResponse#getError() */
    public final Throwable getError() throws IllegalStateException {
        return target.getError();
    }

    /** @see URIFetcherResponse#getHttpResponse() */
    public final URIFetcherHttpResponse getHttpResponse()
            throws IllegalStateException {
        return target.getHttpResponse();
    }

    /** @see URIFetcherResponse#getURI() */
    public final URI getURI() {
        return target.getURI();
    }

    /** @see URIFetcherResponse#isSucceeded() */
    public final boolean isSucceeded() {
        return target.isSucceeded();
    }
}
