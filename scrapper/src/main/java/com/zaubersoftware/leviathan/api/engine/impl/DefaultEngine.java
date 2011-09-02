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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.validate.Validate;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.Pipe;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.FlowBuilderPipe;

/**
 * The Default implementation of the {@link Engine} interface
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class DefaultEngine implements Engine {

    private static final ExceptionHandler<Throwable> DEFAULT_EXCEPTION_HANDLER = new ExceptionHandler<Throwable>() {
        @Override
        public void handle(final Throwable trowable) {
            throw new UnhandledException(trowable);
        }
    };

    private final AsyncUriFetcher fetcher;
    private final Map<URI, List<Pipe<?,?>>> pipeFlowForURIs = new HashMap<URI, List<Pipe<?,?>>>();
    private final Map<URI, InmutableProcessingFlow> packedFlows = new HashMap<URI, InmutableProcessingFlow>();

    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends Throwable>, ExceptionHandler> handlers = new HashMap<Class<? extends Throwable>, ExceptionHandler>();
    private ExceptionHandler<Throwable> defaultFallbackExceptionHandler = DEFAULT_EXCEPTION_HANDLER;

    /**
     * Creates the DefaultEngine.
     *
     * @param fetcher
     */
    public DefaultEngine(final AsyncUriFetcher fetcher) {
        Validate.notNull(fetcher, "the fetcher can not be null!");
        this.fetcher = fetcher;
    }

    @Override
    public Engine onError(final ExceptionHandler<Throwable> handler) {
        Validate.notNull(handler, "The exception handler cannot be null");
        this.defaultFallbackExceptionHandler = handler;
        return this;
    }

    @Override
    public <E extends Throwable> Engine onError(final Class<E> throwableClass, final ExceptionHandler<E> handler) {
        Validate.notNull(throwableClass, "The throwable class cannot be null");
        Validate.notNull(handler, "The exception handler cannot be null");
        this.handlers.put(throwableClass, handler);
        return this;
    }

    @Override
    public AfterFetchingHandler forUri(final String uriTemplate) {
        Validate.notBlank(uriTemplate, "The URI template cannot be blank");
        throw new NotImplementedException();
    }

    @Override
    public AfterFetchingHandler forUri(final URI uri) {
        Validate.notNull(uri, "The URI to be fetched cannot be null");
        this.pipeFlowForURIs.put(uri, new ArrayList<Pipe<?,?>>());
        return new DefaultAfterFetchingHandler(this, uri);
    }

    @Override
    public Engine forUri(final URI uri, final ProcessingFlow flow) {
        return null;
    }

    @Override
    public Engine forUri(final String uriTemplate, final ProcessingFlow flow) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AfterFetchingHandler afterFetch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Engine doGet(final URI uri) {
        Validate.notNull(uri, "The URI for which a GET request will be done cannot be null");
        if (!this.packedFlows.containsKey(uri)) {
            packFlowForURI(uri);
        }
        this.fetcher.get(uri, checkedGetFlow(uri).getRootClosure());
        return this;
    }

    @Override
    public Engine doGet(final URI uri, final ProcessingFlow flow) {
        Validate.notNull(uri, "The URI for which a GET request will be done cannot be null");
        return this;
    }

    @Override
    public Engine awaitIdleness() throws InterruptedException {
        this.fetcher.awaitIdleness();
        return this;
    }

    @Override
    public boolean awaitIdleness(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.fetcher.awaitIdleness(timeout, unit);
    }

    /**
     * Appends a {@link Pipe} to {@link URI}'s pipe flow
     *
     * @param uri
     * @param pipe
     */
    protected void appendPipe(final URI uri, final Pipe<?,?> pipe) {
        Validate.notNull(uri, "The URI for which a pipe will be appended cannot be null");
        Validate.notNull(pipe, "The pipe to be appended cannot be null");
        this.checkedGetPipes(uri).add(pipe);
    }

    /**
     * Defines a {@link InmutableProcessingFlow} for a {@link URI}.
     *
     * @param uri
     * @param flow
     */
    protected void flowForURI(final URI uri, final InmutableProcessingFlow flow) {
        Validate.notNull(uri, "The URI for which a flow will be registered cannot be null");
        Validate.notNull(flow, "The processing flow cannot be null");
        this.packedFlows.put(uri, flow);
    }

    /**
     * Builds and defines a {@link InmutableProcessingFlow} for the given URI using the current {@link Pipe} flow
     *
     * @param uri
     */
    protected ProcessingFlow packFlowForURI(final URI uri) {
        final InmutableProcessingFlow flow = new InmutableProcessingFlow(buildClosureFromPipesFlow(uri));
        flowForURI(uri, flow);
        return flow;
    }

    /**
     * Checks if exists a pipe flow for the given URI and returns it.
     *
     * @param uri The URI for which a pipe flow will be obtained.
     * @return The pipe flow
     * @throws IllegalArgumentException if there is not pipe flow for the given URI
     */
    private List<Pipe<?, ?>> checkedGetPipes(final URI uri) {
        Validate.notNull(uri, "The URI cannot be null");
        final List<Pipe<?, ?>> pipes = this.pipeFlowForURIs.get(uri);
        Validate.notNull(pipes, String.format("There is not pipe flow for the given URI: %s", uri));
        return pipes;
    }

    private InmutableProcessingFlow checkedGetFlow(final URI uri) {
        Validate.notNull(uri, "The URI cannot be null");
        final InmutableProcessingFlow flow = this.packedFlows.get(uri);
        Validate.notNull(flow, String.format("There is not flow for the given URI: %s", uri));
        return flow;
    }

    /**
     * Builds the {@link Closure}<{@link URIFetcherResponse}> from a collection of pipes
     * for the given {@link URI}
     *
     * @param uri
     * @return
     */
    private Closure<URIFetcherResponse> buildClosureFromPipesFlow(final URI uri) {
        // Pipe chain
        final FlowBuilderPipe<URIFetcherResponse, Object> rootPipe =
            new FlowBuilderPipe<URIFetcherResponse, Object>(checkedGetPipes(uri), this.handlers);

        rootPipe.setDefaultExceptionHandler(this.defaultFallbackExceptionHandler);

        return new FetcherResponsePipeAdapterClosure<Object>(rootPipe);
    }

}
