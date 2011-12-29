/**
 * Copyright (c) 2009-2011 Zauber S.A. <http://www.zaubersoftware.com/>
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.zauber.leviathan.common.async.Job;
import ar.com.zauber.leviathan.common.async.JobQueue;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 * 
 * 
 * @author Mariano Cortesi
 * @since Apr 30, 2010
 */
public class MultiDomainPoliteJobQueue implements JobQueue<Job> {

    private static final Logger LOGGER = 
        LoggerFactory.getLogger(MultiDomainPoliteJobQueue.class);
    
    private final AtomicBoolean shutdownFlag = new AtomicBoolean(false);
    
    private final DelayQueue<FixedDelay<Job>> queue = 
        new DelayQueue<FixedDelay<Job>>();
    
    private final ConcurrentMap<String, Long> lastPolls = 
        new ConcurrentHashMap<String, Long>();
    
    private final Set<String> excludedDomains = new HashSet<String>();
    private final long domainDelay;
    
    /**
     * Creates the MultiDomainPoliteJobQueue.
     *
     * @param excludedDomains Dominios a excluir de la politica
     * @param domainDelay tiempo a esperar entre requests a un mismo domain
     * @param unit Unidad de tiempo del delay
     */
    public MultiDomainPoliteJobQueue(final Collection<String> excludedDomains, 
            final long domainDelay, final TimeUnit unit) {
        this.domainDelay = unit.toNanos(domainDelay);
        this.excludedDomains.addAll(excludedDomains);
    }

    /**
     * Creates the MultiDomainPoliteJobQueue sin dominios excluidos.
     * 
     * @see MultiDomainPoliteJobQueue#MultiDomainPoliteJobQueue(Collection, 
     *        long, TimeUnit)
     */
    public MultiDomainPoliteJobQueue(final long domainDelay, final TimeUnit unit) {
        this(Collections.<String>emptyList(), domainDelay, unit);
    }
    
    /** @see JobQueue#isShutdown() */
    public final boolean isShutdown() {
        return shutdownFlag.get();
    }

    /** @see JobQueue#shutdown() */
    public final void shutdown() {
        shutdownFlag.set(true);
    }

    /** @see JobQueue#add(Job) */
    public final void add(final Job fetchJob) throws RejectedExecutionException,
            InterruptedException {
        Validate.notNull(fetchJob, "null jobs are not accepted");
        if(shutdownFlag.get()) {
            throw new RejectedExecutionException(
            "We do not accept jobs, while shutting down");
        } else {
            long nanoDelay = getCurrentDomainDelay(fetchJob);
            
            this.queue.put(new FixedDelay<Job>(fetchJob, nanoDelay, 
                    TimeUnit.NANOSECONDS));            
        }
    }

    /**
     * @param fetchJob
     * @return
     */
    private long getCurrentDomainDelay(final Job fetchJob) {
        String domain = fetchJob.getUriAndCtx().getURI().getHost();
        long nanoDelay;
        if (domain == null
                || this.excludedDomains.contains(domain) 
                || !this.lastPolls.containsKey(domain)) {
            nanoDelay = 0;
        } else {
            nanoDelay = this.domainDelay 
                - (System.nanoTime() - this.lastPolls.get(domain));
        }
        return nanoDelay;
    }
    
    /** @see JobQueue#isEmpty() */
    public final boolean isEmpty() {
        return this.queue.isEmpty();
    }

    /** @see JobQueue#poll() */
    public final Job poll() throws InterruptedException {
        boolean hasJob = false;
        Job fetchJob = null;

        while (!hasJob) {
            long minDelay = this.domainDelay;
            final int queueSize = this.queue.size();
            
            for (int i = 0; i < queueSize; i++) {
                FixedDelay<Job> delayedJob = this.queue.poll();
                
                // only possible with concurrency
                if (delayedJob == null) {
                    break;
                }
                
                fetchJob = delayedJob.getElement();
                long currentDelay = getCurrentDomainDelay(fetchJob);
                if (currentDelay <= 0) {
                    hasJob = true;
                    break;
                } else {
                    minDelay = Math.min(minDelay, currentDelay);
                    this.queue.put(new FixedDelay<Job>(fetchJob, currentDelay, 
                            TimeUnit.NANOSECONDS));
                }
            }
            
            if (!hasJob) {
                if (queueSize == 0 && this.isShutdown()) {
                    throw new InterruptedException("queue is shutdown");
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Jobs needs a delay, waiting");
                    }
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(minDelay));
                }
            }
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Unqueued job: " + fetchJob.getUriAndCtx().getURI());
        }
        String host = fetchJob.getUriAndCtx().getURI().getHost();
        if(host != null) {
            this.lastPolls.put(host, System.nanoTime());
        }
        return fetchJob;
    }

    @Override
    public final int size() {
        return this.queue.size();
    }

}

