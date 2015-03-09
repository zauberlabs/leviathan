/**
 * Copyright (c) 2009-2015 Zauber S.A. <http://zauberlabs.com/>
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
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.common.async.JobQueue;

/**
 * {@link JobQueue} que delega su funcionamiento en un {@link BlockingQueue}.
 * 
 * @author Juan F. Codagnone
 * @since Feb 16, 2010
 */
public class BlockingQueueJobQueue<T> implements JobQueue<T> {
    private final BlockingQueue<T> target;
    private AtomicBoolean shutdownFlag = new AtomicBoolean(false);
    private final long timeout;
    
    /** @see BlockingQueueJobQueue */
    public BlockingQueueJobQueue(final BlockingQueue<T> target) {
        this(target, 500);
    }
    
    /**
     * @param target   Queue que se "wrappea". Es quien en realidad tiene 
     *                 implementada las operaciones
     * @param timeout  Como las {@link BlockingQueue} retornan null si no hay 
     *                 elementos, se le debe indicar un timeout de espera para
     *                 volver a ver si hay elementos (y de esta forma "blockear").
     *                 Está dicho en milisegundos.  
     */
    public BlockingQueueJobQueue(final BlockingQueue<T> target,
            final long timeout) {
        Validate.notNull(target, "target is null");
        Validate.isTrue(timeout > 0, "timeout must be positive");
        
        this.target = target;
        this.timeout = timeout;
    }
    
    
    /**  @see JobQueue#add(Job) */
    public final void add(final  T fetchJob) throws InterruptedException {
        Validate.notNull(fetchJob, "null jobs are not accepted");
        if(shutdownFlag.get()) {
            throw new RejectedExecutionException(
                    "We do not accept jobs, while shutting down");
        } else {
            target.put(fetchJob);
        }
    }

    /** @see JobQueue#isEmpty() */
    public final boolean isEmpty() {
        return target.isEmpty();
    }

    /** template method usado para notificar que se llamó al pool. es interesante
     *  para armar los test. luego de que alguien hace pool, hacer un add. etc */
    public void onPoll() {
        // void
    }
    
    /** template method usado para notificar que se obtuvo un Job; y que será 
     * retornado al usuario */
    public void onJobDelivered(final T job) throws InterruptedException {
        // void
    }
    
    /** @see JobQueue#poll() */
    public final T poll() throws InterruptedException {
        T job;
        onPoll();
        do {
            job = target.poll(timeout, TimeUnit.MILLISECONDS);
            
            // no hay nada en la cola, y notificaron para el shutdown.
            if(job == null && shutdownFlag.get() && target.isEmpty()) {
                throw new InterruptedException("Shutting down");
            }
        } while(job == null);

        onJobDelivered(job);
        return job;
    }
    
    /** @see JobQueue#shutdown() */
    public final void shutdown() {
        shutdownFlag.set(true);
    }
    
    /** @see JobQueue#isShutdown() */
    public final boolean isShutdown() {
        return shutdownFlag.get();
    }

    public int size() {
        return this.target.size();
    }
}
