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
