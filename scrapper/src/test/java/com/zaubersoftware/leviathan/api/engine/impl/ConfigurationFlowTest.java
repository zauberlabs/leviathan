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

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher;
import ar.com.zauber.leviathan.common.FetcherResponsePipeAdapterClosure;
import ar.com.zauber.leviathan.common.fluent.Fetchers;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;

import com.zaubersoftware.leviathan.api.engine.Action;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.Pipe;
import com.zaubersoftware.leviathan.api.engine.impl.dto.Link;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ActionPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ClosureAdapterPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.FlowBuilderPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ForEachPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.HTMLSanitizerPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ToJavaObjectPipe;
import com.zaubersoftware.leviathan.api.engine.impl.pipe.XMLPipe;

/**
 * Test driver for the configuration flow
 *
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ConfigurationFlowTest {
    private final URI mlhome = URI.create("http://www.mercadolibre.com.ar/");

    /** Tests the flow */
    @Test
    public void testFlow() {
        final Source xsltSource = new StreamSource(getClass().getClassLoader().getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"));
        final Action<Link, String> action = new Action<Link, String>() {
            @Override
            public String execute(final Link t) {
                return t.getTitle();
            }
        };
        final Closure<String> assertClosure = new Closure<String>() {
            @Override
            public void execute(final String input) {
                assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", input);
            }
        };
        final Collection<Pipe<?, ?>> pipes = Arrays.asList(new Pipe<?, ?>[] {
                new HTMLSanitizerPipe(),
                new XMLPipe(xsltSource),
                new ToJavaObjectPipe<Link>(Link.class),
                new ActionPipe<Link, String>(action),
                new ClosureAdapterPipe<String>(assertClosure),
        });

        doFetch(pipes);
    }

    /** Tests the exception flow */
    @SuppressWarnings("rawtypes")
    @Test
    public void testExceptionFlowWithExceptionHandler() {
        final Source xsltSource = new StreamSource(getClass().getClassLoader().getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"));
        final Action<Link, String> action = new Action<Link, String>() {
            @Override
            public String execute(final Link t) {
                throw new ArrayIndexOutOfBoundsException();
            }
        };
        final Closure<String> unreachableClosure = new Closure<String>() {
            @Override
            public void execute(final String input) {
                fail("The exception has not been catched. :(");
            }
        };
        final Collection<Pipe<?, ?>> pipes = Arrays.asList(new Pipe<?, ?>[] {
                new HTMLSanitizerPipe(),
                new XMLPipe(xsltSource),
                new ToJavaObjectPipe<Link>(Link.class),
                new ActionPipe<Link, String>(action),
                new ClosureAdapterPipe<String>(unreachableClosure),
        });

        final AtomicBoolean handlerHasBeenReached = new AtomicBoolean(false);
        final Map<Class<? extends Throwable>, ExceptionHandler> handlers =
            new HashMap<Class<? extends Throwable>, ExceptionHandler>();
        handlers.put(ArrayIndexOutOfBoundsException.class, new ExceptionHandler() {
            @Override
            public void handle(final Throwable trowable) {
                assertNotNull(trowable);
                handlerHasBeenReached.set(true);
                // OK The exception has been handled
            }
        });

        doFetch(pipes, handlers);

        /*
         * This is done this way because ExecutorServiceAsyncUriFetcher catches all the exceptions
         * and does nothing, it does not re thrown them.
         */
        assertTrue(handlerHasBeenReached.get());
    }

    /** Tests the exception flow */
    @Test
    public void testExceptionFlowWithoutExceptionHandler() {
        final Source xsltSource = new StreamSource(getClass().getClassLoader().getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"));
        final Action<Link, String> action = new Action<Link, String>() {
            @Override
            public String execute(final Link t) {
                throw new ArrayIndexOutOfBoundsException();
            }
        };
        final Closure<String> unreachableClosure = new Closure<String>() {
            @Override
            public void execute(final String input) {
                fail("The exception has not been catched. :(");
            }
        };
        final Collection<Pipe<?, ?>> pipes = Arrays.asList(new Pipe<?, ?>[] {
                new HTMLSanitizerPipe(),
                new XMLPipe(xsltSource),
                new ToJavaObjectPipe<Link>(Link.class),
                new ActionPipe<Link, String>(action),
                new ClosureAdapterPipe<String>(unreachableClosure),
        });

        final AtomicBoolean defaultHandleHasBeenReached = new AtomicBoolean(false);
        final ExceptionHandler defaultExceptionHandler = new ExceptionHandler() {
            @Override
            public void handle(final Throwable trowable) {
                assertNotNull(trowable);
                defaultHandleHasBeenReached.set(true);
                throw new UnhandledException(trowable);
            }
        };
        doFetch(pipes, defaultExceptionHandler);

        /*
         * This is done this way because ExecutorServiceAsyncUriFetcher catches all the exceptions
         * and does nothing, it does not re thrown them.
         */
        assertTrue(defaultHandleHasBeenReached.get());
    }


    /** Tests the flow with for each */
    @Test
    public void testFlowWithForEach() {
        final Source xsltSource = new StreamSource(getClass().getClassLoader().getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"));
        final Action<Link, String> action = new Action<Link, String>() {
            @Override
            public String execute(final Link t) {
                return t.getTitle();
            }
        };
        final Closure<String> assertClosure = new Closure<String>() {
            @Override
            public void execute(final String input) {
                assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", input);
            }
        };
        final List<String> categories = new ArrayList<String>(4);
        categories.add("Autos");
        categories.add("Ropa");
        categories.add("Motos");
        categories.add("Musica");
        final List<String> resultingCategories = new ArrayList<String>();
        final Pipe<String, ?> loopBodyPipe = new ClosureAdapterPipe<String>(new Closure<String>() {
            @Override
            public void execute(final String input) {
                resultingCategories.add(input);
            }
        });
        final Collection<Pipe<?, ?>> pipes = Arrays.asList(new Pipe<?, ?>[] {
                new HTMLSanitizerPipe(),
                new XMLPipe(xsltSource),
                new ToJavaObjectPipe<Link>(Link.class),
                new ForEachPipe<Link, String>("categories", loopBodyPipe),
                new ActionPipe<Link, String>(action),
                new ClosureAdapterPipe<String>(assertClosure),
        });

        doFetch(pipes);
        assertEquals(categories, resultingCategories);
    }


    /**
     * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
     * to the given chain of {@link Pipe}s
     *
     * @param pipes
     * @throws InterruptedException
     */
    private void doFetch(final Collection<Pipe<?, ?>> pipes) {
        doFetch(pipes, null, null);
    }

    /**
     * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
     * to the given chain of {@link Pipe}s
     *
     * @param pipes
     * @throws InterruptedException
     */
    @SuppressWarnings("rawtypes")
    private void doFetch(
            final Collection<Pipe<?, ?>> pipes,
            final Map<Class<? extends Throwable>, ExceptionHandler> handlers) {
        doFetch(pipes, handlers, null);
    }

    /**
     * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
     * to the given chain of {@link Pipe}s
     *
     * @param pipes
     * @throws InterruptedException
     */
    private void doFetch(
            final Collection<Pipe<?, ?>> pipes,
            final ExceptionHandler defaultExceptionHandler) {
        doFetch(pipes, null, defaultExceptionHandler);
    }

    /**
     * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
     * to the given chain of {@link Pipe}s
     *
     * @param pipes
     * @param handlers
     * @throws InterruptedException
     */
    private void doFetch(
            final Collection<Pipe<?, ?>> pipes,
            final Map<Class<? extends Throwable>, ExceptionHandler> handlers,
            final ExceptionHandler defaultExceptionHandler) {
        // Fetcher Configuration
        final URIFetcher httpClientFetcher = Fetchers.createFixed().register(mlhome, 
                "com/zaubersoftware/leviathan/api/engine/pages/homeml.html").build();
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final AsyncUriFetcher fetcher = new ExecutorServiceAsyncUriFetcher(executor);

        // Pipe chain
        final FlowBuilderPipe<URIFetcherResponse, Object> rootPipe =
            new FlowBuilderPipe<URIFetcherResponse, Object>(pipes, handlers);

        if (defaultExceptionHandler != null) {
            rootPipe.setDefaultExceptionHandler(defaultExceptionHandler);
        }

        final Closure<URIFetcherResponse> rootClosure = new FetcherResponsePipeAdapterClosure<Object>(rootPipe);

        fetcher.scheduleFetch(httpClientFetcher.createGet(mlhome), rootClosure);
        try {
            fetcher.awaitIdleness();
        } catch (final InterruptedException e) {
            // WTF!!!!
            throw new UnhandledException(e);
        }
    }
}
