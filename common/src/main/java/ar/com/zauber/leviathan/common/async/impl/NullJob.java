/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import ar.com.zauber.leviathan.common.async.Job;

/**
 * {@link Job} que no hace nada. Util para tests.
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class NullJob implements Job {

    /** @see Runnable#run() */
    public final void run() {
        // void
    }

}
