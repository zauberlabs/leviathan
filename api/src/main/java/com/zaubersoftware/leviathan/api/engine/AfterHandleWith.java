/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 *
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public interface AfterHandleWith<T> extends ExceptionCatchDefinition<T> {

    T otherwiseHandleWith(ExceptionHandler handler);

}
