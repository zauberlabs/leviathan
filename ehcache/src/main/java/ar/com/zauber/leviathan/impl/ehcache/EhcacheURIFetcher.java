/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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

import java.net.URI;
import java.util.Formatter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
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
    private final static Logger LOGGER  = Logger.getLogger(EhcacheURIFetcher.class); 
    private final URIFetcher fetcher;
    private final Cache cache;
    private long hits;
    private long total;
    
    /**
     * Creates the EHCacheURIFetcher.
     */
    public EhcacheURIFetcher(final URIFetcher fetcher, final Cache cache) {
        Validate.notNull(fetcher);
        Validate.notNull(cache);
        this.fetcher = fetcher;
        this.cache = cache;
    }

    /** @see URIFetcher#fetch(URI) */
    public final URIFetcherResponse fetch(final URIAndCtx uriAndCtx) {
        final Element e = cache.get(uriAndCtx.getURI());
        URIFetcherResponse ret;
        if (e == null) {
            ret = fetcher.fetch(uriAndCtx);
            cache.put(new Element(uriAndCtx.getURI(), ret));
        } else {
            ret = new CtxDecorableURIFetcherResponse((URIFetcherResponse) e.getValue(),
                    uriAndCtx);
            hits++;
        }
        total++;
        return ret;
    }
    
    public final long getHits() {
        return hits;
    }

    public final long getTotal() {
        return total;
    }

    /** log the status of the fetcher */
    public final void logStatus(){
        if(total > 0) {
            LOGGER.info(
               new Formatter(new StringBuilder()).format(
                    "Fetcher cache hit ratio: %2.2f%% (%d/%d).",
                    100.0 * hits / total, hits, total).toString());
        }
    }
}
