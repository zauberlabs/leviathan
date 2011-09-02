/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * TODO
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public interface ProcessingFlowBinding {

    /**
     * Binds the given flow
     *
     * @param flow
     * @return The {@link Engine}
     */
    Engine toFlow(ProcessingFlow flow);

}
