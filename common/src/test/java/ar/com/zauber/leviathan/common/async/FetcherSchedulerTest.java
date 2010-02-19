/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import ar.com.zauber.leviathan.common.async.impl.AtomicIntegerFetchJob;
import ar.com.zauber.leviathan.common.async.impl.BlockingQueueFetchQueue;
import ar.com.zauber.leviathan.common.async.impl.NullFetchJob;


/**
 * Tests {@link FetcherSchedulerTest}.
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class FetcherSchedulerTest {

    /**
     * Prueba el funcionamiento del {@link FetcherScheduler}. 
     * Encola 3 tareas. Las primeras dos incrementan un entero, y la
     * tercera incicia el shutdown.
     */
    @Test(timeout = 2000)
    public final void testConsumeAndShutdown() {
        final FetchQueue queue = new BlockingQueueFetchQueue(
                new LinkedBlockingQueue<FetchJob>());
        final AtomicInteger i = new AtomicInteger(0);
        final FetchJob job = new AtomicIntegerFetchJob(i);
        queue.add(job);
        queue.add(job);
        queue.add(new FetchJob() {
            public void run() {
                queue.shutdown();
            }
        });
        
        Assert.assertEquals(0, i.get());
        final ExecutorService executorService = new DirectExecutorService();
        new FetcherScheduler(queue, executorService).run();
        Assert.assertEquals(2, i.get());
        Assert.assertTrue(executorService.isShutdown());
    }
}
