/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import org.apache.commons.lang.NotImplementedException;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
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

    /** @see Job#getUriAndCtx() */
    public URIAndCtx getUriAndCtx() {
        throw new NotImplementedException();
    }
}
