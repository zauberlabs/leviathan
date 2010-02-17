/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.common.async.impl.BlockingQueueFetchQueue;


/**
 * Tests {@link FetcherSchedulerTest}.
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class FetcherSchedulerTest {

    /**
     * Prueba el funcionamiento del {@link FetcherScheduler}. 
     * Encola 3 tareas. Las primeras dos incrementan un intero, y la
     * tercera incicia el shutdown.
     */
    @Test(timeout = 2000)
    public final void testConsumeAndShutdown() {
        final FetchQueue queue = new BlockingQueueFetchQueue(
                new LinkedBlockingQueue<FetchJob>());
        final AtomicInteger i = new AtomicInteger(0);
        
        queue.add(new FetchJob() {
            public void run() {
                i.addAndGet(1);
            }
        });
        queue.add(new FetchJob() {
            public void run() {
                i.addAndGet(1);
            }
        });
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
