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
package ar.com.zauber.leviathan.common;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.async.FetchQueueAsyncUriFetcher;

/**
 * Clase base para los {@link AsyncUriFetcher}.
 * 
 * Las implementaciones deben (para que funcione debidamente el 
 * {@link #awaitIdleness()}) llamar al {@link #incrementActiveJobs()} cuando
 * hay nuevas tareas (y si falla el procesamiento de la misma (ej: no se puede
 * aceptar como trabajo; restituir el contador con {@link #decrementActiveJobs()}).
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public abstract class AbstractAsyncUriFetcher implements AsyncUriFetcher {
    // para implementar el awaitIdleness
    private final Lock lock = new ReentrantLock();
    private final Condition emptyCondition  = lock.newCondition(); 
    private final AtomicLong activeJobs = new AtomicLong(0);
    private final Logger logger = Logger.getLogger(getClass());
    
    /** @see AsyncUriFetcher#get(URI, Closure) */
    public final void get(final URI uri, 
            final Closure<URIFetcherResponse> closure) {
        get(new InmutableURIAndCtx(uri), closure);
    }
    
    
    

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
