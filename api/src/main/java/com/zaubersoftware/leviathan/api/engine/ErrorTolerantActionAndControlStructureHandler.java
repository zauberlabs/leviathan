/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * An extension of {@link ActionAndControlStructureHandler} that is {@link ErrorTolerant} 
 * 
 * @param <T> The type of the object for which the action will be performed.
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface ErrorTolerantActionAndControlStructureHandler<T> 
    extends ActionAndControlStructureHandler<T>, ErrorTolerant<ErrorTolerantActionAndControlStructureHandler<T>> {

}
