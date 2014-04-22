/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.dao.closure.MutableClosure;
import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;


/**
 * Tests class for the {@link RedirectClosure} class
 *
 * @author Guido Marucci Blas
 * @since Feb 10, 2011
 */
public class RedirectClosureTest {
    private final URI uri1;
    {
        try {
            uri1 = new URI("http://www.foo1.com");
        } catch (final URISyntaxException e) {
            throw new UnhandledException(e);
        }
    }
    private final URI uri2;
    {
        try {
            uri2 = new URI("http://www.foo2.com");
        } catch (final URISyntaxException e) {
            throw new UnhandledException(e);
        }
    }
    
    

    private final Closure <URIFetcherResponse> failClousure = 
        new Closure<URIFetcherResponse>() {

        @Override
        public void execute(final URIFetcherResponse t) {
            fail("should not reach");
        }

    };

    /**
     * Tests that when a 200 is returned as the status code
     * as the first hit the target closure is called.
     */
    @Test
    public final void testNoRedirect() {
        final URIFetcher fetcher = new AbstractRedirectMockFetcher() {
            @Override
            public FetchingTask createGet(final URIAndCtx uriAndCtx) {
                return new FetchingTask() {
                    @Override
                    public URIAndCtx getURIAndCtx() {
                        return uriAndCtx;
                    }
                    
                    @Override
                    public URIFetcherResponse execute() {
                        return new InmutableURIFetcherResponse(
                                uriAndCtx,
                                new InmutableURIFetcherHttpResponse("", 301, 
                                        Collections.EMPTY_MAP));
                    }
                };
            }
        };
        
        final AtomicInteger i = new AtomicInteger();
        final Closure<URIFetcherResponse> ret = new RedirectClosure(
                new NotsoAsyncUriFetcher(), fetcher,
                new Closure<URIFetcherResponse>() {
                    @Override
                    public void execute(final URIFetcherResponse t) {
                        assertEquals(uri1, t.getURIAndCtx().getURI());
                        i.incrementAndGet();
                    }

        }, failClousure, failClousure, 5);


        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        headers.put("locAtiOn", Arrays.asList("http://www.foo1.com"));
        ret.execute(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri1),
                new InmutableURIFetcherHttpResponse("", 200, headers)));
        assertEquals(1, i.get());
    }
    
    /**
     * Tests that when the request did not succeed the error closure is
     * called.
     */
    @Test
    public final void testErrorResponse() {
        final URIFetcher fetcher = new AbstractRedirectMockFetcher() {
            @Override
            public FetchingTask createGet(final URIAndCtx uriAndCtx) {
                return new FetchingTask() {
                    @Override
                    public URIAndCtx getURIAndCtx() {
                        return uriAndCtx;
                    }
                    
                    @Override
                    public URIFetcherResponse execute() {
                        return new InmutableURIFetcherResponse(
                                uriAndCtx,
                                new InmutableURIFetcherHttpResponse("", 200, 
                                        Collections.EMPTY_MAP));
                    }
                };
            }
        };
        
        final AtomicInteger i = new AtomicInteger();
        final AtomicInteger j = new AtomicInteger();
        final Closure<URIFetcherResponse> ret = new RedirectClosure(
                new NotsoAsyncUriFetcher(), fetcher,
                new Closure<URIFetcherResponse>() {
                    @Override
                    public void execute(final URIFetcherResponse t) {
                        i.incrementAndGet();
                    }

        }, new Closure<URIFetcherResponse>() {
                @Override
                public void execute(final URIFetcherResponse t) {
                    j.incrementAndGet();
                    assertEquals(uri1, t.getURIAndCtx().getURI());
                }
        }, failClousure, 5);


        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        headers.put("locAtiOn", Arrays.asList("http://www.foo1.com"));
        ret.execute(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri1),
                new RuntimeException()));
        assertEquals(0, i.get());
        assertEquals(1, j.get());
    }
    
    /**
     * Tests that when a 301 is returned as the status code
     * a called to the fetcher with the new location is called.
     */
    @Test
    public final void testRedirect() {
        final URIFetcher fetcher = new AbstractRedirectMockFetcher() {
            @Override
            public FetchingTask createGet(final URIAndCtx uriAndCtx) {
                return new FetchingTask() {
                    @Override
                    public URIAndCtx getURIAndCtx() {
                        return uriAndCtx;
                    }
                    
                    @Override
                    public URIFetcherResponse execute() {
                        final Map<String, List<String>> headers = 
                                new HashMap<String, List<String>>();
                            headers.put("locAtiOn", Arrays.asList("http://www.foo2.com"));
                            return new InmutableURIFetcherResponse(
                                    uriAndCtx,
                                    new InmutableURIFetcherHttpResponse("", 200, 
                                            headers));
                    }
                };
            }
        };
        
        final AtomicInteger i = new AtomicInteger();
        final MutableClosure<URIFetcherResponse> root = 
            new MutableClosure<URIFetcherResponse>();
        final Closure<URIFetcherResponse> ret = new RedirectClosure(
                new NotsoAsyncUriFetcher(), fetcher,
                new Closure<URIFetcherResponse>() {
                    @Override
                    public void execute(final URIFetcherResponse t) {
                        assertEquals(uri2, t.getURIAndCtx().getURI());
                        assertEquals(uri1.toString(), t.getURIAndCtx()
                                .getCtx().get(RedirectClosure.KEY_ORIGINAL_URL));
                        i.incrementAndGet();
                    }

        }, failClousure, root, 5);
        root.setTarget(ret);


        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        headers.put("locAtiOn", Arrays.asList("http://www.foo2.com"));
        ret.execute(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri1),
                new InmutableURIFetcherHttpResponse("", 301, headers)));
        assertEquals(1, i.get());
    }
    
    /**
     * Tests that when a 301 is returned more than the max hops set the
     * MaxRedirectHopsException is thrown.
     */
    @Test(expected = MaxRedirectHopsException.class)
    public final void testMaxHops() {
        final AtomicInteger i = new AtomicInteger();
        final URIFetcher fetcher = new AbstractRedirectMockFetcher() {
            @Override
            public FetchingTask createGet(final URIAndCtx uriAndCtx) {
                return new FetchingTask() {
                    @Override
                    public URIAndCtx getURIAndCtx() {
                        return uriAndCtx;
                    }
                    
                    @Override
                    public URIFetcherResponse execute() {
                        System.out.println("Redirect number: " 
                                + i.incrementAndGet());
                        final Map<String, List<String>> headers = 
                            new HashMap<String, List<String>>();
                        headers.put("locAtiOn", Arrays.asList("http://www.foo2.com"));
                        return new InmutableURIFetcherResponse(
                                uriAndCtx,
                                new InmutableURIFetcherHttpResponse("", 301, 
                                        headers));
                    }
                };
            }
        };
        
        final MutableClosure<URIFetcherResponse> root = 
            new MutableClosure<URIFetcherResponse>();
        final Closure<URIFetcherResponse> ret = new RedirectClosure(new NotsoAsyncUriFetcher(), fetcher,
                failClousure, failClousure, root, 5);
        root.setTarget(ret);


        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        headers.put("locAtiOn", Arrays.asList("http://www.foo2.com"));
        ret.execute(new InmutableURIFetcherResponse(
                new InmutableURIAndCtx(uri1),
                new InmutableURIFetcherHttpResponse("", 301, headers)));
        assertEquals(1, i.get());
    }
    
}

/**
 * Base mock class for handling redirects
 * 
 * 
 * @author Guido Marucci Blas
 * @since Feb 10, 2011
 */
abstract class AbstractRedirectMockFetcher extends AbstractURIFetcher {

    @Override
    public final FetchingTask createPost(final URIAndCtx uriAndCtx, final InputStream body) {
        throw new NotImplementedException("POST not implemented");
    }

    @Override
    public final FetchingTask createPost(final URIAndCtx uriAndCtx, final UrlEncodedPostBody body) {
        throw new NotImplementedException("POST not implemented");
    }


}