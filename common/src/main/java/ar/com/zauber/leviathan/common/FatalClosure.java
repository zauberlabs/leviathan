/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * A closure that thorws an exception in case the response has an error. 
 * 
 * @author Guido Marucci Blas
 * @since Feb 10, 2011
 */
public final class FatalClosure implements Closure<URIFetcherResponse> {

    @Override
    public void execute(final URIFetcherResponse response) {
        Validate.notNull(response);
        
        if (!response.isSucceeded()) {
            throw new UnhandledException(response.getError());
        }
    }
}
