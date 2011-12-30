package com.zaubersoftware.leviathan.api.engine.impl
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

import com.sun.xml.internal.txw2.output.StreamSerializer;
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
final class GroovyInstantiationFlowTest {

    final URI mlhome = URI.create("http://www.mercadolibre.com.ar/")
    AsyncUriFetcher fetcher
    Engine engine
    URIFetcher f
	
	def contextAware(aBlock) {
		new ContextAwareClosure(){
			void execute(arg0) { aBlock(arg0) }
		}
	}
	
	def exceptionHandler(aBlock) {
		new ExceptionHandler() {
			void handle(Throwable arg0) { aBlock(arg0) }
		}
	}
	
	def action(aBlock) {
		new Action() {
			def execute( arg0) { aBlock(arg0) }
		}
	}

    @Before
    void setUp() {
        final Map<URI, String> pages = new HashMap<URI, String>()
        f = Fetchers.createFixed().register(mlhome, 
                "com/zaubersoftware/leviathan/api/engine/pages/homeml.html").build()
        final ExecutorService executor = Executors.newSingleThreadExecutor()
        this.fetcher = new ExecutorServiceAsyncUriFetcher(executor)

        this.engine = Leviathan.flowBuilder()
    }


    @Test
    void shouldFetchAndDoSomethingWithAClosure() {
        final AtomicBoolean fetchPerformed = new AtomicBoolean(false)
        final ProcessingFlow flow = this.engine
			.afterFetch()
			.then( contextAware { URIFetcherResponse response ->
                assertTrue(response.isSucceeded())
                fetchPerformed.set(true)
			})
			.pack()

        this.fetcher.scheduleFetch(f.createGet(this.mlhome), flow).awaitIdleness()
        assertTrue("Did not fetch!", fetchPerformed.get())

    }

    @Test
    void shouldFetchDoSomethingAndHandleTheExceptionWithoutConfiguredHandlers() {
        final AtomicBoolean exceptionHandled = new AtomicBoolean(false)
        final RuntimeException exception = 
                new MockException("an exception was thrown while processing the response!")
        ProcessingFlow flow = this.engine
			.afterFetch()
			.then( contextAware { _ -> throw exception})
			.onAnyExceptionDo( exceptionHandler { Throwable trowable ->
                exceptionHandled.set(true)
                assertEquals(exception, trowable)
	        }).pack()
        fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
        assertTrue("Did not hadle the exception", exceptionHandled.get())
    }

    @Test
    void shouldFetchDoSomethingAndHandleTheExceptionWithAnSpecificHandler() {
        final AtomicBoolean exceptionHandled = new AtomicBoolean(false)
        final RuntimeException exception = 
                new MockException("an exception was thrown while processing the response!")
        ProcessingFlow pack = this.engine
			.afterFetch()
			.then( contextAware { _ -> throw exception } )
			.on(MockException.class)
			.handleWith( exceptionHandler { throwable ->
                exceptionHandled.set(true)
                assertEquals(exception, throwable)
			})
			.otherwiseHandleWith(exceptionHandler { _ -> 
                fail("It should never reach here, the exception should be handled by the configured handler."
                        + " Look above!!!")
            })
			.pack()
        this.fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
        assertTrue("Did not hadle the exception", exceptionHandled.get())
    }

    @Test
    void shouldBindUriToAFlow() {
        final AtomicBoolean fetchPerformed = new AtomicBoolean(false)
        final ProcessingFlow flow = this.engine
            .afterFetch()
            .then(contextAware { 
				URIFetcherResponse response ->
                    assertTrue(response.isSucceeded())
                    fetchPerformed.set(true)
            })
            .pack()

        fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
        assertTrue("Did not fetch!", fetchPerformed.get())
    }

    @Test
    void shouldHaveContext() {
        final String key = "FOO"
        final String val = "VAL"

        final AtomicBoolean fetchPerformed = new AtomicBoolean(false)
        final ProcessingFlow flow = this.engine
            .afterFetch()
            .then(new ContextAwareClosure<URIFetcherResponse>() {
                @Override
                void execute(final URIFetcherResponse response) {
                    assertTrue(response.isSucceeded())
                    assertEquals(val, get(key))
                    fetchPerformed.set(true)
                }
            })
            .pack()

        final Map<String, Object> ctx = new HashMap<String, Object>()
        ctx.put(key, val)
        fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(this.mlhome, ctx)), flow).awaitIdleness()
        assertTrue("Did not fetch!", fetchPerformed.get())
    }

    @Test
    void shouldHaveContextAndCanBeShareBetweenActions() {
        final String KEY = "FOO"
        final String VAL = "VAL"

        final AtomicBoolean fetchPerformed = new AtomicBoolean(false)
        final ProcessingFlow flow = this.engine
            .afterFetch()
            .then(new ContextAwareClosure<URIFetcherResponse>() {
                @Override
                void execute(final URIFetcherResponse response) {
                    assertTrue(response.isSucceeded())
                    assertEquals(VAL, get(KEY))
                    fetchPerformed.set(true)
                }
            })
            .pack()

        final Map<String, Object> ctx = new HashMap<String, Object>()
        ctx.put(KEY, VAL)
        fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(this.mlhome, ctx)), flow).awaitIdleness()
        assertTrue("Did not fetch!", fetchPerformed.get())
    }

    @Test
    void shouldFlow() {
        final Source xsltSource = new StreamSource(getClass().classLoader.getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"))
        final AtomicBoolean actionPerformed = new AtomicBoolean(false)
        final ProcessingFlow pack = this.engine
            .afterFetch()
            .sanitizeHTML()
            .transformXML(xsltSource)
            .toJavaObject(Link.class)
            .then(action { Link link -> actionPerformed.set(true);  link.title })
            .then(contextAware { assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", it ) })
            .pack()
        fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
        assertTrue("Did not hadle the exception", actionPerformed.get())
    }

    @Test
    void shouldForEachFlow() {
        final Source xsltSource = new StreamSource(getClass().classLoader.getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"))
        final AtomicBoolean actionPerformed = new AtomicBoolean(false)
        final AtomicInteger count = new AtomicInteger(0)
        final ProcessingFlow pack = this.engine.afterFetch()
            .sanitizeHTML()
            .transformXML(xsltSource)
            .toJavaObject(Link.class)
            .then(action { Link link -> actionPerformed.set(true); link })
            .forEach(String.class).in("categories")
                .then( contextAware { _ -> count.incrementAndGet() })
            .endFor()
            .then(contextAware { _ -> assertEquals(4, count.get()) })
            .pack()
        fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
        assertTrue("Did not hadle the exception", actionPerformed.get())
    }
}
