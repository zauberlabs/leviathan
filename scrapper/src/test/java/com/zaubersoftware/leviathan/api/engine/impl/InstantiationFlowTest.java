/**
 * Copyright (c) 2009-2012 Zauber S.A. <http://zauberlabs.com/>
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;

import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;
import ar.com.zauber.leviathan.common.fluent.Fetchers;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;

import com.zaubersoftware.leviathan.api.engine.Action;
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.Leviathan;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.impl.dto.Link;


/**
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class InstantiationFlowTest {

    private final URI mlhome = URI.create("http://www.mercadolibre.com.ar/");
    private AsyncUriFetcher fetcher;
    private Engine engine;
    private URIFetcher f;

    @Before
    public void setUp() {
        final Map<URI, String> pages = new HashMap<URI, String>();
        f = Fetchers.createFixed().register(mlhome, 
                "com/zaubersoftware/leviathan/api/engine/pages/homeml.html").build();
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        this.fetcher = new ExecutorServiceAsyncUriFetcher(executor);

        this.engine = Leviathan.flowBuilder();
    }




    @Test
    public void shouldFetchAndDoSomethingWithAClosure() throws Exception {
        final AtomicBoolean fetchPerformed = new AtomicBoolean(false);
        final ProcessingFlow flow = this.engine.afterFetch().then(new ContextAwareClosure<URIFetcherResponse>() {
            @Override
            public void execute(final URIFetcherResponse response) {
                assertTrue(response.isSucceeded());
                fetchPerformed.set(true);
            }
        }).pack();

        this.fetcher.scheduleFetch(f.createGet(this.mlhome), flow).awaitIdleness();
        assertTrue("Did not fetch!", fetchPerformed.get());

    }

    @Test
    public void shouldFetchDoSomethingAndHandleTheExceptionWithoutConfiguredHandlers() throws Exception {
        final AtomicBoolean exceptionHandled = new AtomicBoolean(false);
        final RuntimeException exception = 
                new MockException("an exception was thrown while processing the response!");
        ProcessingFlow flow = this.engine.afterFetch().then(new ContextAwareClosure<URIFetcherResponse>() {
            @Override
            public void execute(final URIFetcherResponse response) {
                throw exception;
            }
        }).onAnyExceptionDo(new ExceptionHandler() {
            @Override
            public void handle(final Throwable trowable) {
                exceptionHandled.set(true);
                assertEquals(exception, trowable);
            }
        }).pack();
        fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness();
        assertTrue("Did not hadle the exception", exceptionHandled.get());
    }

    @Test
    public void shouldFetchDoSomethingAndHandleTheExceptionWithAnSpecificHandler() throws Exception {
        final AtomicBoolean exceptionHandled = new AtomicBoolean(false);
        final RuntimeException exception = 
                new MockException("an exception was thrown while processing the response!");
        ProcessingFlow pack = this.engine.afterFetch().then(new ContextAwareClosure<URIFetcherResponse>() {
            @Override
            public void execute(final URIFetcherResponse response) {
                throw exception;
            }
        }).on(MockException.class).handleWith(new ExceptionHandler() {
            @Override
            public void handle(final Throwable trowable) {
                exceptionHandled.set(true);
                assertEquals(exception, trowable);
            }
        }).otherwiseHandleWith(new ExceptionHandler() {
            @Override
            public void handle(final Throwable trowable) {
                fail("It should never reach here, the exception should be handled by the configured handler."
                        + " Look above!!!");
            }
        }).pack();
        this.fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness();
        assertTrue("Did not hadle the exception", exceptionHandled.get());
    }

    @Test
    public void shouldBindUriToAFlow() throws Exception {
        final AtomicBoolean fetchPerformed = new AtomicBoolean(false);
        final ProcessingFlow flow = this.engine
            .afterFetch()
            .then(new ContextAwareClosure<URIFetcherResponse>() {
                @Override
                public void execute(final URIFetcherResponse response) {
                    assertTrue(response.isSucceeded());
                    fetchPerformed.set(true);
                }
            })
            .pack();

        fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness();
        assertTrue("Did not fetch!", fetchPerformed.get());
    }

    @Test
    public void shouldHaveContext() throws Exception {
        final String key = "FOO";
        final String val = "VAL";

        final AtomicBoolean fetchPerformed = new AtomicBoolean(false);
        final ProcessingFlow flow = this.engine
            .afterFetch()
            .then(new ContextAwareClosure<URIFetcherResponse>() {
                @Override
                public void execute(final URIFetcherResponse response) {
                    assertTrue(response.isSucceeded());
                    assertEquals(val, get(key));
                    fetchPerformed.set(true);
                }
            })
            .pack();

        final Map<String, Object> ctx = new HashMap<String, Object>();
        ctx.put(key, val);
        fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(this.mlhome, ctx)), flow).awaitIdleness();
        assertTrue("Did not fetch!", fetchPerformed.get());
    }

    @Test
    public void shouldHaveContextAndCanBeShareBetweenActions() throws Exception {
        final String KEY = "FOO";
        final String VAL = "VAL";

        final AtomicBoolean fetchPerformed = new AtomicBoolean(false);
        final ProcessingFlow flow = this.engine
            .afterFetch()
            .then(new ContextAwareClosure<URIFetcherResponse>() {
                @Override
                public void execute(final URIFetcherResponse response) {
                    assertTrue(response.isSucceeded());
                    assertEquals(VAL, get(KEY));
                    fetchPerformed.set(true);
                }
            })
            .pack();

        final Map<String, Object> ctx = new HashMap<String, Object>();
        ctx.put(KEY, VAL);
        fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(this.mlhome, ctx)), flow).awaitIdleness();
        assertTrue("Did not fetch!", fetchPerformed.get());
    }

    @Test
    public void shouldFlow() throws Exception {
        final Source xsltSource = new StreamSource(getClass().getClassLoader().getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"));
        final AtomicBoolean actionPerformed = new AtomicBoolean(false);
        final ProcessingFlow pack = this.engine
            .afterFetch()
            .sanitizeHTML()
            .transformXML(xsltSource)
            .toJavaObject(Link.class)
            .then(new Action<Link, String>() {
                @Override
                public String execute(final Link link) {
                    actionPerformed.set(true);
                    return link.getTitle();
                }
            })
            .then(new ContextAwareClosure<String>() {
                @Override
                public void execute(final String input) {
                    assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", input);
                }
            })
            .pack();
        fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness();
        assertTrue("Did not hadle the exception", actionPerformed.get());
    }

    @Test
    public void shouldForEachFlow() throws Exception {
        final Source xsltSource = new StreamSource(getClass().getClassLoader().getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"));
        final AtomicBoolean actionPerformed = new AtomicBoolean(false);
        final AtomicInteger count = new AtomicInteger(0);
        final ProcessingFlow pack = this.engine.afterFetch()
            .sanitizeHTML()
            .transformXML(xsltSource)
            .toJavaObject(Link.class)
            .then(new Action<Link, Link>() {
                @Override
                public Link execute(final Link link) {
                    actionPerformed.set(true);
                    return link;
                }
            })
            .forEach(String.class).in("categories")
                .then(new ContextAwareClosure<String>() {
                    @Override
                    public void execute(final String category) {
                        count.incrementAndGet();
                    }
                })
            .endFor()
            .then(new ContextAwareClosure<Link>() {
                @Override
                public void execute(final Link link) {
                    assertEquals(4, count.get());
                }
            })
            .pack();
        fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness();
        assertTrue("Did not hadle the exception", actionPerformed.get());
    }
}
