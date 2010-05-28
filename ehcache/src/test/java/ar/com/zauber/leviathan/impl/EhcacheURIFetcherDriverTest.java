/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ar.com.zauber.leviathan.impl;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang.Validate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherHttpResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;
import ar.com.zauber.leviathan.impl.ehcache.EhcacheURIFetcher;
import ar.com.zauber.leviathan.impl.ehcache.EhcacheURIFetcher.CACHING_BEHAVIOR;

/**
 * Tests para {@link EhcacheURIFetcher}
 * 
 * @author Francisco J. Gonzalez Costanzo
 * @since Nov 19, 2009
 */
public class EhcacheURIFetcherDriverTest {
    private Cache cache;

    @Before
    public final void setUp() {
        final InputStream is = EhcacheURIFetcherDriverTest.class.getResourceAsStream("ehcache.xml");
        Validate.notNull(is, "cant find ehcache.xml");
        final CacheManager m = CacheManager.create(is);
        cache = m.getCache("fetcher");
    }

    @After
    public final void tearDown() {
        cache.removeAll();
        CacheManager.getInstance().shutdown();
    }

    /** testCaching */
    @Test
    public final void testCaching() throws URISyntaxException {
        final Map<URI, String> map = new TreeMap<URI, String>();
        URIFetcher fetcher = new EhcacheURIFetcher(new FixedURIFetcher(map), cache);
        final URI uri = new URI("http://www.club.lanacion.com.ar");
        fetcher.get(uri);
    }

    @Test
    public final void testOKCachingBehavior() throws URISyntaxException {
        URIFetcher mockedFetcher = mock(URIFetcher.class);

        // 200 para www.club.lanacion.com.ar
        final URIAndCtx uri = new InmutableURIAndCtx(new URI("http://www.club.lanacion.com.ar"));
        URIFetcherResponse mocked200Response = mockFetcherResponse(200);

        // 404 para www.google.com/pepe
        final URIAndCtx uri2 = new InmutableURIAndCtx(new URI("http://www.google.com/pepe"));
        URIFetcherResponse mocked404Response = mockFetcherResponse(404);

        when(mockedFetcher.get(uri)).thenReturn(mocked200Response);
        when(mockedFetcher.get(uri2)).thenReturn(mocked404Response);

        EhcacheURIFetcher fetcher = new EhcacheURIFetcher(mockedFetcher, cache, CACHING_BEHAVIOR.OK);

        assertEquals(0, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri);
        assertEquals(1, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri2);
        assertEquals(2, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri);
        assertEquals(3, fetcher.getTotal());
        assertEquals(1, fetcher.getHits());

        fetcher.get(uri2);
        assertEquals(4, fetcher.getTotal());
        assertEquals(1, fetcher.getHits());

        fetcher.get(uri);
        assertEquals(5, fetcher.getTotal());
        assertEquals(2, fetcher.getHits());

    }

    @Test
    public final void testALLCachingBehavior() throws URISyntaxException {
        URIFetcher mockedFetcher = mock(URIFetcher.class);

        // 200 para www.club.lanacion.com.ar
        final URIAndCtx uri = new InmutableURIAndCtx(new URI("http://www.club.lanacion.com.ar"));
        URIFetcherResponse mocked200Response = mockFetcherResponse(200);

        // 404 para www.google.com/pepe
        final URIAndCtx uri2 = new InmutableURIAndCtx(new URI("http://www.google.com/pepe"));
        URIFetcherResponse mocked404Response = mockFetcherResponse(404);

        // Error para noexiste.com
        final URIAndCtx uri3 = new InmutableURIAndCtx(new URI("http://noexiste.com"));
        URIFetcherResponse mockedErrorResponse = mockNotSuccededResponse();

        when(mockedFetcher.get(uri)).thenReturn(mocked200Response);
        when(mockedFetcher.get(uri2)).thenReturn(mocked404Response);
        when(mockedFetcher.get(uri3)).thenReturn(mockedErrorResponse);

        EhcacheURIFetcher fetcher = new EhcacheURIFetcher(mockedFetcher, cache,
                CACHING_BEHAVIOR.ALL);

        assertEquals(0, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri);
        assertEquals(1, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri2);
        assertEquals(2, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri3);
        assertEquals(3, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri);
        assertEquals(4, fetcher.getTotal());
        assertEquals(1, fetcher.getHits());

        fetcher.get(uri2);
        assertEquals(5, fetcher.getTotal());
        assertEquals(2, fetcher.getHits());

        fetcher.get(uri3);
        assertEquals(6, fetcher.getTotal());
        assertEquals(3, fetcher.getHits());
    }

    @Test
    public final void testSUCCEDEDCachingBehavior() throws URISyntaxException {
        URIFetcher mockedFetcher = mock(URIFetcher.class);

        // 200 para www.club.lanacion.com.ar
        final URIAndCtx uri = new InmutableURIAndCtx(new URI("http://www.club.lanacion.com.ar"));
        URIFetcherResponse mocked200Response = mockFetcherResponse(200);

        // 404 para www.google.com/pepe
        final URIAndCtx uri2 = new InmutableURIAndCtx(new URI("http://www.google.com/pepe"));
        URIFetcherResponse mocked404Response = mockFetcherResponse(404);

        // Error para noexiste.com
        final URIAndCtx uri3 = new InmutableURIAndCtx(new URI("http://noexiste.com"));
        URIFetcherResponse mockedErrorResponse = mockNotSuccededResponse();

        when(mockedFetcher.get(uri)).thenReturn(mocked200Response);
        when(mockedFetcher.get(uri2)).thenReturn(mocked404Response);
        when(mockedFetcher.get(uri3)).thenReturn(mockedErrorResponse);

        EhcacheURIFetcher fetcher = new EhcacheURIFetcher(mockedFetcher, cache,
                CACHING_BEHAVIOR.SUCCEEDED);

        assertEquals(0, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri);
        assertEquals(1, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri2);
        assertEquals(2, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri3);
        assertEquals(3, fetcher.getTotal());
        assertEquals(0, fetcher.getHits());

        fetcher.get(uri);
        assertEquals(4, fetcher.getTotal());
        assertEquals(1, fetcher.getHits());

        fetcher.get(uri2);
        assertEquals(5, fetcher.getTotal());
        assertEquals(2, fetcher.getHits());

        fetcher.get(uri3);
        assertEquals(6, fetcher.getTotal());
        assertEquals(2, fetcher.getHits());
    }

    private URIFetcherResponse mockFetcherResponse(final int status) {
        URIFetcherHttpResponse httpResponse = mock(URIFetcherHttpResponse.class);
        when(httpResponse.getStatusCode()).thenReturn(status);
        URIFetcherResponse response = mock(URIFetcherResponse.class, withSettings().serializable());
        when(response.isSucceeded()).thenReturn(true);
        when(response.getHttpResponse()).thenReturn(httpResponse);

        return response;
    }

    private URIFetcherResponse mockNotSuccededResponse() {
        URIFetcherResponse response = mock(URIFetcherResponse.class, withSettings().serializable());
        when(response.isSucceeded()).thenReturn(false);
        when(response.getHttpResponse()).thenThrow(new IllegalStateException());

        return response;
    }
}
