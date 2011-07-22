/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;


/**
 * Defines all the actions that can be configured after the Java object has been deserialized.
 * 
 * @param <T> The type of the deserialized Java object
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface AfterJavaObjectHandler<T> extends ErrorTolerantActionAndControlStructureHandler<T> {

}
