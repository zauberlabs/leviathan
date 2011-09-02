/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl;

import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ErrorTolerantAfterThen;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 *
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class DefaultErrorTolerantAfterThen extends EngineFowarder implements ErrorTolerantAfterThen {

    /**
     * Creates the DefaultErrorTolerantAfterThen.
     *
     * @param target
     */
    public DefaultErrorTolerantAfterThen(final Engine target) {
        super(target);
    }

    /** @see com.zaubersoftware.leviathan.api.engine.AfterThen#pack() */
    @Override
    public ProcessingFlow pack() {
        // TODO Auto-generated method stub
        return null;
    }

}
