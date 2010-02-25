/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.utils;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.commons.lang.UnhandledException;
import org.junit.Test;


/**
 * {@link BlockingRejectedExecutionHandler} test
 * 
 * 
 * @author Juan F. Codagnone
 * @since Feb 25, 2010
 */
public class BlockingRejectedExecutionHandlerTest {

    /**
     * Prueba la efectividad de {@link BlockingRejectedExecutionHandler} 
     * utilizando una cola bloqueante. 
     */
    @Test(timeout = 2000)
    public final void foo() throws InterruptedException {
        final ExecutorService s = new ThreadPoolExecutor(1, 1, 0, 
                TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
                new BlockingRejectedExecutionHandler());
        final AtomicInteger i = new AtomicInteger(0);
        
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(1);
        s.submit(new Runnable() {
            public void run() {
                try {
                    latch.await();
                    Thread.sleep(1000);
                    i.incrementAndGet();
                } catch (InterruptedException e) {
                    throw new UnhandledException(e);
                }
            }
        });
        latch.countDown();
        s.submit(new Runnable() {
            public void run() {
                i.incrementAndGet();
                end.countDown();
            }
        });
        end.await();
        s.shutdown();
        while(!s.awaitTermination(500, TimeUnit.MILLISECONDS)) {
            // todo
        }
        Assert.assertEquals(2, i.get());
    }
}
