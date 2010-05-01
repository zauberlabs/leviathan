/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
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
import org.apache.log4j.Logger;

import ar.com.zauber.leviathan.common.async.Job;
import ar.com.zauber.leviathan.common.async.JobQueue;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 * 
 * 
 * @author Mariano Cortesi
 * @since Apr 30, 2010
 */
public class MultiDomainPoliteJobQueue implements JobQueue {

    private final static Logger LOGGER = 
        Logger.getLogger(MultiDomainPoliteJobQueue.class);
    
    private final AtomicBoolean shutdownFlag = new AtomicBoolean(false);
    
    private final DelayQueue<FixedDelay<Job>> queue = 
        new DelayQueue<FixedDelay<Job>>();
    
    private final ConcurrentMap<String, Long> lastPolls = 
        new ConcurrentHashMap<String, Long>();
    
    private final Set<String> excludedDomains = new HashSet<String>();
    private final long domainDelay;
    
    public MultiDomainPoliteJobQueue(Collection<String> excludedDomains, long domainDelay, TimeUnit unit) {
        this.domainDelay = unit.toNanos(domainDelay);
        this.excludedDomains.addAll(excludedDomains);
    }

    public MultiDomainPoliteJobQueue(long domainDelay, TimeUnit unit) {
        this(Collections.<String>emptyList(), domainDelay, unit);
    }
    
    /** @see JobQueue#isShutdown() */
    public boolean isShutdown() {
        return shutdownFlag.get();
    }

    /** @see JobQueue#shutdown() */
    public void shutdown() {
        shutdownFlag.set(true);
    }

    /** @see JobQueue#add(Job) */
    public void add(Job fetchJob) throws RejectedExecutionException,
            InterruptedException {
        Validate.notNull(fetchJob, "null jobs are not accepted");
        if(shutdownFlag.get()) {
            throw new RejectedExecutionException(
            "We do not accept jobs, while shutting down");
        } else {
            long nanoDelay = getCurrentDomainDelay(fetchJob);
            
            this.queue.put(new FixedDelay<Job>(fetchJob, nanoDelay, TimeUnit.NANOSECONDS));            
        }
    }

    /**
     * @param fetchJob
     * @return
     */
    private long getCurrentDomainDelay(Job fetchJob) {
        String domain = fetchJob.getUriAndCtx().getURI().getHost();
        
        long nanoDelay;
        if (this.excludedDomains.contains(domain) || !this.lastPolls.containsKey(domain)) {
            nanoDelay = 0;
        } else {
            nanoDelay = this.domainDelay - (System.nanoTime() - this.lastPolls.get(domain));
        }
        return nanoDelay;
    }
    
    /** @see JobQueue#isEmpty() */
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    /** @see JobQueue#poll() */
    public Job poll() throws InterruptedException {
        boolean hasJob = false;
        Job fetchJob = null;

        while (!hasJob) {
            long minDelay = this.domainDelay;
            final int queueSize = this.queue.size();
            
            for (int i=0; i < queueSize; i++) {
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
                    this.queue.put(new FixedDelay<Job>(fetchJob, currentDelay, TimeUnit.NANOSECONDS));
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
        this.lastPolls.put(fetchJob.getUriAndCtx().getURI().getHost(), 
                System.nanoTime());
        return fetchJob;
    }    

}

