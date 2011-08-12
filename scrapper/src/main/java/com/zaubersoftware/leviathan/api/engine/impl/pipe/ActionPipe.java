/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import com.zaubersoftware.leviathan.api.engine.Action;

import ar.com.zauber.commons.validate.Validate;

/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ActionPipe<I, O> implements Pipe<I, O> {

    private final Action<I, O> action;
    /**
     * Creates the ActionPipe.
     *
     */
    public ActionPipe(final Action<I, O> action) {
        Validate.notNull(action, "The action can not be null!");
        this.action = action;
    }

    @Override
    public O execute(final I input) {
        return this.action.execute(input);
    }

}
