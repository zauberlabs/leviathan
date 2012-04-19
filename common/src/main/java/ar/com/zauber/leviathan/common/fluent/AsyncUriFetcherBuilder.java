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
package ar.com.zauber.leviathan.common.fluent;

import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.common.async.AsyncUriFetcherObserver;
import ar.com.zauber.leviathan.common.async.Job;
import ar.com.zauber.leviathan.common.async.JobQueue;
import ar.com.zauber.leviathan.common.async.JobScheduler;
import ar.com.zauber.leviathan.common.async.impl.MultiDomainPoliteJobQueue;
import ar.com.zauber.leviathan.common.async.impl.RateLimitBlockingQueueJobQueue;
import ar.com.zauber.leviathan.common.fluent.AsyncUriFetcherBuilder.SchedulerBuilder;
import ar.com.zauber.leviathan.common.fluent.AsyncUriFetcherBuilder.ThreadedExecutorService;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Juan F. Codagnone
 * @since Dec 29, 2011
 */
public interface AsyncUriFetcherBuilder {

    /** set up how we process the fetching part */
    AsyncUriFetcherBuilder withFetchingScheduler(JobScheduler scheduler);
    /** fluid {@link JobScheduler} builder */
    SchedulerBuilder withFetchingScheduler();
    
    /** set up how we process the content processing part */
    AsyncUriFetcherBuilder withProcessingScheduler(JobScheduler scheduler);
    /** fluid {@link JobScheduler} builder */
    SchedulerBuilder withProcessingScheduler();
    
    /** set an observer to monitor the flow */
    AsyncUriFetcherBuilder withFetchingObserver(AsyncUriFetcherObserver observer);
    
    /** builds the {@link AsyncUriFetcher} */
    AsyncUriFetcher build();
    
    
    public interface SchedulerBuilder {
        /** set up a custom job queue */
        SchedulerBuilder withQueue(final JobQueue<Job> queue);
        
        /** helps to build a polite job queue (waits some time before visiting a site twice) 
         * @see {@link MultiDomainPoliteJobQueue} */
        MultiDomainPoliteJobQueueBuilder withPoliteQueue();
        
        /** helps to build a rate limit queue (@see {@link RateLimitBlockingQueueJobQueue}) */
        RateLimitJobQueueBuilder withRateLimitQueue();
        
        /** use a simple blocking queue  */
        SchedulerBuilder withBloquingQueue(LinkedBlockingQueue<Job> linkedBlockingQueue);
        
        SchedulerBuilder withTimer(Timer timer, int i);
        
        /** select the {@link ExecutorService} to use */
        SchedulerBuilder withExecutorService(ExecutorService executorService);
        
        /** select the {@link ExecutorService} to use */
        ThreadedExecutorService withThreadedExecutorServicer();
        
        AsyncUriFetcherBuilder doneScheduler();
        

    }
    
    /** builds a {@link MultiDomainPoliteJobQueue}  */
    public interface MultiDomainPoliteJobQueueBuilder {
        /**
         * @param domainDelay tiempo a esperar entre requests a un mismo domain
         * @param unit Unidad de tiempo del delay
         */
        MultiDomainPoliteJobQueueBuilder minimalDelay(final long domainDelay, final TimeUnit unit);
        /** excludedDomains */
        MultiDomainPoliteJobQueueBuilder withException(String s);
        /** excludedDomains */
        MultiDomainPoliteJobQueueBuilder withExceptions(Iterable<String> s);
        
        /** done */
        SchedulerBuilder donePoliteQueue();
    }
    
    /** builds a {@link RateLimitBlockingQueueJobQueue}  */
    public interface RateLimitJobQueueBuilder {
        
        /**
         * La cola esta en realidad wrappea a una {@link BlockingQueue} de java. Con esto
         * se puede configurar que tipo usar.
         */
        RateLimitJobQueueBuilder withTargetQueue(final BlockingQueue<Job> target);
        
        /**
         * milisegundos entre respuesta (la cola no deja sacar más de 1 request cada tanto tiempo)
         */
        RateLimitJobQueueBuilder withThrowling(long throwlingInMs);
        
        /** 
         * cosa interna. como cambia la semantica de bloquing queue, este parametro le dice cada
         * cuanto poolear por nuevos elementos en ciertos casos. 
         */
        RateLimitJobQueueBuilder withPoolingTimeout(long timeout);
        
        /** build! */
        SchedulerBuilder doneRateLimitQueue();
        
    }
    
    interface ThreadedExecutorService {
        

        /** @see ThreadedExecutorService */
        ThreadedExecutorService withCorePoolSize(int i);

        /** @see ThreadedExecutorService */
        ThreadedExecutorService withMaximumPoolSize(int i);
        
        /** @see ThreadedExecutorService */
        ThreadedExecutorService withKeepAliveTimeOut(int i, TimeUnit seconds);
        
        /** build! */
        SchedulerBuilder doneThreadedExecutorService();

    }

}
