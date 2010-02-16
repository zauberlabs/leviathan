/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zauber.com.ar/>
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
package ar.com.zauber.labs.kraken.core.fetcher.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.junit.Test;

import ar.com.zauber.labs.kraken.core.fetcher.impl.ehcache.EhcacheURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.common.mock.FixedURIFetcher;

/**
 * Tests para {@link EhcacheURIFetcher}
 * 
 * @author Francisco J. Gonzalez Costanzo
 * @since Nov 19, 2009
 */
public class EhcacheURIFetcherDriverTest {
    

    /** testCaching */
    @Test
    public final void testCaching() throws URISyntaxException {
        final CacheManager m = CacheManager.create(
                EhcacheURIFetcherDriverTest.class.getResourceAsStream("ehcache.xml"));
        final Cache cache = m.getCache("fetcher");
                
        final Map<URI, String> map = new TreeMap<URI, String>();
        final URIFetcher fetcher = new EhcacheURIFetcher(new FixedURIFetcher(map),
                cache);
        
        final URI uri = new URI("http://www.club.lanacion.com.ar");
        fetcher.fetch(uri);
        CacheManager.getInstance().shutdown();
    }
}
