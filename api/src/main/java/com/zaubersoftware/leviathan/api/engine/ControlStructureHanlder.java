/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;


/**
 * Defines all the available control structures actions.
 * 
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface ControlStructureHanlder {

    /**
     * Iterates over an {@link Iterable}
     * 
     * @param <R> The type of the elements that will be returned by the Iterator.
     * @param elementClass The class of the elements that will be returned by the Iterator. Must not be null. 
     * @return An {@link AfterForEachHandler} that will export the method to indicate which method should
     * be called to obtain the iterator.
     * @throws IllegalArgumentException if the elementClass is null.
     */
    <R> AfterForEachHandler<R> forEach(Class<R> elementClass);

}
