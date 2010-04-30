/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.async.Job;

/**
 * {@link Job} que incrementa un {@link AtomicInteger}. Es de utilidad
 * en tests para saber cuantas veces se ha ejecutado.
 * 
 * @author Juan F. Codagnone
 * @since Feb 19, 2010
 */
public class AtomicIntegerJob implements Job {
    private final AtomicInteger atomic;
    private final int delta;

    /** Creates the AtomicIntegerFetchJob. */
    public AtomicIntegerJob(final AtomicInteger atomic) {
        this(atomic, 1);
    }
    
    /** Creates the AtomicIntegerFetchJob. */
    public AtomicIntegerJob(final AtomicInteger atomic,
            final int delta) {
        Validate.notNull(atomic);
        
        this.atomic = atomic;
        this.delta = delta;
    }
    
    /** @see Runnable#run() */
    public final void run() {
        atomic.addAndGet(delta);
    }

    /** @see Job#getUriAndCtx() */
    public URIAndCtx getUriAndCtx() {
        throw new NotImplementedException();
    }
}
