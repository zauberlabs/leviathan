/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.common.async.FetchJob;

/**
 * {@link FetchJob} que incrementa un {@link AtomicInteger}. Es de utilidad
 * en tests para saber cuantas veces se ha ejecutado.
 * 
 * @author Juan F. Codagnone
 * @since Feb 19, 2010
 */
public class AtomicIntegerFetchJob implements FetchJob {
    private final AtomicInteger atomic;
    private final int delta;

    /** Creates the AtomicIntegerFetchJob. */
    public AtomicIntegerFetchJob(final AtomicInteger atomic) {
        this(atomic, 1);
    }
    
    /** Creates the AtomicIntegerFetchJob. */
    public AtomicIntegerFetchJob(final AtomicInteger atomic,
            final int delta) {
        Validate.notNull(atomic);
        
        this.atomic = atomic;
        this.delta = delta;
    }
    
    /** @see Runnable#run() */
    public void run() {
        atomic.addAndGet(delta);
    }
}
