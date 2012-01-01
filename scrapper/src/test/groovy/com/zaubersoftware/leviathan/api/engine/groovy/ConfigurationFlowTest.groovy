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
package com.zaubersoftware.leviathan.api.engine.groovy

import static org.junit.Assert.*

import java.net.URI
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource

import org.apache.commons.lang.UnhandledException
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import ar.com.zauber.commons.dao.Closure
import ar.com.zauber.leviathan.api.AsyncUriFetcher
import ar.com.zauber.leviathan.api.URIFetcher
import ar.com.zauber.leviathan.api.URIFetcherResponse
import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher
import ar.com.zauber.leviathan.common.FetcherResponsePipeAdapterClosure
import ar.com.zauber.leviathan.common.fluent.Fetchers

import com.zaubersoftware.leviathan.api.engine.Action
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler
import com.zaubersoftware.leviathan.api.engine.Pipe
import com.zaubersoftware.leviathan.api.engine.impl.dto.Link
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ActionPipe
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ClosureAdapterPipe
import com.zaubersoftware.leviathan.api.engine.impl.pipe.FlowBuilderPipe
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ForEachPipe
import com.zaubersoftware.leviathan.api.engine.impl.pipe.HTMLSanitizerPipe
import com.zaubersoftware.leviathan.api.engine.impl.pipe.ToJavaObjectPipe
import com.zaubersoftware.leviathan.api.engine.impl.pipe.XMLPipe

/**
 * Test driver for the configuration flow, using groovy 
 * support, based on {@link com.zaubersoftware.leviathan.api.engine.impl.ConfigurationFlowTest}
 *
 * @author flbulgarelli
 */
class ConfigurationFlowTest {
  def URI mlhome = URI.create("http://www.mercadolibre.com.ar/")

  /** Tests the flow */
  @Test
  void testFlow() {
    def xsltSource = classpathSource("com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl")
    def action = GAction.from { Link it -> it.title }
    def assertClosure = GClosure.from { assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", it) }

    doFetch([
      new HTMLSanitizerPipe(),
      new XMLPipe(xsltSource),
      new ToJavaObjectPipe(Link),
      new ActionPipe(action),
      new ClosureAdapterPipe(assertClosure),
    ])
  }

  /** Tests the exception flow */ 
  @Test
  void testExceptionFlowWithExceptionHandler() {
    def xsltSource = classpathSource("com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl")
    def action = GAction.from {  throw new ArrayIndexOutOfBoundsException() }
    def unreachableClosure = GClosure.from { fail("The exception has not been catched. :(") }

    def pipes = [
      new HTMLSanitizerPipe(),
      new XMLPipe(xsltSource),
      new ToJavaObjectPipe(Link),
      new ActionPipe(action),
      new ClosureAdapterPipe(unreachableClosure)
    ]

    def handlerHasBeenReached = false
    def handlers = [:]
    handlers.put(ArrayIndexOutOfBoundsException,
        GExceptionHandler.from { Throwable throwable ->
          assert throwable != null
          handlerHasBeenReached = true
          // OK The exception has been handled
        })

    doFetch(pipes, handlers)

    /*
     * This is done this way because ExecutorServiceAsyncUriFetcher catches all the exceptions
     * and does nothing, it does not re thrown them.
     */
    assert handlerHasBeenReached
  }

  /** Tests the exception flow */
  @Test
  void testExceptionFlowWithoutExceptionHandler() {
    def Source xsltSource = classpathSource("com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl")
    def throwExceptionAction = GAction.from {  throw new ArrayIndexOutOfBoundsException() }
    def unreachableClosure = GClosure.from { fail("The exception has not been catched. :(") }

    def pipes = [
      new HTMLSanitizerPipe(),
      new XMLPipe(xsltSource),
      new ToJavaObjectPipe(Link),
      new ActionPipe(throwExceptionAction),
      new ClosureAdapterPipe(unreachableClosure),
    ]

    def defaultHandleHasBeenReached = false
    def ExceptionHandler defaultExceptionHandler = GExceptionHandler.from {
      assert it != null
      defaultHandleHasBeenReached = true
      throw it
    }
    doFetch(pipes, defaultExceptionHandler)

    /*
     * This is done this way because ExecutorServiceAsyncUriFetcher catches all the exceptions
     * and does nothing, it does not re thrown them.
     */
    assert defaultHandleHasBeenReached
  }


  /** Tests the flow with for each */
  @Test
  void testFlowWithForEach() {
    def Source xsltSource = classpathSource("com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl")
    def action = GAction.from { Link it -> it.title }
    def assertClosure = GClosure.from { assertEquals("MercadoLibre Argentina - Donde comprar y vender de todo.", it)  }
    def categories = [
      "Autos",
      "Ropa",
      "Motos",
      "Musica"
    ]
    def List<String> resultingCategories = []
    def loopBodyPipe = new ClosureAdapterPipe (
        GClosure.from { String it -> resultingCategories << it })

    doFetch([
      new HTMLSanitizerPipe(),
      new XMLPipe(xsltSource),
      new ToJavaObjectPipe(Link),
      new ForEachPipe("categories", loopBodyPipe),
      new ActionPipe(action),
      new ClosureAdapterPipe(assertClosure),
    ])
    assert categories == resultingCategories
  }


  /**
   * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
   * to the given chain of {@link Pipe}s
   *
   * @param pipes
   * @throws InterruptedException
   */
  def doFetch(Collection pipes) {
    doFetch(pipes, null, null)
  }

  /**
   * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
   * to the given chain of {@link Pipe}s
   *
   * @param pipes
   * @throws InterruptedException
   */
  def doFetch(Collection pipes, Map handlers) {
    doFetch(pipes, handlers, null)
  }

  /**
   * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
   * to the given chain of {@link Pipe}s
   *
   * @param pipes
   * @throws InterruptedException
   */
  def doFetch(Collection pipes, ExceptionHandler defaultExceptionHandler) {
    doFetch(pipes, null, defaultExceptionHandler)
  }

  /**
   * Sets up an {@link AsyncUriFetcher} and performs the fetch and pass the {@link URIFetcherResponse}
   * to the given chain of {@link Pipe}s
   *
   * @param pipes
   * @param handlers
   * @throws InterruptedException
   */
  def doFetch(Collection pipes, Map handlers, ExceptionHandler defaultExceptionHandler) {
    // Fetcher Configuration
    def httpClientFetcher = Fetchers.createFixed().register(mlhome,
        "com/zaubersoftware/leviathan/api/engine/pages/homeml.html").build()
    def fetcher = new ExecutorServiceAsyncUriFetcher(Executors.newSingleThreadExecutor())

    // Pipe chain
    def rootPipe = new FlowBuilderPipe(pipes, handlers)

    if (defaultExceptionHandler != null) {
      rootPipe.setDefaultExceptionHandler(defaultExceptionHandler)
    }

    def rootClosure = new FetcherResponsePipeAdapterClosure(rootPipe)

    fetcher.scheduleFetch(httpClientFetcher.createGet(mlhome), rootClosure)
    fetcher.awaitIdleness()
  }

  def classpathSource = {name -> new StreamSource(new ClassPathResource(name).inputStream )}
}
