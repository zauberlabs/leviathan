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
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;

import com.zaubersoftware.leviathan.api.engine.AfterExceptionCatchDefinition;
import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler;
import com.zaubersoftware.leviathan.api.engine.AfterHandleWith;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.Pipe;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlowBinding;

/**
 * The Default implementation of the {@link Engine} interface
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class DefaultEngine implements Engine, AfterHandleWith<Engine> {

    private static final ExceptionHandler DEFAULT_EXCEPTION_HANDLER = new ExceptionHandler() {
        @Override
        public void handle(final Throwable trowable) {
            throw new UnhandledException(trowable);
        }
    };

    private final AsyncUriFetcher fetcher;
    private final Map<URI, ProcessingFlow> packedFlows = new HashMap<URI, ProcessingFlow>();
    private List<Pipe<?, ?>> currentPipeFlow = new ArrayList<Pipe<?, ?>>();
    private URI currentURI;
    private Pipe<?, ?> currentPipe;

    private final Map<Class<? extends Throwable>, ExceptionHandler> handlers = 
            new HashMap<Class<? extends Throwable>, ExceptionHandler>();
    private ExceptionHandler defaultFallbackExceptionHandler = DEFAULT_EXCEPTION_HANDLER;
    private final Map<Pipe<?, ?>, PipeExceptionResolver> pipeExceptionResolvers = 
            new HashMap<Pipe<?, ?>, PipeExceptionResolver>();

    private final class DefaultEngineProcessingFlowBinding implements ProcessingFlowBinding {

        private final URI uri;

        /** Creates the DefaultProcessingFlowBinding. */
        public DefaultEngineProcessingFlowBinding(final URI uri) {
            this.uri = uri;
        }

        @Override
        public Engine toFlow(final ProcessingFlow flow) {
            Validate.notNull(flow, "The flow cannot be null");
            DefaultEngine.this.packedFlows.put(this.uri, flow);
            return DefaultEngine.this;
        }
    }

    private final class DefaultEngineAfterExceptionCatchDefinition<E extends Throwable> 
             implements AfterExceptionCatchDefinition<Engine> {

        private final Class<E> throwableClass;

        /**
         * Creates the DefaultEngineAfterExceptionCatchDefinition.
         *
         * @param throwableClass
         */
        public DefaultEngineAfterExceptionCatchDefinition(final Class<E> throwableClass) {
            Validate.notNull(throwableClass, "The throwable class cannot be null");
            this.throwableClass = throwableClass;
        }

        @Override
        public AfterHandleWith<Engine> handleWith(final ExceptionHandler handler) {
            Validate.notNull(handler, "The exception handler cannot be null");
            DefaultEngine.this.handlers.put(this.throwableClass, handler);
            return DefaultEngine.this;
        }

    }

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
    public Engine onAnyExceptionDo(final ExceptionHandler handler) {
        Validate.notNull(handler, "The exception handler cannot be null");
        this.defaultFallbackExceptionHandler = handler;
        return this;
    }

    @Override
    public Engine otherwiseHandleWith(final ExceptionHandler handler) {
        return onAnyExceptionDo(handler);
    }

    @Override
    public <E extends Throwable> AfterExceptionCatchDefinition<Engine> on(final Class<E> throwableClass) {
        Validate.notNull(throwableClass, "The throwable class cannot be null");
        return new DefaultEngineAfterExceptionCatchDefinition<E>(throwableClass);
    }

    @Override
    public AfterFetchingHandler forUri(final String uriTemplate) {
        Validate.notBlank(uriTemplate, "The URI template cannot be blank");
        reset();
        throw new NotImplementedException();
    }

    @Override
    public AfterFetchingHandler forUri(final URI uri) {
        Validate.notNull(uri, "The URI to be fetched cannot be null");
        reset();
        this.currentURI = uri;
        return new DefaultAfterFetchingHandler(this);
    }

    @Override
    public ProcessingFlowBinding bindURI(final URI uri) {
        Validate.notNull(uri, "The URI cannot be null");
        return new DefaultEngineProcessingFlowBinding(uri);
    }

    @Override
    public ProcessingFlowBinding bindURI(final String uriTemplate) {
        throw new NotImplementedException();
    }

    @Override
    public AfterFetchingHandler afterFetch() {
        reset();
        return new DefaultAfterFetchingHandler(this);
    }

    @Override
    public Engine doGet(final URI uri) {
        return doGet(new InmutableURIAndCtx(uri));
    }

    @Override
    public Engine doGet(final URIAndCtx uriAndCtx) {
        Validate.notNull(uriAndCtx, "The URI for which a GET request will be done cannot be null");
        if (!this.packedFlows.containsKey(uriAndCtx.getURI())) {
            throw new IllegalStateException(String.format("There is no processing flow for the given URI %s", 
                    uriAndCtx.getURI()));
        }
        this.fetcher.get(uriAndCtx, adaptProcessingFlowToClosure(checkedGetFlow(uriAndCtx.getURI())));
        return this;
    }


    @Override
    public Engine doGet(final URI uri, final ProcessingFlow flow) {
        return doGet(new InmutableURIAndCtx(uri), flow);
    }

    @Override
    public Engine doGet(final URIAndCtx uriAndCtx, final ProcessingFlow flow) {
        Validate.notNull(uriAndCtx, "The URI for which a GET request will be done cannot be null");
        this.fetcher.get(uriAndCtx, adaptProcessingFlowToClosure(flow));
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
     * @param handler
     */
    public void addExceptionHandlerForCurrentPipe(final ExceptionHandler handler) {
        this.pipeExceptionResolvers.get(this.currentPipe).setDefaultExceptionHandler(handler);
    }

    /**
     * @param handler
     */
    public void addExceptionHandlerForCurrentPipe(final Class<? extends Throwable> throwableClass, 
                                                  final ExceptionHandler handler) {
        this.pipeExceptionResolvers.get(this.currentPipe).addExceptionHandler(throwableClass, handler);
    }


    /**
     * Appends a {@link Pipe} to {@link URI}'s pipe flow
     *
     * @param uri
     * @param pipe
     */
    protected void appendPipe(final Pipe<?, ?> pipe) {
        Validate.notNull(pipe, "The pipe to be appended cannot be null");
        this.currentPipe = pipe;
        this.pipeExceptionResolvers.put(pipe, new PipeExceptionResolver());
        this.currentPipeFlow.add(pipe);
    }

    /**
     * Defines a {@link InmutableProcessingFlow} for a {@link URI}.
     *
     * @param uri
     * @param flow
     */
    protected void flowForURI(final URI uri, final InmutableProcessingFlow flow) {
        Validate.notNull(flow, "The processing flow cannot be null");
        if (uri != null) {
            this.packedFlows.put(uri, flow);
        }
    }

    /**
     * Builds and defines a {@link InmutableProcessingFlow} for the given URI using the current {@link Pipe} flow
     */
    protected ProcessingFlow packCurrentFlow() {
        final InmutableProcessingFlow flow =
            new InmutableProcessingFlow(this.currentPipeFlow,
                                        this.handlers,
                                        this.defaultFallbackExceptionHandler,
                                        this.pipeExceptionResolvers);
        flowForURI(this.currentURI, flow);

        reset();

        return flow;
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

    /**
     * Resets current state
     */
    private void reset() {
        this.currentPipeFlow = new ArrayList<Pipe<?, ?>>();
        this.currentURI = null;
        this.currentPipe = null;
    }

    /**
     * Checks if exists a flow for the given URI and returns it.
     *
     * @param uri The URI for which a pipe flow will be obtained.
     * @return The processing flow
     * @throws IllegalArgumentException if there is not flow for the given URI
     */
    private ProcessingFlow checkedGetFlow(final URI uri) {
        Validate.notNull(uri, "The URI cannot be null");
        final ProcessingFlow flow = this.packedFlows.get(uri);
        Validate.notNull(flow, String.format("There is not flow for the given URI: %s", uri));
        return flow;
    }

    @Override
    public void shutdown() {
        fetcher.shutdown();
    }

    @Override
    public void shutdownNow() {
        fetcher.shutdownNow();
    }
}