/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.mock;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.core.fetcher.impl.InmutableURIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.core.fetcher.impl.InmutableURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * {@link URIFetcher} used for tests. Get the content from the classpath.
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class FixedURIFetcher implements URIFetcher {
    private final Map<URI, String> map;
    private final Charset charset;
    
    /** Creates the FixedContentProvider. */
    public FixedURIFetcher(final Map<URI, String> map) {
        this(map, Charset.defaultCharset());
    }
    
    /** Creates the FixedContentProvider. */
    public FixedURIFetcher(final Map<URI, String> map,
            final Charset charset) {
        Validate.notNull(map);
        Validate.notNull(charset);

        this.map = map;
        this.charset = charset;
    }

    /** @see URIFetcher#fetch(URI) */
    public final URIFetcherResponse fetch(final URI uri) {
        final String destURL = map.get(uri);
        final URIFetcherResponse ret;
        
        if(destURL == null) {
            ret = new  InmutableURIFetcherResponse(uri, 
                    new UnknownHostException(uri.getHost()));
        } else {
            final InputStream is = getClass().getClassLoader().getResourceAsStream(
                    destURL);
            
            if(is == null) {
                ret = new  InmutableURIFetcherResponse(uri,
                        new UnknownHostException(uri.getHost()));
            } else {
                ret = new InmutableURIFetcherResponse(uri,
                        new InmutableURIFetcherHttpResponse(
                            new InputStreamReader(is, charset), 200));
            }
        }
        
        return ret;
    }
}
