/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.validate.Validate;

/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ClosureAdapterPipe<T> implements Pipe<T, Void> {

    private final Closure<T> closure;

    /**
     * Creates the ClosureAdapterPipe.
     *
     * @param closure
     */
    public ClosureAdapterPipe(final Closure<T> closure) {
        Validate.notNull(closure, "The closure cannot be null");
        this.closure = closure;
    }

    @Override
    public Void execute(final T input) {
        closure.execute(input);
        return null;
    }

}
