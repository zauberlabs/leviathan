/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.common.async.Job;
import ar.com.zauber.leviathan.common.async.JobQueue;


/**
 * Prueba {@link RateLimitBlockingQueueJobQueue}
 * 
 * @author Juan F. Codagnone
 * @since Feb 25, 2010
 */
public class RateLimitBlockingQueueJobQueueTest {

    /** prueba */
    @Test(timeout = 10 * 1000)
    public final void prueba() throws Exception {
        final LinkedBlockingQueue<Job> queue = 
            new LinkedBlockingQueue<Job>();
        final AtomicInteger counter = new AtomicInteger(0);
        final int n = 5;
        final int timeout = 200;
        
        for(int i = 0; i < n; i++) {
            queue.add(new AtomicIntegerJob(counter));
        }
        final long t1 = System.currentTimeMillis();
        final JobQueue q = new RateLimitBlockingQueueJobQueue(queue, 500, timeout);
        q.shutdown();
        try {
            while(true) {
                q.poll().run();
            }
        } catch(InterruptedException e) {
            // ok
        }
        Assert.assertEquals(n, counter.get());
        final long t2 = System.currentTimeMillis();
        Assert.assertTrue("no paso el tiempo esperado", (t2 - t1) > timeout * n);
    }
    
    
}
