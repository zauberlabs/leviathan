/**
 * Copyright (c) 2009-2013 Zauber S.A. <http://zauberlabs.com/>
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

import org.apache.commons.lang.UnhandledException;

import ar.com.zauber.commons.validate.Validate;

import com.zaubersoftware.leviathan.api.engine.AfterExceptionCatchDefinition;
import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler;
import com.zaubersoftware.leviathan.api.engine.AfterHandleWith;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.Pipe;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;

/**
 * The Default implementation of the {@link Engine} interface
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public class DefaultEngine implements Engine, AfterHandleWith<Engine> {

    private static final ExceptionHandler DEFAULT_EXCEPTION_HANDLER = new ExceptionHandler() {
        @Override
        public void handle(final Throwable trowable) {
            throw new UnhandledException(trowable);
        }
    };

    
    private List<Pipe<?, ?>> currentPipeFlow = new ArrayList<Pipe<?, ?>>();
    private Pipe<?, ?> currentPipe;
    private final Map<Class<? extends Throwable>, ExceptionHandler> handlers = 
            new HashMap<Class<? extends Throwable>, ExceptionHandler>();
    private ExceptionHandler defaultFallbackExceptionHandler = DEFAULT_EXCEPTION_HANDLER;
    private final Map<Pipe<?, ?>, PipeExceptionResolver> pipeExceptionResolvers = 
            new HashMap<Pipe<?, ?>, PipeExceptionResolver>();


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

    @Override
    public final Engine onAnyExceptionDo(final ExceptionHandler handler) {
        Validate.notNull(handler, "The exception handler cannot be null");
        this.defaultFallbackExceptionHandler = handler;
        return this;
    }

    @Override
    public final Engine otherwiseHandleWith(final ExceptionHandler handler) {
        return onAnyExceptionDo(handler);
    }

    @Override
    public final <E extends Throwable> AfterExceptionCatchDefinition<Engine> on(final Class<E> throwableClass) {
        Validate.notNull(throwableClass, "The throwable class cannot be null");
        return new DefaultEngineAfterExceptionCatchDefinition<E>(throwableClass);
    }

    @Override
    public final AfterFetchingHandler afterFetch() {
        reset();
        return new DefaultAfterFetchingHandler(this);
    }

    /**
     * @param handler
     */
    public final void addExceptionHandlerForCurrentPipe(final ExceptionHandler handler) {
        this.pipeExceptionResolvers.get(this.currentPipe).setDefaultExceptionHandler(handler);
    }

    /**
     * @param handler
     */
    public final void addExceptionHandlerForCurrentPipe(final Class<? extends Throwable> throwableClass, 
                                                  final ExceptionHandler handler) {
        this.pipeExceptionResolvers.get(this.currentPipe).addExceptionHandler(throwableClass, handler);
    }


    /** Appends a {@link Pipe} to {@link URI}'s pipe flow */
    protected final void appendPipe(final Pipe<?, ?> pipe) {
        Validate.notNull(pipe, "The pipe to be appended cannot be null");
        this.currentPipe = pipe;
        this.pipeExceptionResolvers.put(pipe, new PipeExceptionResolver());
        this.currentPipeFlow.add(pipe);
    }

    /**  Builds and defines a {@link InmutableProcessingFlow} for the given URI using the current 
     *   {@link Pipe} flow
     */
    protected final ProcessingFlow packCurrentFlow() {
        final InmutableProcessingFlow flow =
            new InmutableProcessingFlow(this.currentPipeFlow,
                                        this.handlers,
                                        this.defaultFallbackExceptionHandler,
                                        this.pipeExceptionResolvers);
        reset();
        return flow;
    }

    /** Resets current state */
    private void reset() {
        this.currentPipeFlow = new ArrayList<Pipe<?, ?>>();
        this.currentPipe = null;
    }

}
