/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

import ar.com.zauber.leviathan.api.CurrentThreadURIAndContextDictionary;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public abstract class ExceptionHandler<T extends Throwable> extends CurrentThreadURIAndContextDictionary {

    // TODO Move to this key definition to another place. Because when an exception is
    // caught the cause should be put into the context with the failing object and parameters.
    private final String CAUSE_OBJECT_KEY = getClass().getCanonicalName() + ".cause";
    
    public abstract void handle(T trowable);
    
    public final T getCause() {
        return (T) get(CAUSE_OBJECT_KEY);
    }
}
