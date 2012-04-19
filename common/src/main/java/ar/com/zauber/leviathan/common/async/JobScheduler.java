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
package ar.com.zauber.leviathan.common.async;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Consume FetchQueue y los pone a ejecutar en un ExecutorService.
 * Exista para garantizar algunas cosas. Si esto fuera directamente un
 * executorservice, los trabajos podrian ir directamente al thread y no a la cola, y 
 * eso no nos permitaria tener politicas de polite.  
 * </p>
 * 
 * <p>
 * Se puede especificar un Timer y un timeout; los cuales limitan el tiempo de 
 * ejecución de los {@link Job}. Hay que recordar que estos deben ser interruptibles.
 * </p>
 * 
 * @author Juan F. Codagnone
 * @since Feb 16, 2010
 */
public class JobScheduler extends AbstractJobScheduler<Job> {
    private final ExecutorService executorService;
    private final Logger logger = LoggerFactory.getLogger(JobScheduler.class);
    private Timer timer;
    private long timeout = 0;
    private final AtomicBoolean shutdownNowFlag = new AtomicBoolean(false);
    
    /** Creates the FetcherScheduler. */
    public JobScheduler(final JobQueue<Job> queue, 
            final ExecutorService executorService) {
        this(queue, executorService, null, 0);
    }
    
    /** Creates the FetcherScheduler. */
    public JobScheduler(final JobQueue<Job> queue, 
            final ExecutorService executorService,
            final Timer timer, final long timeout) {
        super(queue);
        Validate.notNull(queue);
        Validate.notNull(executorService);
        if(timer != null) {
            Validate.isTrue(timeout > 0);
        }
        
        this.executorService = executorService;
        this.timer = timer;
        this.timeout = timeout;
        
    }
    
    @Override
    protected void doJob(final Job job) {
        final Future<?> future = executorService.submit(job);
        if(timer != null && !future.isDone()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!future.isDone()) {
                        future.cancel(true);
                        if(logger.isDebugEnabled()) {
                            logger.debug("Timeout while processing job "
                                    + job);
                        }
                    }
                }
            }, timeout);
        }
        
    }

    @Override
    protected void doShutdown() {
        if(shutdownNowFlag.get()) {
            executorService.shutdownNow();
        } else {
            // si es que nos encontramos aqui ya no quedó nada por consumir  
            executorService.shutdown();
            
            while(!executorService.isTerminated()) {
                try {
                    executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
                } catch (final InterruptedException e) {
                    logger.warn(
                      "Interrupted awaitTermination on FetcherScheduler shutdown",
                      e);
                }
            }
        }
        
    }
    
    /** Returns the queue. */
    public final void shutdownNow() {
        shutdownNowFlag.set(true);
    }
}
