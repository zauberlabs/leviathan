/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ar.com.zauber.leviathan.common.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.async.impl.AtomicIntegerJob;
import ar.com.zauber.leviathan.common.async.impl.BlockingQueueJobQueue;
import ar.com.zauber.leviathan.common.utils.DirectExecutorService;


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
    public final void testConsumeAndShutdown() throws InterruptedException {
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
            /** @see Job#getUriAndCtx() */
            public URIAndCtx getUriAndCtx() {
                throw new NotImplementedException();
            }            
        });
        
        Assert.assertEquals(0, i.get());
        final ExecutorService executorService = new DirectExecutorService();
        new JobScheduler(queue, executorService).run();
        Assert.assertEquals(2, i.get());
        Assert.assertTrue(executorService.isShutdown());
    }
}
