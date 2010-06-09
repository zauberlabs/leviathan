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
package ar.com.zauber.leviathan.common.async.impl;

import java.util.concurrent.LinkedBlockingQueue;
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
