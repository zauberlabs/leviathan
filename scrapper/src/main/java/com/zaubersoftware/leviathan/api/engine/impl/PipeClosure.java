/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaubersoftware.leviathan.api.engine.impl.pipe.Pipe;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * A {@link Closure} that feeds {@link URIFetcherResponse} a {@link Pipe} 
 * 
 * @param <O> The {@link Pipe}'s output type
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class PipeClosure<O> implements Closure<URIFetcherResponse> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Pipe<URIFetcherResponse, O> pipe;
    
    /**
     * Creates the PipeClosure.
     *
     * @param pipe
     */
    public PipeClosure(final Pipe<URIFetcherResponse, O> pipe) {
        Validate.notNull(pipe);
        this.pipe = pipe;
    }


    @Override
    public void execute(final URIFetcherResponse response) {
        Validate.notNull(response);

        final O out = pipe.execute(response);
        if(out != null) {
            logger.warn("The last pipe returned a value != null. Null was expected: {}", out);
        }
    }

}
