/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.async.AsyncUriFetcherObserver;

/**
 * Muestra información  de debug
 * 
 * @author Juan F. Codagnone
 * @since Feb 27, 2010
 */
public class DebugLoggerAsyncUriFetcherObserver implements AsyncUriFetcherObserver {
    private final Logger logger;
    // esto puede bloquear, asi que es por eso que se hizo todo el lock en
    // en incrementActiveJobs.
    private final boolean isDebug;
    
    /** Creates the LoggerAsyncUriFetcherObserver. */
    public DebugLoggerAsyncUriFetcherObserver(final Logger logger) {
        Validate.notNull(logger);
        
        this.logger = logger;
        isDebug = logger.isDebugEnabled();
    }
    
    /** @see AsyncUriFetcherObserver#newFetch(URIFetcherResponse.URIAndCtx) */
    public final void newFetch(final URIAndCtx uriAndCtx) {
        logger.debug("New URI: " + uriAndCtx.getURI());
    }

    /** @see AsyncUriFetcherObserver#beginFetch(URIAndCtx) */
    public final void beginFetch(final URIAndCtx uriAndCtx) {
        if(isDebug) {
            logger.debug("Fetching " + uriAndCtx.getURI());
        }
    }
    
    /** @see AsyncUriFetcherObserver#finishFetch(URIAndCtx, long) */
    public final void finishFetch(final URIAndCtx uriAndCtx, final long elapsed) {
        if(isDebug) {
            logger.debug("Elapsed " + elapsed + "ms fetching " 
                    + uriAndCtx.getURI());
        }
    }
    
    /** @see AsyncUriFetcherObserver#beginProcessing(URIFetcherResponse.URIAndCtx) */
    public final void beginProcessing(final URIAndCtx uriAndCtx) {
        if(isDebug) {
            logger.debug("Processing " + uriAndCtx.getURI());
        }
    }
    /** @see AsyncUriFetcherObserver#finishProcessing(URIAndCtx, long) */
    public final void finishProcessing(final URIAndCtx uriAndCtx, 
            final long elapsed) {
        if(isDebug) {
            logger.debug("Elapsed " + elapsed + "ms on " + uriAndCtx.getURI());
        }
    }
}
