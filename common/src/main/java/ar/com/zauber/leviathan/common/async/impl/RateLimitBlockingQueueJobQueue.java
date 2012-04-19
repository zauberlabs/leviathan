/**
 * Copyright (c) 2009-2012 Zauber S.A. <http://zauberlabs.com/>
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

import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.Validate;

/**
 * {@link BlockingQueueJobQueue} que no entrega 
 * 
 * 
 * @author Juan F. Codagnone
 * @since Feb 25, 2010
 */
public class RateLimitBlockingQueueJobQueue<T> extends BlockingQueueJobQueue<T> {
    private final long queueTimeout;
    private long lastPool = 0;
    
    /** @see BlockingQueueJobQueue */
    public RateLimitBlockingQueueJobQueue(final BlockingQueue<T> target, 
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
    public RateLimitBlockingQueueJobQueue(final BlockingQueue<T> target,
            final long timeout, final long queueTimeout) {
        super(target, timeout);
        Validate.isTrue(queueTimeout >= 0);
        
        this.queueTimeout = queueTimeout;
    }
    
    /** @see BlockingQueueJobQueue#onJobDelivered(Job) */
    @Override
    public final void onJobDelivered(final T job) throws InterruptedException {
        super.onJobDelivered(job);
        final long l = System.currentTimeMillis() - lastPool - queueTimeout;
        if(l < 0) {
            Thread.sleep(-l);
        }
        lastPool = System.currentTimeMillis();
    }
}
