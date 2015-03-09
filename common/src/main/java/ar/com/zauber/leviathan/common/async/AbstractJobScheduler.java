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
package ar.com.zauber.leviathan.common.async;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Apr 29, 2011
 */
public abstract class AbstractJobScheduler<T> implements Runnable {
    private final JobQueue<T> queue;
    private final Logger logger = LoggerFactory.getLogger(JobScheduler.class);
    
    
    /** Creates the FetcherScheduler. */
    public AbstractJobScheduler(final JobQueue<T> queue) {
        Validate.notNull(queue);

        this.queue = queue;
    }
    

    /** @see Runnable#run() */
    public final void run() {
        while(true) {
            try {
                doJob(queue.poll());
            } catch (final InterruptedException e) {
                if(queue.isShutdown()) {
                    logger.info("Interrupted poll(). Queue is shutting down");
                } else {
                    logger.warn("Interrupted poll() but we are not shutting down!",
                            e);
                }
                break;
            }
        }
        
        doShutdown();
    }
    
    /**
     * Process the job
     */
    protected abstract void doJob(T job) throws InterruptedException;
    
    /**
     * Shutdown properly
     */
    protected abstract void doShutdown();


    /** Returns the queue. */
    public final JobQueue<T> getQueue() {
        return queue;
    }
    
   
}
