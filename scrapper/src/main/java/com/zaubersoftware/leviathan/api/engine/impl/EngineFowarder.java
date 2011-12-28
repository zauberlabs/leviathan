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

import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlowBinding;

/**
 * An {@link Engine} fowarder
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public abstract class EngineFowarder implements Engine {

    private final Engine target;

    /**
     * Creates the EngineFowarder.
     *
     * @param target
     */
    public EngineFowarder(final Engine target) {
        this.target = target;
    }

    @Override
    public Engine onAnyExceptionDo(final ExceptionHandler handler) {
        return this.target.onAnyExceptionDo(handler);
    }

    @Override
    public ProcessingFlowBinding bindURI(final URI uri) {
        return this.target.bindURI(uri);
    }

    @Override
    public ProcessingFlowBinding bindURI(final String uriTemplate) {
        return this.bindURI(uriTemplate);
    }

    @Override
    public AfterFetchingHandler forUri(final URI uri) {
        return this.forUri(uri);
    }

    @Override
    public AfterFetchingHandler afterFetch() {
        return this.afterFetch();
    }

    @Override
    public Engine doGet(final URI uri) {
        return this.target.doGet(uri);
    }

    @Override
    public Engine doGet(final URI uri, final ProcessingFlow flow) {
        return this.doGet(uri, flow);
    }

    @Override
    public Engine awaitIdleness() throws InterruptedException {
        return this.awaitIdleness();
    }

    @Override
    public boolean awaitIdleness(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.target.awaitIdleness(timeout, unit);
    }

    @Override
    public void shutdown() {
        this.target.shutdown();
    }

    @Override
    public void shutdownNow() {
        this.target.shutdownNow();
    }
}
