/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.api;


/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public abstract  class Action<T, R> extends CurrentThreadURIAndContextDictionary {

    public abstract R execute(T t);
    
}
