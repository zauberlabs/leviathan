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
package ar.com.zauber.leviathan.common.async.impl;

import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.async.AsyncUriFetcherObserver;

/**
 * TODO Descripcion de la clase. Los comenterios van en castellano.
 *
 *
 * @author Juan F. Codagnone
 * @since Mar 1, 2010
 */
public class NullAsyncUriFetcherObserver implements AsyncUriFetcherObserver {

    /** @see AsyncUriFetcherObserver#beginFetch(URIFetcherResponse.URIAndCtx) */
    public void beginFetch(final URIAndCtx uriAndCtx) {
        // nada que hacer
    }

    /** @see AsyncUriFetcherObserver#beginProcessing(URIFetcherResponse.URIAndCtx) */
    public void beginProcessing(final URIAndCtx uriAndCtx) {
        // nada que hacer
    }

    /** @see AsyncUriFetcherObserver#finishFetch(URIAndCtx, long) */
    public void finishFetch(final URIAndCtx uriAndCtx, final long elapsed) {
        // nada que hacer

    }

    /** @see AsyncUriFetcherObserver#finishProcessing(URIAndCtx, long) */
    public void finishProcessing(final URIAndCtx uriAndCtx, final long elapsed) {
        // nada que hacer

    }

    /** @see AsyncUriFetcherObserver#newFetch(URIFetcherResponse.URIAndCtx) */
    public void newFetch(final URIAndCtx uriAndCtx) {
        // nada que hacer

    }
}
