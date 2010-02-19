/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Consume FetchQueue y los pone a ejecutar en un ExecutorService.
 * Exista para garantizar algunas cosas. Si esto fuera directamente un
 * executorservice, los trabajos podrian ir directamente al thread y no a la cola, y 
 * eso no nos permitaria tener politicas de polite.  
 *    
 * @author Juan F. Codagnone
 * @since Feb 16, 2010
 */
public class JobScheduler implements Runnable {
    private final JobQueue queue;
    private final ExecutorService executorService;
    private final Logger logger = Logger.getLogger(JobScheduler.class);
    
    /** Creates the FetcherScheduler. */
    public JobScheduler(final JobQueue queue, 
            final ExecutorService executorService) {
        Validate.notNull(queue);
        Validate.notNull(executorService);
        
        this.queue = queue;
        this.executorService = executorService;
        
    }

    /** @see Runnable#run() */
    public final void run() {
        while(true) {
            try {
                executorService.execute(queue.poll());
            } catch (final InterruptedException e) {
                if(queue.isShutdown()) {
                    logger.info("Interrupted poll(). Queue is shutting down");
                } else {
                    logger.log(Level.WARN, 
                            "Interrupted poll() but we are not shutting down!", e);
                }
                break;
            }
        }
        // si es que nos encontramos aqui ya no quedó nada por consumir  
        executorService.shutdown();
        
        while(!executorService.isTerminated()) {
            try {
                executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException e) {
                logger.log(Level.WARN, 
                  "Interrupted awaitTermination on FetcherScheduler shutdown", e);
            }
        }
    }
}
