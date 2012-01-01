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

import static GContextAwareClosure.*
import static org.junit.Assert.*

import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource

import org.junit.Before
import org.junit.BeforeClass;
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
import com.zaubersoftware.leviathan.api.engine.ControlStructureHanlder;
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
  
  @BeforeClass 
  static void setUpGroovySupport() {
      ActionHandler.mixin(ActionHandlerCategory)
      ErrorTolerant.mixin(ErrorTolerantCategory)
      ControlStructureHanlder.mixin(ControlStructureHanlderCategory)
  }

  @Before
  void setUp() {
    f = Fetchers.createFixed().register(mlhome,
      "com/zaubersoftware/leviathan/api/engine/pages/homeml.html").build()
    final ExecutorService executor = Executors.newSingleThreadExecutor()
    fetcher = new ExecutorServiceAsyncUriFetcher(executor)
    engine = Leviathan.flowBuilder()
  }


  @Test
  void shouldFetchAndDoSomethingWithAClosure() {
    def fetchPerformed = false
    def flow = engine
      .afterFetch()
      .then { URIFetcherResponse response ->
        assertTrue(response.succeeded)
        fetchPerformed = true
      }.pack()

    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert fetchPerformed, "Did not fetch!"
  }

  @Test
  void shouldFetchDoSomethingAndHandleTheExceptionWithoutConfiguredHandlers() {
    def exceptionHandled = false
    def exception = new MockException("an exception was thrown while processing the response!")
    def flow = engine
      .afterFetch()
      .then { throw exception }
      .onAnyExceptionDo {
        assert exception == it
        exceptionHandled = true
      }.pack()
    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert exceptionHandled, "Did not hadle the exception"
  }

  @Test
  void shouldFetchDoSomethingAndHandleTheExceptionWithAnSpecificHandler() {
    def exceptionHandled = false
    def exception = new MockException("an exception was thrown while processing the response!")
    def pack = engine
      .afterFetch()
      .then { throw exception } 
      .onExceptionHandleWith(MockException) { throwable ->
        assert exception == throwable
        exceptionHandled = true
      }.otherwiseHandleWith {
        fail("It should never reach here, the exception should be handled by the configured handler."
          + " Look above!!!")
      }.pack()
    fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
    assert exceptionHandled, "Did not hadle the exception"
  }

  @Test
  void shouldBindUriToAFlow() {
    def fetchPerformed = false
    def flow = engine
      .afterFetch()
      .then { URIFetcherResponse response ->
        assert response.succeeded
        fetchPerformed = true
      }.pack()

    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert fetchPerformed, "Did not fetch!"
  }

  @Test
  void shouldHaveContext() {
    final key = "FOO"
    final val = "VAL"

    def fetchPerformed = false
    def flow = engine
      .afterFetch()
      .then { URIFetcherResponse response ->
        assert response.succeeded
        assertEquals(val, get(key))
        fetchPerformed = true
      }.pack()

    def ctx = [(key): val]
    fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(mlhome, ctx)), flow).awaitIdleness()
    assert fetchPerformed, "Did not fetch!"
  }

  @Test
  void shouldHaveContextAndCanBeShareBetweenActions() {
    final KEY = "FOO"
    final VAL = "VAL"

    def fetchPerformed = false
    def flow = engine
      .afterFetch()
      .then { URIFetcherResponse response ->
        assert response.succeeded
        assertEquals(VAL, get(KEY))
        fetchPerformed = true
      }.pack()

    final ctx = [(KEY):VAL]
    fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(mlhome, ctx)), flow).awaitIdleness()
    assert fetchPerformed, "Did not fetch!"
  }

  @Test
  void shouldFlow() {
    final xsltSource = new StreamSource(getClass().classLoader.getResourceAsStream(
      "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"))
    def actionPerformed = false
    def pack = engine
      .afterFetch()
      .sanitizeHTML()
      .transformXML(xsltSource)
      .toJavaObject(Link)
      .thenDo { Link link -> actionPerformed = true;  link.title }
      .then { assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", it)  }
      .pack()
    fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
    assert actionPerformed, "Did not hadle the exception"
  }

  @Test
  void shouldForEachFlow() {
    final xsltSource = new StreamSource(getClass().classLoader.getResourceAsStream(
      "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"))
    def actionPerformed = false
    def timesRun = 0
    def flow = engine.afterFetch()
      .sanitizeHTML()
      .transformXML(xsltSource)
      .toJavaObject(Link)
      .thenDo { actionPerformed = true; it } 
      .forEachIn("categories") { ++timesRun }
      .then { assert 4 == timesRun }
      .pack()
    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert actionPerformed, "Did not hadle the exception"
  }
}
