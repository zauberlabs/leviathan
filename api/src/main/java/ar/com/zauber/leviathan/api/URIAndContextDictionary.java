/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.api;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public interface URIAndContextDictionary extends URIAndCtx {

    Object get(String key);
    
    void put(String key, Object value);

}
