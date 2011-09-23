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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;

import com.zaubersoftware.leviathan.api.engine.ActionHandler;
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ErrorTolerantActionHandler;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.LeviathanBuilder;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;


/**
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public class InstantiationFlowTest {

    private final URI mlhome = URI.create("http://www.mercadolibre.com.ar/");
    private LeviathanBuilder leviathan;
    private AsyncUriFetcher fetcher;
    private Engine engine;


    @Before
    public void setUp() {
        final Map<URI, String> pages = new HashMap<URI, String>();
        pages.put(this.mlhome, "com/zaubersoftware/leviathan/api/engine/pages/homeml.html");

        final URIFetcher httpClientFetcher = new FixedURIFetcher(pages);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        this.fetcher = new ExecutorServiceAsyncUriFetcher(executor, httpClientFetcher);

        this.leviathan = new DefaultLeviathanBuilder();
        this.engine = this.leviathan
            .withAsyncURIFetcher(this.fetcher)
            .build();
    }




    @Test
    public void shouldFetchAndDoSomethingWithAClosure() throws Exception {
        final AtomicBoolean fetchPerformed = new AtomicBoolean(false);
        this.engine.forUri(this.mlhome).then(new ContextAwareClosure<URIFetcherResponse>() {
            @Override
            public void execute(final URIFetcherResponse response) {
                assertTrue(response.isSucceeded());
                fetchPerformed.set(true);
            }
        }).pack();

        this.engine.doGet(this.mlhome).awaitIdleness();
        assertTrue("Did not fetch!", fetchPerformed.get());

    }

    @Test
    public void shouldFetchDoSomethingAndHandleTheExceptionWithoutConfiguredHandlers() throws Exception {
        final AtomicBoolean exceptionHandled = new AtomicBoolean(false);
        final RuntimeException exception = new MockException("an exception was thrown while processing the response!");
        this.engine.forUri(this.mlhome).then(new ContextAwareClosure<URIFetcherResponse>() {
            @Override
            public void execute(final URIFetcherResponse response) {
                throw exception;
            }
        }).onAnyExceptionDo(new ExceptionHandler<Throwable>() {
            @Override
            public void handle(final Throwable trowable) {
                exceptionHandled.set(true);
                assertEquals(exception, trowable);
            }
        }).pack();
        this.engine.doGet(this.mlhome).awaitIdleness();
        assertTrue("Did not hadle the exception", exceptionHandled.get());
    }

    @Test
    public void shouldFetchDoSomethingAndHandleTheExceptionWithAnSpecificHandler() throws Exception {
        final AtomicBoolean exceptionHandled = new AtomicBoolean(false);
        final RuntimeException exception = new MockException("an exception was thrown while processing the response!");
        this.engine.forUri(this.mlhome).then(new ContextAwareClosure<URIFetcherResponse>() {
            @Override
            public void execute(final URIFetcherResponse response) {
                throw exception;
            }
        }).on(MockException.class).handleWith(new ExceptionHandler<MockException>() {
            @Override
            public void handle(final MockException trowable) {
                exceptionHandled.set(true);
                assertEquals(exception, trowable);
            }
        }).otherwiseHandleWith(new ExceptionHandler<Throwable>() {
            @Override
            public void handle(final Throwable trowable) {
                fail("It should never reach here, the exception should be handled by the configured handler. Look above!!!");
            }
        }).pack();
        this.engine.doGet(this.mlhome).awaitIdleness();
        assertTrue("Did not hadle the exception", exceptionHandled.get());
    }

    @Test
    public void testname() throws Exception {
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

        this.engine.bindURI(this.mlhome).toFlow(flow);
        assertTrue("Did not fetch!", fetchPerformed.get());

        // TODO IMPLMENET THIS!!!!
        final ErrorTolerantActionHandler<ActionHandler<?>> a = null;
        a.on(null).handleWith(null).on(null).handleWith(null).otherwiseHandleWith(null).pack();

    }






}
