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
package com.zaubersoftware.leviathan.api.engine.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.closure.NullClosure;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.Pipe;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ClosureAdapterPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.FlowBuilderPipe;

/**
 * An inmutable implementation of {@link ProcessingFlow}
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class InmutableProcessingFlow implements ProcessingFlow {

    private final FlowBuilderPipe<URIFetcherResponse, Void> pipeFlow;

    /**
     * Creates the InmutableProcessingFlow.
     *
     * @param pipes
     * @param exceptionHandlers
     * @param defaultExceptionHandler
     * @param pipeExceptionResolvers
     */
    public InmutableProcessingFlow(
            final Collection<Pipe<?, ?>> pipes,
            final Map<Class<? extends Throwable>, ExceptionHandler> exceptionHandlers,
            final ExceptionHandler defaultExceptionHandler,
            final Map<Pipe<?, ?>, PipeExceptionResolver> pipeExceptionResolvers) {
        Validate.notNull(pipes, "The collection of pipes to be assembled cannot be built");
        Validate.notNull(exceptionHandlers, "The exception handles cannot be null");
        Validate.notNull(defaultExceptionHandler, "The default exception handler cannot be null");
        Validate.notNull(pipeExceptionResolvers, "The pipeExceptionResolvers cannot be null");

        pipes.add(new ClosureAdapterPipe<Object>(new NullClosure<Object>()));
        this.pipeFlow = new FlowBuilderPipe<URIFetcherResponse, Void>(pipes, exceptionHandlers, 
                                                                      pipeExceptionResolvers);
        this.pipeFlow.setDefaultExceptionHandler(defaultExceptionHandler);
    }

    @Override
    public Pipe<URIFetcherResponse, Void> toPipe() {
        return this.pipeFlow;
    }

}
