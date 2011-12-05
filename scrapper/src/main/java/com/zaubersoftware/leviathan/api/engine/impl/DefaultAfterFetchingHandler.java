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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcherResponse;

import com.zaubersoftware.leviathan.api.engine.Action;
import com.zaubersoftware.leviathan.api.engine.ActionAndControlStructureHandler;
import com.zaubersoftware.leviathan.api.engine.ActionAndThenFetch;
import com.zaubersoftware.leviathan.api.engine.ActionHandler;
import com.zaubersoftware.leviathan.api.engine.AfterExceptionCatchDefinition;
import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler;
import com.zaubersoftware.leviathan.api.engine.AfterForEachHandler;
import com.zaubersoftware.leviathan.api.engine.AfterForEachHandler.EndFor;
import com.zaubersoftware.leviathan.api.engine.AfterForEachHandler.ThenWithContextAwareClosure;
import com.zaubersoftware.leviathan.api.engine.AfterHandleWith;
import com.zaubersoftware.leviathan.api.engine.AfterJavaObjectHandler;
import com.zaubersoftware.leviathan.api.engine.AfterThen;
import com.zaubersoftware.leviathan.api.engine.AfterXMLTransformer;
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ErrorTolerantActionAndControlStructureHandler;
import com.zaubersoftware.leviathan.api.engine.ErrorTolerantAfterThen;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ActionPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ClosureAdapterPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ForEachPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.HTMLSanitizerPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ToJavaObjectPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.XMLPipe;

