/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;


/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public abstract class ExceptionHandler<T extends Throwable> extends CurrentThreadURIAndContextDictionary {

    public abstract void handle(T trowable);

    @SuppressWarnings("unchecked")
    public final <C> Class<C> getInputClass() {
        return (contains(ContextKeys.INPUT_CLASS_KEY)) ? (Class<C>) get(ContextKeys.INPUT_CLASS_KEY) : null;
    }

    @SuppressWarnings("unchecked")
    public final <C> C getInput() {
        return (contains(ContextKeys.INPUT_OBJECT_KEY))
        ? (C) getInputClass().cast(get(ContextKeys.INPUT_OBJECT_KEY)) : null;
    }
}
