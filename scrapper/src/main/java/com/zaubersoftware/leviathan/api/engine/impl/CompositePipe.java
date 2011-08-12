/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl;

import java.util.Collection;

import org.apache.commons.lang.Validate;

/**
 * A {@link Pipe} compositer
 * 
 * @param <I>
 * @param <O>
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class CompositePipe<I, O> implements Pipe<I, O> {

    private final Iterable<Pipe<?, ?>> pipes;

    /**
     * Creates the CompositePipe.
     *
     * @param pipes
     */
    public CompositePipe(final Iterable<Pipe<?, ?>> pipes) {
        Validate.notNull(pipes);
        this.pipes = pipes;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" }) 
    public O execute(final I input) {
        Object ret = input;
        for (final Pipe pipe : pipes) {
            ret = pipe.execute(ret);
        }
        return (O) ret;
    }
    
}
