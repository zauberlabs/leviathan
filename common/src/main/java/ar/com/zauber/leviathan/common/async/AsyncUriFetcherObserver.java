/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * Permite mostrar el progreso de los downloads.
 * 
 * @author Juan F. Codagnone
 * @since Feb 27, 2010
 */
public interface AsyncUriFetcherObserver {

    /** una nueva url se quiere encolar como trabajo */
    void newFetch(URIAndCtx uriAndCtx);
    
    /** se comienza a descargar */
    void beginFetch(URIAndCtx uriAndCtx);
    
    /** se termina a descargar */
    void finishFetch(URIAndCtx uriAndCtx, long elapsed);
    
    /** se comienza a procesar */
    void beginProcessing(URIAndCtx uriAndCtx);
    
    /** se termina a procesar */
    void finishProcessing(URIAndCtx uriAndCtx, long elapsed);
}
