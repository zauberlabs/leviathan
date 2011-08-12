/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

import java.net.URI;


/**
 * Defines all the actions that can be configured for the fetched resource in order to process it.
 * 
 * @param <T> The type of the object for which the action will be performed.
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface ActionHandler<T>  {

    /**
     * Configures the next processing {@link Action} in the chain.
     * @param object the {@link Action} to execute over the T object.
     * @param <R> the result type.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the object is null.
     */
    <R> ActionAndControlStructureHandler<R> then(Action<T, R> object);
    
    
    /**
     * Configures the next processing {@link Action} in the chain and then fetches a new resource.
     * @param object the {@link Action} to execute over the T object.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the object is null.
     */
    AfterFetchingHandler then(ActionAndThenFetch<T> object);

    /**
     * Configures the last processing closure in the chain.
     * @param object the closure to execute over the T object.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the object is null.
     */
    Engine then(ContextAwareClosure<T> object);
    
    
    /**
     * Configures the next resource to be fetched.
     * @param uriTemplate the location of the resource.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the uriTemplate is null.
     */
    AfterFetchingHandler thenFetch(String uriTemplate);
    
    /**
     * Configures the next resource to be fetched.
     * @param uri the location of the resource.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the uri is null.
     */
    AfterFetchingHandler thenFetch(URI uri);
 
    /**
     * Null action.
     * @return An {@link Engine}
     */
    Engine thenDoNothing();
}
