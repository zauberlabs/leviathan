/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;


/**
 * Contiene la informacion necesaria para obtener recursos. Estas tareas se 
 * suelen encolar hasta que se puedan realizar. 
 * 
 * @author Juan F. Codagnone
 * @since Feb 16, 2010
 */
public interface Job extends Runnable {
    
    /** @return un UriAndCtx */
    URIAndCtx getUriAndCtx();
}
