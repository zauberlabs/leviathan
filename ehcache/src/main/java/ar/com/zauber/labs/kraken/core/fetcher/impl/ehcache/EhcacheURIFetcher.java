/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.ehcache;

import java.net.URI;
import java.util.Formatter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.labs.kraken.fetcher.common.CtxDecorableURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIAndCtx;

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
public class EhcacheURIFetcher implements URIFetcher {
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

    /** @see URIFetcher#fetch(URIAndCtx) */
    public final URIFetcherResponse fetch(final URI uri) {
        return fetch(new InmutableURIAndCtx(uri));
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
