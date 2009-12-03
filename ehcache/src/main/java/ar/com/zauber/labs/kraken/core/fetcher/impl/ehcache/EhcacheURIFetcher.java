/*
 * Copyright (c) 2009 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.ehcache;

import java.net.URI;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

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
    private final URIFetcher fetcher;
    private final Cache cache;

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
    public final URIFetcherResponse fetch(final URI uri) {
        final Element e = cache.get(uri);
        if (e == null) {
            final URIFetcherResponse response = fetcher.fetch(uri);
            cache.put(new Element(uri, response));
            return response;
        } else {
            return fetcher.fetch(uri);
        }
    }
}
