/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.net.URI;
import java.util.Map;

/**
 * {@link URIFetcher} response.
 *
 *
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface URIFetcherResponse {

    /** @return the URI associated with this response */
    @Deprecated
    URI getURI();

    /** @return the URI associated with this response and it's context */
    URIAndCtx getURIAndCtx();

    /**
     * @return <code>true</code> if the request was able to connect the server
     * an execute the request, retriving HTTP information.
     * If it failed you can call {@link #getError()} to get more information.
     */
    boolean isSucceeded();

    /**
     * @see #isSucceeded()
     * @throws IllegalStateException if called when {@link #isSucceeded()}
     * is <code>true</code>
     */
    Throwable getError() throws IllegalStateException;

    /**
     * @see #isSucceeded()
     * @throws IllegalStateException if called when {@link #isSucceeded()}
     * is <code>false</code>
     */
    URIFetcherHttpResponse getHttpResponse() throws IllegalStateException;


    /** an uri and a retrival ctx */
    interface URIAndCtx {
        /** @return the uri */
        URI getURI();
        /** @return it's context */
        Map<String, Object> getCtx();
    }
}
