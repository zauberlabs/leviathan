/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

import ar.com.zauber.commons.dao.Closure;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public abstract class ContextAwareClosure<T> extends CurrentThreadURIAndContextDictionary implements Closure<T> {

    /** @see ar.com.zauber.commons.dao.Closure#execute(java.lang.Object) */
    @Override
    public void execute(T arg0) {
        // TODO: Auto-generated method stub

    }

}
