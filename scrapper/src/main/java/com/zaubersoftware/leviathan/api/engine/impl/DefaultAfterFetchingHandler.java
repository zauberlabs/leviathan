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

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcherResponse;

import com.zaubersoftware.leviathan.api.engine.Action;
import com.zaubersoftware.leviathan.api.engine.ActionAndControlStructureHandler;
import com.zaubersoftware.leviathan.api.engine.ActionAndThenFetch;
import com.zaubersoftware.leviathan.api.engine.ActionHandler;
import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler;
import com.zaubersoftware.leviathan.api.engine.AfterJavaObjectHandler;
import com.zaubersoftware.leviathan.api.engine.AfterXMLTransformer;
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ErrorTolerantAfterThen;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ClosureAdapterPipe;

/**
 * Default implementation of the {@link AfterFetchingHandler} interface that uses a {@link DefaultEngine}
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class DefaultAfterFetchingHandler implements AfterFetchingHandler {

    private final DefaultEngine engine;

    private final class DefaultErrorTolerantAfterThen extends EngineFowarder implements ErrorTolerantAfterThen {

        /**
         * Creates the DefaultErrorTolerantAfterThen.
         *
         * @param target
         */
        public DefaultErrorTolerantAfterThen() {
            super(DefaultAfterFetchingHandler.this.engine);
        }

        @Override
        public ProcessingFlow pack() {
            return DefaultAfterFetchingHandler.this.pack();
        }

    }

    /**
     * Creates the DefaultAfterFetchingHandler.
     *
     * @param engine
     */
    public DefaultAfterFetchingHandler(final DefaultEngine engine) {
        Validate.notNull(engine, "The engine cannot be null");
        this.engine = engine;
    }

    @Override
    public AfterXMLTransformer transformXML(final String xsl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AfterXMLTransformer transformXML(final Source xsl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AfterXMLTransformer transformXML(final Transformer xsl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> AfterJavaObjectHandler<T> toJavaObject(final Class<T> aClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ActionHandler<URIFetcherResponse> onAnyExceptionDo(final ExceptionHandler<Throwable> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E extends Throwable> ActionHandler<URIFetcherResponse> onError(final Class<E> throwableClass,
            final ExceptionHandler<E> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <R> ActionAndControlStructureHandler<R> then(final Action<URIFetcherResponse, R> object) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AfterFetchingHandler then(final ActionAndThenFetch<URIFetcherResponse> object) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ErrorTolerantAfterThen then(final ContextAwareClosure<URIFetcherResponse> closure) {
        this.engine.appendPipe(new ClosureAdapterPipe<URIFetcherResponse>(closure));
        return new DefaultErrorTolerantAfterThen();
    }

    @Override
    public AfterFetchingHandler thenFetch(final String uriTemplate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AfterFetchingHandler thenFetch(final URI uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Engine thenDoNothing() {
        return this.engine;
    }

    @Override
    public AfterXMLTransformer sanitizeHTML() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProcessingFlow pack() {
        return this.engine.packCurrentFlow();
    }

}
