package com.zaubersoftware.leviathan.api.engine.groovy
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
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.zaubersoftware.leviathan.api.engine.groovy.GLeviathan.*
import static org.junit.Assert.*

import java.net.URI
import java.util.concurrent.Executors

import javax.xml.transform.stream.StreamSource

import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import ar.com.zauber.leviathan.api.AsyncUriFetcher
import ar.com.zauber.leviathan.api.URIFetcher
import ar.com.zauber.leviathan.api.URIFetcherResponse
import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher
import ar.com.zauber.leviathan.common.InmutableURIAndCtx
import ar.com.zauber.leviathan.common.fluent.Fetchers

import com.zaubersoftware.leviathan.api.engine.Engine
import com.zaubersoftware.leviathan.api.engine.impl.MockException
import com.zaubersoftware.leviathan.api.engine.impl.dto.Link


/**
 * @author Martin Silva
 * @since Sep 2, 2011
 */
final class LocalInstantiationFlowTest {

  final URI mlhome = URI.create('http://www.mercadolibre.com.ar/')
  AsyncUriFetcher fetcher
  URIFetcher f
  
  @Before
  void setUp() {
    f = Fetchers.createFixed().register(mlhome,
      'com/zaubersoftware/leviathan/api/engine/pages/homeml.html').build()
    def executor = Executors.newSingleThreadExecutor()
    fetcher = new ExecutorServiceAsyncUriFetcher(executor)
  }

  @Test
  void 'Should Fetch And DoSomething With A Closure'() {
    def fetchPerformed = false
    def flow = withEngine { Engine e -> e
      .afterFetch()
      .then { URIFetcherResponse response ->
        assertTrue(response.succeeded)
        fetchPerformed = true
      }
    }
    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert fetchPerformed, 'Did not fetch!'
  }

  @Test
  void 'Should Fetch Do Something And Handle The Exception Without Configured Handlers'() {
    def exceptionHandled = false
    def exception = new MockException('an exception was thrown while processing the response!')
    def flow = withEngine { Engine e -> e
      .afterFetch()
      .then { throw exception } 
      .onAnyExceptionDo {
        assert exception == it
        exceptionHandled = true
      }
    }
    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert exceptionHandled, 'Did not hadle the exception'
  }

  @Test
  void 'Should Fetch Do Something And Handle The Exception With An Specific Handler'() {
      def exceptionHandled = false
      def exception = new MockException('an exception was thrown while processing the response!')
      def flow = withEngine { Engine e -> e
          .afterFetch()
          .then { throw exception }
          .onExceptionHandleWith(MockException) { throwable ->
            assert exception == throwable
            exceptionHandled = true
          }.otherwiseHandleWith {
            fail('It should never reach here, the exception should be handled by the configured handler.'
                + ' Look above!!!')
          }
      }
      fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
      assert exceptionHandled, 'Did not hadle the exception'
  }

  @Test
  void 'Should Bind Uri To A Flow'() {
    def fetchPerformed = false
    def flow = withEngine { Engine e -> e
      .afterFetch()
      .then { URIFetcherResponse response ->
        assert response.succeeded
        fetchPerformed = true
      }
    }
    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert fetchPerformed, 'Did not fetch!'
  }

  @Test
  void 'Should Have Context'() {
    final key = 'FOO'
    final val = 'VAL'

    def fetchPerformed = false
    def flow = withEngine { Engine e -> e
      .afterFetch()
      .then { URIFetcherResponse response ->
        assert response.succeeded
        assertEquals(val, get(key))
        fetchPerformed = true
      }
    }
    def ctx = [(key): val]
    fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(mlhome, ctx)), flow).awaitIdleness()
    assert fetchPerformed, 'Did not fetch!'
  }

  @Test
  void 'Should Have Context And Can Be Share Between Actions'() {
    final KEY = 'FOO'
    final VAL = 'VAL'

    def fetchPerformed = false
    def flow = withEngine { Engine e -> e
      .afterFetch()
      .then { URIFetcherResponse response ->
        assert response.succeeded
        assertEquals(VAL, get(KEY))
        fetchPerformed = true
      }
    }

    final ctx = [(KEY):VAL]
    fetcher.scheduleFetch(f.createGet(new InmutableURIAndCtx(mlhome, ctx)), flow).awaitIdleness()
    assert fetchPerformed, 'Did not fetch!'
  }

  @Test
  void 'Should Flow'() {
    def xsltSource = classpathSource('com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl')
    def actionPerformed = false
    def pack = withEngine { Engine e -> e
      .afterFetch()
      .sanitizeHTML()
      .transformXML(xsltSource)
      .toJavaObject(Link)
      .thenDo { Link link -> actionPerformed = true;  link.title }
      .then { assertEquals('MercadoLibre Argentina - Donde comprar y vender de todo.', it)  }
    }
    fetcher.scheduleFetch(f.createGet(mlhome), pack).awaitIdleness()
    assert actionPerformed, 'Did not hadle the exception'
  }

  @Test
  void 'Should For Each Flow'() {
    def xsltSource = classpathSource('com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl')
    def actionPerformed = false
    def timesRun = 0
    def flow = withEngine { Engine e -> e
      .afterFetch()
      .sanitizeHTML()
      .transformXML(xsltSource)
      .toJavaObject(Link)
      .thenDo { actionPerformed = true; it }
      .forEachIn('categories') { ++timesRun }
      .then { assert 4 == timesRun }
    }
    fetcher.scheduleFetch(f.createGet(mlhome), flow).awaitIdleness()
    assert actionPerformed, 'Did not hadle the exception'
  }
  
  def classpathSource = {name -> new StreamSource(new ClassPathResource(name).inputStream )}
}