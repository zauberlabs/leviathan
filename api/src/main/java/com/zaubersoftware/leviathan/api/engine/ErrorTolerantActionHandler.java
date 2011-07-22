/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * Mixin of {@link ErrorTolerant} and {@link ActionHandler}   
 * 
 * 
 * @param <T> The interface type that defines the available actions that can be configured once the error 
 * handler has been subscribed. 
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011 
 */
public interface ErrorTolerantActionHandler<T> extends ErrorTolerant<ActionHandler<T>>, ActionHandler<T> {

}
