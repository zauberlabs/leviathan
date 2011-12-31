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
import static ActionHandlerCategory.*
import static org.junit.Assert.*

import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource

import org.junit.Before
import org.junit.Test

import ar.com.zauber.leviathan.api.AsyncUriFetcher
import ar.com.zauber.leviathan.api.URIFetcher
import ar.com.zauber.leviathan.api.URIFetcherResponse
import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher
import ar.com.zauber.leviathan.common.InmutableURIAndCtx
import ar.com.zauber.leviathan.common.fluent.Fetchers
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher

import com.sun.xml.internal.txw2.output.StreamSerializer
import com.zaubersoftware.leviathan.api.engine.Action
import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.AfterExceptionCatchDefinition
import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler
import com.zaubersoftware.leviathan.api.engine.AfterHandleWith
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure
import com.zaubersoftware.leviathan.api.engine.Engine
import com.zaubersoftware.leviathan.api.engine.ErrorTolerant
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler
import com.zaubersoftware.leviathan.api.engine.Leviathan
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow
import com.zaubersoftware.leviathan.api.engine.impl.dto.Link


/**
 * @author Martin Silva
 * @since Sep 2, 2011
 */
final class GroovyInstantiationFlowTest {

  final URI mlhome = URI.create("http://www.mercadolibre.com.ar/")
  AsyncUriFetcher fetcher
  Engine engine
  URIFetcher f

  def action(aBlock) {
    new Action() {
        def execute( arg0) {
          aBlock(arg0)
        }
      }
  }

  @Before
  void setUp() {
    f = Fetchers.createFixed().register(mlhome,
      "com/zaubersoftware/leviathan/api/engine/pages/homeml.html").build()
    final ExecutorService executor = Executors.newSingleThreadExecutor()
    this.fetcher = new ExecutorServiceAsyncUriFetcher(executor)

    this.engine = Leviathan.flowBuilder()
    ActionHandler.mixin(ActionHandlerCategory)
    ErrorTolerant.mixin(ErrorTolerantCategory)
  }


  @Test
  void shouldFetchAndDoSomethingWithAClosure() {
    final fetchPerformed = new AtomicBoolean(false)
    final flow = this.engine
      .afterFetch()
      .then { URIFetcherResponse response ->
        assertTrue(response.isSucceeded())
        fetchPerformed.set(true)
      }.pack()

    this.fetcher.scheduleFetch(f.createGet(this.mlhome), flow).awaitIdleness()
    assertTrue("Did not fetch!", fetchPerformed.get())
  }

  @Test
  void shouldFetchDoSomethingAndHandleTheExceptionWithoutConfiguredHandlers() {
    final exceptionHandled = new AtomicBoolean(false)
    final exception = new MockException("an exception was thrown while processing the response!")
    ProcessingFlow flow = this.engine
      .afterFetch()
      .then { _ -> throw exception }
      .onAnyExceptionDo {
        exceptionHandled.set(true)
        assertEquals(exception, it )
      }.pack()
    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assertTrue("Did not hadle the exception", exceptionHandled.get())
  }

  @Test
  void shouldFetchDoSomethingAndHandleTheExceptionWithAnSpecificHandler() {
    final exceptionHandled = new AtomicBoolean(false)
    final exception = new MockException("an exception was thrown while processing the response!")
    ProcessingFlow pack = this.engine
      .afterFetch()
      .then  { _ -> throw exception }
      .on(MockException)
      .handleWith { throwable ->
        exceptionHandled.set(true)
        assertEquals(exception, throwable)
      }.otherwiseHandleWith { _ ->
        fail("It should never reach here, the exception should be handled by the configured handler."
          + " Look above!!!")
      }.pack()
    this.fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
    assertTrue("Did not hadle the exception", exceptionHandled.get())
  }

  @Test
  void shouldBindUriToAFlow() {
    final fetchPerformed = new AtomicBoolean(false)
    final ProcessingFlow flow = this.engine
      .afterFetch()
      .then {  URIFetcherResponse response ->
        assertTrue(response.succeeded)
        fetchPerformed.set(true)
      }.pack()

    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assertTrue("Did not fetch!", fetchPerformed.get())
  }

  @Test
  void shouldHaveContext() {
    final key = "FOO"
    final val = "VAL"

    final fetchPerformed = new AtomicBoolean(false)
    final flow = this.engine
      .afterFetch()
      .then { URIFetcherResponse response ->
        assertTrue(response.succeeded)
        assertEquals(val, get(key))
        fetchPerformed.set(true)
      }.pack()

    final ctx = [(key): val]
    fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(this.mlhome, ctx)), flow).awaitIdleness()
    assertTrue("Did not fetch!", fetchPerformed.get())
  }

  @Test
  void shouldHaveContextAndCanBeShareBetweenActions() {
    final KEY = "FOO"
    final VAL = "VAL"

    final fetchPerformed = new AtomicBoolean(false)
    final flow = this.engine
      .afterFetch()
      .then { URIFetcherResponse response ->
        assertTrue(response.isSucceeded())
        assertEquals(VAL, get(KEY))
        fetchPerformed.set(true)
      }.pack()

    final ctx = [(KEY):VAL]
    fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(this.mlhome, ctx)), flow).awaitIdleness()
    assertTrue("Did not fetch!", fetchPerformed.get())
  }

  @Test
  void shouldFlow() {
    final xsltSource = new StreamSource(getClass().classLoader.getResourceAsStream(
      "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"))
    final actionPerformed = new AtomicBoolean(false)
    final ProcessingFlow pack = this.engine
      .afterFetch()
      .sanitizeHTML()
      .transformXML(xsltSource)
      .toJavaObject(Link)
      .then(action { Link link -> actionPerformed.set(true);  link.title })
      .then { assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", it ) }
      .pack()
    fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
    assertTrue("Did not hadle the exception", actionPerformed.get())
  }

  @Test
  void shouldForEachFlow() {
    final xsltSource = new StreamSource(getClass().classLoader.getResourceAsStream(
      "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"))
    final actionPerformed = new AtomicBoolean(false)
    final count = new AtomicInteger(0)
    final ProcessingFlow pack = this.engine.afterFetch()
      .sanitizeHTML()
      .transformXML(xsltSource)
      .toJavaObject(Link)
      .then(action { actionPerformed.set(true); it })
      .forEach(String).in("categories")
      .then( contextAware { _ -> count.incrementAndGet() })
      .endFor()
      .then { _ -> assertEquals(4, count.get()) }
      .pack()
    fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
    assertTrue("Did not hadle the exception", actionPerformed.get())
  }
}