/**
 * Default implementation of the {@link AfterFetchingHandler} interface that uses a {@link DefaultEngine}
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class DefaultAfterFetchingHandler implements AfterFetchingHandler {

    private final DefaultEngine engine;

    private final class DefaultErrorTolerantAfterThen implements ErrorTolerantAfterThen, AfterExceptionCatchDefinition<AfterThen>, AfterHandleWith<AfterThen> {

        private Class<? extends Throwable> throwableClass;

        @Override
        public ProcessingFlow pack() {
            return DefaultAfterFetchingHandler.this.pack();
        }

        @Override
        public AfterThen onAnyExceptionDo(final ExceptionHandler handler) {
            DefaultAfterFetchingHandler.this.engine.addExceptionHandlerForCurrentPipe(handler);
            return this;
        }

        @Override
        public <E extends Throwable> AfterExceptionCatchDefinition<AfterThen> on(final Class<E> throwableClass) {
            Validate.notNull(throwableClass, "The throwable class canont be null");
            this.throwableClass = throwableClass;
            return this;
        }

        @Override
        public AfterHandleWith<AfterThen> handleWith(final ExceptionHandler handler) {
            DefaultAfterFetchingHandler.this
                .engine.addExceptionHandlerForCurrentPipe(this.throwableClass, handler);
            return this;
        }

        @Override
        public AfterThen otherwiseHandleWith(final ExceptionHandler handler) {
            DefaultAfterFetchingHandler.this.engine.addExceptionHandlerForCurrentPipe(handler);
            return this;
        }

    }

    private final class DefaultErrorTolerant<T> implements AfterExceptionCatchDefinition<T>, AfterHandleWith<T> {

        private Class<? extends Throwable> throwableClass;
        private final T ret;

        /**
         * Creates the DefaultErrorTolerant.
         *
         * @param throwableClass
         * @param ret
         */
        public DefaultErrorTolerant(final Class<? extends Throwable> throwableClass, final T ret) {
            Validate.notNull(throwableClass, "The throwableClass cannot be null");
            Validate.notNull(ret, "The return object cannot be null");
            this.throwableClass = throwableClass;
            this.ret = ret;
        }

        @Override
        public <E extends Throwable> AfterExceptionCatchDefinition<T> on(final Class<E> throwableClass) {
            Validate.notNull(throwableClass, "The throwable class canont be null");
            this.throwableClass = throwableClass;
            return this;
        }

        @Override
        public AfterHandleWith<T> handleWith(final ExceptionHandler handler) {
            DefaultAfterFetchingHandler.this.engine.addExceptionHandlerForCurrentPipe(this.throwableClass, handler);
            return this;
        }

        @Override
        public T otherwiseHandleWith(final ExceptionHandler handler) {
            DefaultAfterFetchingHandler.this.engine.addExceptionHandlerForCurrentPipe(handler);
            return this.ret;
        }

    }

    private final class DefaultActionAndControlStructureHandler<T> implements ErrorTolerantActionAndControlStructureHandler<T>, AfterJavaObjectHandler<T> {

        @Override
        public <R> ActionAndControlStructureHandler<R> then(final Action<T, R> action) {
            Validate.notNull(action, "The action cannot be null");
            DefaultAfterFetchingHandler.this.engine.appendPipe(new ActionPipe<T, R>(action));
            return new DefaultActionAndControlStructureHandler<R>();
        }
        @Override
        public AfterFetchingHandler then(final ActionAndThenFetch<T> object) {
            throw new NotImplementedException();
        }

        @Override
        public ErrorTolerantAfterThen then(final ContextAwareClosure<T> closure) {
            Validate.notNull(closure, "The closure cannot be null");
            DefaultAfterFetchingHandler.this.engine.appendPipe(new ClosureAdapterPipe<T>(closure));
            return new DefaultErrorTolerantAfterThen();
        }

        @Override
        public AfterFetchingHandler thenFetch(final String uriTemplate) {
            throw new NotImplementedException();
        }

        @Override
        public AfterFetchingHandler thenFetch(final URI uri) {
            throw new NotImplementedException();
        }

        @Override
        public Engine thenDoNothing() {
            pack();
            return DefaultAfterFetchingHandler.this.engine;
        }

        @Override
        public ProcessingFlow pack() {
            return DefaultAfterFetchingHandler.this.pack();
        }

        @Override
        public ErrorTolerantActionAndControlStructureHandler<T> onAnyExceptionDo(final ExceptionHandler handler) {
            DefaultAfterFetchingHandler.this.engine.addExceptionHandlerForCurrentPipe(handler);
            return this;
        }

        @Override
        public <E extends Throwable> AfterExceptionCatchDefinition<ErrorTolerantActionAndControlStructureHandler<T>> on(
                final Class<E> throwableClass) {
            return new DefaultErrorTolerant<ErrorTolerantActionAndControlStructureHandler<T>>(throwableClass, this);
        }

        @Override
        public <R> AfterForEachHandler<R, T> forEach(final Class<R> elementClass) {
            return new DefaultAfterForEachHandler<R, T>();
        }

    }

    public final class DefaultAfterForEachHandler<R,T> 
            implements AfterForEachHandler<R, T>, ThenWithContextAwareClosure<R, T>, EndFor<T> {

        private String propertyName;
        private ContextAwareClosure<R> closure;

        @Override
        public ThenWithContextAwareClosure<R, T> in(
                final String propertyName) {
            this.propertyName = propertyName;
            return this;
        }

        @Override
        public EndFor<T> then(
                final ContextAwareClosure<R> closure) {
            this.closure = closure;
            return this;
        }

        @Override
        public ErrorTolerantActionAndControlStructureHandler<T> endFor() {
            DefaultAfterFetchingHandler.this.engine.appendPipe(new ForEachPipe<T, R>(
                    this.propertyName, new ClosureAdapterPipe<R>(this.closure)));
            return new DefaultActionAndControlStructureHandler<T>();
        }

    }

    private final class DefaultAfterXMLTransformer implements AfterXMLTransformer {

        @Override
        public <T> AfterJavaObjectHandler<T> toJavaObject(final Class<T> aClass) {
            Validate.notNull(aClass, "The class cannot be null");
            DefaultAfterFetchingHandler.this.engine.appendPipe(new ToJavaObjectPipe<T>(aClass));
            return new DefaultActionAndControlStructureHandler<T>();
        }

        @Override
        public AfterXMLTransformer transformXML(final String xsl) {
            Validate.notNull(xsl, "The XSLT path cannot be null");
            try {
                DefaultAfterFetchingHandler.this.engine.appendPipe(
                        new XMLPipe(new StreamSource(new FileInputStream(xsl))));
            } catch (final FileNotFoundException e) {
                throw new UnhandledException(e);
            }
            return this;
        }

        @Override
        public AfterXMLTransformer transformXML(final Source xsl) {
            Validate.notNull(xsl, "The XSLT source cannot be null");
            DefaultAfterFetchingHandler.this.engine.appendPipe(new XMLPipe(xsl));
            return this;
        }

        @Override
        public AfterXMLTransformer transformXML(final Transformer xsl) {
            Validate.notNull(xsl, "The XSLT transformer cannot be null");
            DefaultAfterFetchingHandler.this.engine.appendPipe(new XMLPipe(xsl));
            return this;
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
        Validate.notNull(xsl, "The XSLT source cannot be null");
        try {
            this.engine.appendPipe(new XMLPipe(new StreamSource(new FileInputStream(xsl))));
        } catch (final FileNotFoundException e) {
            throw new UnhandledException(e);
        }
        return this;
    }

    @Override
    public AfterXMLTransformer transformXML(final Source xsl) {
        Validate.notNull(xsl, "The XSLT transformer cannot be null");
        this.engine.appendPipe(new XMLPipe(xsl));
        return this;
    }

    @Override
    public AfterXMLTransformer transformXML(final Transformer xsl) {
        Validate.notNull(xsl, "The XSLT transformer cannot be null");
        this.engine.appendPipe(new XMLPipe(xsl));
        return this;
    }

    @Override
    public <T> AfterJavaObjectHandler<T> toJavaObject(final Class<T> aClass) {
        Validate.notNull(aClass, "The class cannot be null");
        this.engine.appendPipe(new ToJavaObjectPipe<T>(aClass));
        return new DefaultActionAndControlStructureHandler<T>();
    }

    @Override
    public ActionHandler<URIFetcherResponse> onAnyExceptionDo(final ExceptionHandler handler) {
        Validate.notNull(handler, "The handler cannot be null");
        this.engine.onAnyExceptionDo(handler);
        return new DefaultActionAndControlStructureHandler<URIFetcherResponse>();
    }

    @Override
    public <R> ActionAndControlStructureHandler<R> then(final Action<URIFetcherResponse, R> object) {
        Validate.notNull(object, "The action cannot be null");
        this.engine.appendPipe(new ActionPipe<URIFetcherResponse, R>(object));
        return new DefaultActionAndControlStructureHandler<R>();
    }

    @Override
    public AfterFetchingHandler then(final ActionAndThenFetch<URIFetcherResponse> object) {
        throw new NotImplementedException();
    }

    @Override
    public ErrorTolerantAfterThen then(final ContextAwareClosure<URIFetcherResponse> closure) {
        this.engine.appendPipe(new ClosureAdapterPipe<URIFetcherResponse>(closure));
        return new DefaultErrorTolerantAfterThen();
    }

    @Override
    public AfterFetchingHandler thenFetch(final String uriTemplate) {
        throw new NotImplementedException();
    }

    @Override
    public AfterFetchingHandler thenFetch(final URI uri) {
        throw new NotImplementedException();
    }

    @Override
    public Engine thenDoNothing() {
        return this.engine;
    }

    @Override
    public AfterXMLTransformer sanitizeHTML() {
        this.engine.appendPipe(new HTMLSanitizerPipe());
        return new DefaultAfterXMLTransformer();
    }

    @Override
    public ProcessingFlow pack() {
        return this.engine.packCurrentFlow();
    }

    @Override
    public <E extends Throwable> AfterExceptionCatchDefinition<ActionHandler<URIFetcherResponse>> on(
            final Class<E> throwableClass) {
        throw new NotImplementedException("This should handle exeception that occur while fetching "
            + "(POST or GET). They should be treated as engine exceptions");
    }
}
