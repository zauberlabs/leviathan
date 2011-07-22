/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * Mixin and {@link ActionHandler} and {@link ControlStructureHanlder}
 * 
 * @param <T> The type of the object for which the action will be performed.
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface ActionAndControlStructureHandler<T> extends ControlStructureHanlder, ActionHandler<T> {

}
