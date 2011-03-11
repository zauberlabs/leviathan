package ar.com.zauber.leviathan.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import ar.com.zauber.leviathan.api.AsyncUriFetcher;

/**
 * Esqueleto que ayuda a llevar la cuenta de tareas que se están ejecutando de forma asincronica
 * y a esperar que todas terminen.
 * 
 * @author Juan F. Codagnone
 * @since Mar 11, 2011
 */
public abstract class AbstractAsyncTaskExecutor {
    // para implementar el awaitIdleness
    private final Lock lock = new ReentrantLock();
    private final Condition emptyCondition  = lock.newCondition(); 
    private final AtomicLong activeJobs = new AtomicLong(0);
    private final Logger logger = LoggerFactory.getLogger(getClass());

    
    

    /** @see AsyncUriFetcher#awaitIdleness() */
    public final void awaitIdleness() throws InterruptedException {
        lock.lock();
        try {
            while(activeJobs.get() != 0) {
                emptyCondition.await();
            }
        } finally {
            lock.unlock();
        }
    }
    /** @see AsyncUriFetcher#awaitIdleness(long, TimeUnit) */
    public final boolean awaitIdleness(final long timeout, final TimeUnit unit) 
        throws InterruptedException {
        boolean timedOut = false;
        lock.lock();
        try {
            // no hago esto desde un while ya que un spurious wakeup puede ser
            // interpretado como un wakeup.
            timedOut = emptyCondition.await(timeout, unit);
        } finally {
            lock.unlock();
        }
        
        return timedOut;
    }
    

    /** 
     *  decrementa la cantidad de trabajos activos y notifica a quien 
     *  este esperando por idleness si se llegó a 0.
     */
    protected final void decrementActiveJobs() {
        lock.lock();
        try {
            if(activeJobs.decrementAndGet() == 0) {
                emptyCondition.signalAll();
            }
        } catch(final Throwable t) {
            logger.error("decrementing active jobs. should not happen ", t);
            // nada que relanzar no queremos molestar upstream.
        } finally {
            lock.unlock();
        }
    }
    
    /** incrementa la cantidad de trabajos activos */
    protected final void incrementActiveJobs() {
        lock.lock();
        try {
            activeJobs.incrementAndGet();
        } catch(final Throwable t) {
            logger.error("incrementing  active jobs. should not happen ", t);
            // nada que relanzar no queremos molestar upstream.
        } finally {
            lock.unlock();
        }
    }
}
