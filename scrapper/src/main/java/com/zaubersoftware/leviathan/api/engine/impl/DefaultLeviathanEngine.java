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
package com.zaubersoftware.leviathan.api.engine.impl;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.validate.Validate;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;

import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.FetchingEngine;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Juan F. Codagnone
 * @since Dec 28, 2011
 */
public class DefaultLeviathanEngine extends DefaultEngine implements FetchingEngine {
    private final AsyncUriFetcher fetcher;
    

    /**
     * Creates the DefaultEngine.
     *
     * @param fetcher
     */
    public DefaultLeviathanEngine(final AsyncUriFetcher fetcher) {
        Validate.notNull(fetcher, "the fetcher can not be null!");
        this.fetcher = fetcher;
    }

    @Override
    public final FetchingEngine doGet(final URI uri, final ProcessingFlow flow) {
        return doGet(new InmutableURIAndCtx(uri), flow);
    }

    @Override
    public final FetchingEngine doGet(final URIAndCtx uriAndCtx, final ProcessingFlow flow) {
        Validate.notNull(uriAndCtx, "The URI for which a GET request will be done cannot be null");
        this.fetcher.get(uriAndCtx, adaptProcessingFlowToClosure(flow));
        return this;
    }

    @Override
    public final FetchingEngine awaitIdleness() throws InterruptedException {
        this.fetcher.awaitIdleness();
        return this;
    }

    @Override
    public final boolean awaitIdleness(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.fetcher.awaitIdleness(timeout, unit);
    }
    
    @Override
    public final void shutdown() {
        fetcher.shutdown();
    }

    @Override
    public final void shutdownNow() {
        fetcher.shutdownNow();
    }
    

    /**
     * Adapts a {@link ProcessingFlow} to a {@link Closure}&lt;{@link URIFetcherResponse}&gt;
     *
     * @param uri
     * @return
     */
    private FetcherResponsePipeAdapterClosure<Void> adaptProcessingFlowToClosure(final ProcessingFlow flow) {
        return new FetcherResponsePipeAdapterClosure<Void>(flow.toPipe());
    }

}
