/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * Interface that gives implementors the ability to handle errors that can occur during the processing
 * flow. 
 * 
 * @param <T> The interface type that defines the available actions that can be configured once the error 
 * handler has been subscribed. 
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface ErrorTolerant<T> {

    /**
     * Subscribes an error handler for any {@link Throwable} that has not been cathed by a more
     * precise handler.
     * 
     * @param handler an {@link ExceptionHandler}. Must not be null.
     * @return The interface that defines the available actions that can be configured once the error
     * @throws IllegalArgumentException if the handler is null. 
     * handler has been subscribed. 
     */
    T onError(ExceptionHandler<Throwable> handler);
    
    /**
     * Subscribes an error handler for the given <code>throwableClass</code> 
     * 
     * @param throwableClass The expected exception. Must not be null.
     * @param handler an {@link ExceptionHandler}.
     * @return The interface that defines the available actions that can be configured once the error 
     * @throws IllegalArgumentException if the handler or throwableClass is null.
     */
    <E extends Throwable> T onError(Class<E> throwableClass, ExceptionHandler<E> handler);
}
