/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.async.AsyncUriFetcherObserver;

/**
 * TODO Descripcion de la clase. Los comenterios van en castellano.
 *
 *
 * @author Juan F. Codagnone
 * @since Mar 1, 2010
 */
public class NullAsyncUriFetcherObserver implements AsyncUriFetcherObserver {

    /** @see AsyncUriFetcherObserver#beginFetch(URIFetcherResponse.URIAndCtx) */
    public void beginFetch(final URIAndCtx uriAndCtx) {
        // nada que hacer
    }

    /** @see AsyncUriFetcherObserver#beginProcessing(URIFetcherResponse.URIAndCtx) */
    public void beginProcessing(final URIAndCtx uriAndCtx) {
        // nada que hacer
    }

    /** @see AsyncUriFetcherObserver#finishFetch(URIAndCtx, long) */
    public void finishFetch(final URIAndCtx uriAndCtx, final long elapsed) {
        // nada que hacer

    }

    /** @see AsyncUriFetcherObserver#finishProcessing(URIAndCtx, long) */
    public void finishProcessing(final URIAndCtx uriAndCtx, final long elapsed) {
        // nada que hacer

    }

    /** @see AsyncUriFetcherObserver#newFetch(URIFetcherResponse.URIAndCtx) */
    public void newFetch(final URIAndCtx uriAndCtx) {
        // nada que hacer

    }
}
