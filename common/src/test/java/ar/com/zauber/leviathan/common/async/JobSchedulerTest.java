/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.common.async.impl.AtomicIntegerJob;
import ar.com.zauber.leviathan.common.async.impl.BlockingQueueJobQueue;


/**
 * Tests {@link JobSchedulerTest}.
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class JobSchedulerTest {

    /**
     * Prueba el funcionamiento del {@link JobScheduler}. 
     * Encola 3 tareas. Las primeras dos incrementan un entero, y la
     * tercera incicia el shutdown.
     */
    @Test(timeout = 2000)
    public final void testConsumeAndShutdown() {
        final JobQueue queue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        final AtomicInteger i = new AtomicInteger(0);
        final Job job = new AtomicIntegerJob(i);
        queue.add(job);
        queue.add(job);
        queue.add(new Job() {
            public void run() {
                queue.shutdown();
            }
        });
        
        Assert.assertEquals(0, i.get());
        final ExecutorService executorService = new DirectExecutorService();
        new JobScheduler(queue, executorService).run();
        Assert.assertEquals(2, i.get());
        Assert.assertTrue(executorService.isShutdown());
    }
}
