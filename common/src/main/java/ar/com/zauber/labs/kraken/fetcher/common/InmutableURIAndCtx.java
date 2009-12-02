/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * URIAndCtx inmutable implementation
 *
 * @author Mariano Semelman
 * @since Dec 2, 2009
 */
public class InmutableURIAndCtx implements URIFetcherResponse.URIAndCtx {
    private final URI uri;
    private final Map<String, Object> ctx;

    /** Creates the InmutableURIAndCtx. */
    public InmutableURIAndCtx(final URI uri) {
        this(uri, Collections.EMPTY_MAP);
    }

    /** Creates the InmutableURIAndCtx. */
    public InmutableURIAndCtx(final URI uri, final Map<String, Object> ctx) {
        Validate.notNull(uri);
        Validate.notNull(ctx);

        this.uri = uri;
        this.ctx = ctx;
    }

    /** @see BulkURIFetcher.URIAndCtx#getCtx() */
    public final Map<String, Object> getCtx() {
        return ctx;
    }

    /** @see BulkURIFetcher.URIAndCtx#getURI() */
    public final URI getURI() {
        return uri;
    }
}
