/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class LoggerPipe<I, O> implements Pipe<I, O> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @SuppressWarnings("rawtypes")
    private final Pipe<I, O> pipe;

    /**
     * Creates the DeadEndPipe.
     *
     * @param pipe
     */
    public <T> LoggerPipe(final Pipe<I, O> pipe) {
        Validate.notNull(pipe, "The pipe cannot be null");
        this.pipe = pipe;
    }

    @Override
    public O execute(final I input) {
        final O result = this.pipe.execute(input);
        logger.debug("Pipe's flow -> {}", (result == null) ? null : result.toString());
        return result;
    }

}
