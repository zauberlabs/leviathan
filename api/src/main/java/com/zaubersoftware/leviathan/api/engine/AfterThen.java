/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * Termination method are exported by this interface
 *
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public interface AfterThen {

    /**
     * Packs all the pipes into a processing flow
     *
     * @return The processing flow.
     */
    ProcessingFlow pack();

}
