/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.common.async.Job;

/**
 * {@link BlockingQueueJobQueue} que no entrega 
 * 
 * 
 * @author Juan F. Codagnone
 * @since Feb 25, 2010
 */
public class RateLimitBlockingQueueJobQueue extends BlockingQueueJobQueue {
    private final long queueTimeout;
    private long lastPool = 0;
    
    /** @see BlockingQueueJobQueue */
    public RateLimitBlockingQueueJobQueue(final BlockingQueue<Job> target, 
            final long queueTimeOut) {
        this(target, 500, queueTimeOut);
    }
    
    /**
     * @param target   Queue que se "wrappea". Es quien en realidad tiene 
     *                 implementada las operaciones
     * @param timeout  Como las {@link BlockingQueue} retornan null si no hay 
     *                 elementos, se le debe indicar un timeout de espera para
     *                 volver a ver si hay elementos (y de esta forma "blockear").
     *                 Está dicho en milisegundos.  
     * @param queueTimeout milisegundos minimos entre que se entregan dos requests
     * @param unit
     */
    public RateLimitBlockingQueueJobQueue(final BlockingQueue<Job> target,
            final long timeout, final long queueTimeout) {
        super(target, timeout);
        Validate.isTrue(queueTimeout >= 0);
        
        this.queueTimeout = queueTimeout;
    }
    
    /** @see BlockingQueueJobQueue#onJobDelivered(Job) */
    @Override
    public final void onJobDelivered(final Job job) throws InterruptedException {
        super.onJobDelivered(job);
        final long l = System.currentTimeMillis() - lastPool - queueTimeout;
        if(l < 0) {
            Thread.sleep(-l);
        }
        lastPool = System.currentTimeMillis();
    }
}
