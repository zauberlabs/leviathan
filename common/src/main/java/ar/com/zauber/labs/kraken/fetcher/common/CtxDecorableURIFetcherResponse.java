/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;

import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * {@link URIFetcherHttpResponse} que recibe un ctx, y otro
 * {@link URIFetcherHttpResponse} en quien delega todos los mensajes salvo el
 * {@link #getURIAndCtx()}
 *
 * @author Mariano Semelman
 * @since Dec 4, 2009
 */
public class CtxDecorableURIFetcherResponse implements URIFetcherResponse {
    private final URIFetcherResponse target;
    private final URIAndCtx uriAndCtx;


    /** Creates the CtxDecorableURIFetcherResponse. */
    public CtxDecorableURIFetcherResponse(final URIFetcherResponse target,
            final URIAndCtx uriAndCtx) {
        Validate.notNull(target);
        Validate.notNull(uriAndCtx);

        this.target = target;
        this.uriAndCtx = uriAndCtx;
    }

    /** @see URIFetcherResponse#getURIAndCtx() */
    public final URIAndCtx getURIAndCtx() {
        return uriAndCtx;
    }

    /** @see URIFetcherResponse#getError() */
    public final Throwable getError() throws IllegalStateException {
        return target.getError();
    }

    /** @see URIFetcherResponse#getHttpResponse() */
    public final URIFetcherHttpResponse getHttpResponse()
            throws IllegalStateException {
        return target.getHttpResponse();
    }

    /** @see URIFetcherResponse#getURI() */
    public final URI getURI() {
        return target.getURI();
    }

    /** @see URIFetcherResponse#isSucceeded() */
    public final boolean isSucceeded() {
        return target.isSucceeded();
    }
}
