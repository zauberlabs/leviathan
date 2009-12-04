/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIAndCtx;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherResponse;

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

    /** @see URIFetcher#fetch(URIFetcherResponse.URIAndCtx) */
    public final URIFetcherResponse fetch(final URIAndCtx uriAndCtx) {
        final URI uri = uriAndCtx.getURI();
        final String destURL = map.get(uri);
        final URIFetcherResponse ret;

        if(destURL == null) {
            ret = new  InmutableURIFetcherResponse(uriAndCtx,
                    new UnknownHostException(uri.getHost()));
        } else {
            final InputStream is = getClass().getClassLoader().getResourceAsStream(
                    destURL);

            if(is == null) {
                ret = new  InmutableURIFetcherResponse(uriAndCtx,
                        new UnknownHostException(uri.getHost()));
            } else {
                try {
                    ret = new InmutableURIFetcherResponse(uriAndCtx,
                            new InmutableURIFetcherHttpResponse(
                                new String(IOUtils.toByteArray(is), charset), 200));
                } catch (IOException e) {
                    throw new UnhandledException(e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        return ret;
    }

    /** @see URIFetcher#fetch(URI) */
    public final URIFetcherResponse fetch(final URI uri) {
        return fetch(new InmutableURIAndCtx(uri));
    }
}
