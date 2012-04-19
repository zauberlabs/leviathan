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
package ar.com.zauber.leviathan.impl.ehcache;

import java.io.InputStream;
import java.net.URI;
import java.util.Formatter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;
import ar.com.zauber.leviathan.common.AbstractURIFetcher;
import ar.com.zauber.leviathan.common.CtxDecorableURIFetcherResponse;

/**
 * EhCache URI fetcher.
 *
 * Inicialmente se pensaba usar
 * {@link ar.com.zauber.commons.web.rest.impl.EhcacheContentProvider}, pero en
 * ese caso se pierde la info del response, específicamente el statusCode. En
 * caso de no ser necesario se podría considerar ya que la funcionalidad es
 * similar.
 *
 * @author Francisco J. Gonzalez Costanzo
 * @since Nov 19, 2009
 */
public class EhcacheURIFetcher extends AbstractURIFetcher {
    private static final Logger LOGGER  = LoggerFactory.getLogger(
            EhcacheURIFetcher.class); 
    private final URIFetcher fetcher;
    private final Cache cache;
    private final CACHING_BEHAVIOR cachingBehavior;
    private long hits;
    private long total;


    /**
     * 
     * Define cuando se cachea un request.
     * 
     * <dl> 
     *  <dt>ALL</dt> 
     *      <dd>Todos los requests sin importar la respuesta se cachean</dd>
     *  <dt>SUCCEDED</dt> 
     *      <dd>Solo los requests que hayan sido completados se cachean 
     *      ({@link URIFetcherResponse#isSucceeded()} es <code>true</code>) </dd>
     *  <dt>OK</dt>
     *      <dd>Solo los requests completados con status code 2xx se cachean</dd>      
     * </dl>
     */
    public static enum CACHING_BEHAVIOR {ALL, SUCCEEDED, OK};
    
    
    /**
     * Creates the EHCacheURIFetcher.
     * 
     * @param fetcher
     * @param cache
     * @param cachingBehavior define cuando se cachea un request.   
     */
    public EhcacheURIFetcher(final URIFetcher fetcher, final Cache cache, 
            final CACHING_BEHAVIOR cachingBehavior) {
        Validate.notNull(fetcher);
        Validate.notNull(cache);
        Validate.notNull(cachingBehavior);
        
        this.fetcher = fetcher;
        this.cache = cache;
        this.cachingBehavior = cachingBehavior;
    }

    
    /**
     * Crea el EHCacheURIFetcher.  
     * Cachea todos los requests ({@link CACHING_BEHAVIOR#ALL})
     */
    public EhcacheURIFetcher(final URIFetcher fetcher, final Cache cache) {
        this(fetcher, cache, CACHING_BEHAVIOR.ALL);
    }
    
    /**
     * @see ar.com.zauber.leviathan.api.URIFetcher#fetch(java.net.URI)
     * @deprecated Use {@link #get(URI)}
     */
    @Deprecated
    public final URIFetcherResponse fetch(final URI uri) {
        return get(uri);
    }
    
    /**
     * @see URIFetcher#fetch(URIFetcherResponse.URIAndCtx)
     * @deprecated Use {@link #get(URIAndCtx)}
     */
    @Deprecated
    public final URIFetcherResponse fetch(final URIAndCtx uri) {
        return get(uri);
    }
    
    @Override
    public final FetchingTask createGet(final URIAndCtx uriAndCtx) {
        return new FetchingTask() {
            
            @Override
            public URIAndCtx getURIAndCtx() {
                return uriAndCtx;
            }
            
            @Override
            public URIFetcherResponse execute() {
                final Element e = cache.get(uriAndCtx.getURI());
                URIFetcherResponse ret;
                if (e == null) {
                    ret = fetcher.get(uriAndCtx);

                    switch (cachingBehavior) {
                        case OK:
                            if(ret.isSucceeded() && ret.getHttpResponse().getStatusCode() >= 200 
                                    && ret.getHttpResponse().getStatusCode() < 300) {
                                cache.put(new Element(uriAndCtx.getURI(), ret));
                            }
                            break;
                        case SUCCEEDED:
                            if(ret.isSucceeded()) {
                                cache.put(new Element(uriAndCtx.getURI(), ret));
                            }
                            break;
                        case ALL:
                            cache.put(new Element(uriAndCtx.getURI(), ret));
                            break;
                        default:
                            throw new IllegalStateException("CachingBehavior no manejado");
                    }
                } else {
                    ret = new CtxDecorableURIFetcherResponse(
                            (URIFetcherResponse) e.getValue(), uriAndCtx);
                    hits++;
                }
                total++;
                return ret;
            }
        };
    }

    
    @Override
    public final FetchingTask createPost(final URIAndCtx uriAndCtx, final InputStream body) {
        return fetcher.createPost(uriAndCtx, body);
    }
    
    @Override
    public final FetchingTask createPost(final URIAndCtx uriAndCtx, final UrlEncodedPostBody body) {
        return fetcher.createPost(uriAndCtx, body);
    }
    
    public final long getHits() {
        return hits;
    }

    public final long getTotal() {
        return total;
    }

    /** log the status of the fetcher */
    public final void logStatus() {
        if(total > 0) {
            LOGGER.info(
               new Formatter(new StringBuilder()).format(
                    "Fetcher cache hit ratio: %2.2f%% (%d/%d).",
                    100.0 * hits / total, hits, total).toString());
        }
    }

}
