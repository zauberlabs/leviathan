/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.UnhandledException;
import org.junit.Assert;
import org.junit.Test;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.async.impl.BlockingQueueJobQueue;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;


/**
 * Tests {@link FetchQueueAsyncUriFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class FetchQueueAsyncUriFetcherTest {

    /** intenta cerra un fetcher ya cerrado */
    @Test(timeout = 2000)
    public final void shutdownShiny() {
        final JobQueue fetcherQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        final JobQueue processingQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        
        final AsyncUriFetcher fetcher = new FetchQueueAsyncUriFetcher(
                new FixedURIFetcher(new HashMap<URI, String>()), 
                new JobScheduler(fetcherQueue, Executors.newSingleThreadExecutor()),
                new JobScheduler(processingQueue, Executors.newSingleThreadExecutor())
        );
        fetcher.shutdown();
    }
    
    /** 
     * Arma un fetcher, le da urls para fetchear.
     * y llama al shutdown. 
     */
//    @Test(timeout = 2000)
    @Test
    public final void poll() throws URISyntaxException {
        final JobQueue fetcherQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        final JobQueue processingQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        
        final AsyncUriFetcher fetcher = new FetchQueueAsyncUriFetcher(
              new FixedURIFetcher(new HashMap<URI, String>()), 
              new JobScheduler(processingQueue, Executors.newSingleThreadExecutor()),
              new JobScheduler(fetcherQueue, Executors.newSingleThreadExecutor()));
        
        final AtomicInteger i = new AtomicInteger(0);
        
        final Closure<URIFetcherResponse> closure = 
            new Closure<URIFetcherResponse>() {
            public void execute(final URIFetcherResponse t) {
                i.addAndGet(1);
            }
        };
        
        for(int j = 0; j < 100; j++) {
            fetcher.fetch(new URI("http://foo"), closure);
        }
        fetcher.shutdown();
        Assert.assertEquals(100, i.get());
    }
    

    /**
     * Prueba que el scheduler no termine si se queda sin jobs luego de haber
     * procesado algunos. 
     * 
     *  - Se inicia el fetcher
     *  - Se encola una tarea
     *  - Cuando se está terminando de procesar; nos aseguramos que la cola
     *    esté vacia. Cuando esto sucede agrega (via un truco en el onPoll de la
     *    cola nuevas tareas para su ejecución). 

     * @throws InterruptedException 
     * @throws URISyntaxException 
     */
    @Test(timeout = 2000)
    public final void testConsumeWaitConsumeAndShutdown() 
        throws InterruptedException, URISyntaxException {
        // cantidad de tareas a encolar una vez que se termina de procesar la primera
        final int cantTasks = 100;
        
        final CountDownLatch waitForEndTask1 = new CountDownLatch(1);
        final CountDownLatch finish = new CountDownLatch(cantTasks);
        final AtomicBoolean firstTaskDone = new AtomicBoolean(false);
        final AtomicInteger tasksAfterFirstTask = new AtomicInteger(0);

        
        final List<AsyncUriFetcher> fetcherHolder = 
            new ArrayList<AsyncUriFetcher>(1);
        final URI uri = new URI("http://123");
        
        final JobQueue queue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>()) {
            private final AtomicInteger i = new AtomicInteger(0);
            public void onPoll() {
                i.addAndGet(1);
                
                if(i.get() == 2) {
                    try {
                        Assert.assertFalse("faltó cargar el holder", 
                                fetcherHolder.isEmpty());
                        // agregamos cantTasks trabajos cuando se está terminando
                        // de procesar la tarea primera.
                        waitForEndTask1.await();
                        for(int i = 0; i < cantTasks; i++) {
                            fetcherHolder.iterator().next().fetch(uri, 
                                    new Closure<URIFetcherResponse>() {
                                public void execute(final URIFetcherResponse t) {
                                    tasksAfterFirstTask.addAndGet(1);
                                    finish.countDown();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        throw new UnhandledException(e);
                    }
                }
            };
        };
        final JobQueue processingQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        
        final AsyncUriFetcher fetcher = new FetchQueueAsyncUriFetcher(
            new FixedURIFetcher(new HashMap<URI, String>()), 
            new JobScheduler(queue, Executors.newSingleThreadExecutor()),
            new JobScheduler(processingQueue, Executors.newSingleThreadExecutor()));
        fetcherHolder.add(fetcher);
        fetcher.fetch(uri, new Closure<URIFetcherResponse>() {
            public void execute(final URIFetcherResponse t) {
                firstTaskDone.set(true);
                Assert.assertTrue(queue.isEmpty());
                waitForEndTask1.countDown();
            }
        });
        
        finish.await(); // espera que onPoll() agrege todas las tareas.
        fetcher.shutdown();
        Assert.assertEquals(cantTasks, tasksAfterFirstTask.get());
    }
    
    /**
     * Prueba que el scheduler no termine si se queda sin jobs luego de haber
     * procesado algunos. 
     * 
     *  - Se inicia el fetcher
     *  - Se encola una tarea
     *  - Cuando se está terminando de procesar; nos aseguramos que la cola
     *    esté vacia. Cuando esto sucede agrega (via un truco en el onPoll de la
     *    cola nuevas tareas para su ejecución). 

     * @throws InterruptedException 
     * @throws URISyntaxException 
     */
    @Test(timeout = 2000)
    public final void testConsumeWaitConsumeAndShutdownMetralleta() 
        throws InterruptedException, URISyntaxException {
        // cantidad de tareas a encolar una vez que se termina de procesar la primera
        final int cantTasks = 100;
        
        final CountDownLatch waitForEndTask1 = new CountDownLatch(1);
        final CountDownLatch finish = new CountDownLatch(1);
        final AtomicBoolean firstTaskDone = new AtomicBoolean(false);
        final AtomicInteger tasksAfterFirstTask = new AtomicInteger(0);

        
        final List<AsyncUriFetcher> fetcherHolder = 
            new ArrayList<AsyncUriFetcher>(1);
        final URI uri = new URI("http://123");
        
        final JobQueue queue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>()) {
            private final AtomicInteger i = new AtomicInteger(0);
            public void onPoll() {
                i.addAndGet(1);
                
                if(i.get() == 2) {
                    try {
                        Assert.assertFalse("faltó cargar el holder", 
                                fetcherHolder.isEmpty());
                        // agregamos cantTasks trabajos cuando se está terminando
                        // de procesar la tarea primera.
                        waitForEndTask1.await();
                        for(int i = 0; i < cantTasks; i++) {
                            fetcherHolder.iterator().next().fetch(uri, 
                                    new Closure<URIFetcherResponse>() {
                                public void execute(final URIFetcherResponse t) {
                                    tasksAfterFirstTask.addAndGet(1);
                                }
                            });
                        }
                        finish.countDown();
                    } catch (InterruptedException e) {
                        throw new UnhandledException(e);
                    }
                }
            };
        };
        final JobQueue processingQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        
        final AsyncUriFetcher fetcher = new FetchQueueAsyncUriFetcher(
           new FixedURIFetcher(new HashMap<URI, String>()), 
           new JobScheduler(queue, Executors.newSingleThreadExecutor()),
           new JobScheduler(processingQueue, Executors.newSingleThreadExecutor()));
        fetcherHolder.add(fetcher);
        fetcher.fetch(uri, new Closure<URIFetcherResponse>() {
            public void execute(final URIFetcherResponse t) {
                firstTaskDone.set(true);
                Assert.assertTrue(queue.isEmpty());
                waitForEndTask1.countDown();
            }
        });
        
        finish.await(); // espera que onPoll() agrege todas las tareas.
        fetcher.shutdown();
        Assert.assertEquals(cantTasks, tasksAfterFirstTask.get());
    }
    
    /**
     *  Verifica que los dos schedulers no compartan las misma colas. 
     */
    @Test(timeout = 2000)
    public final void avoidUsingSameQueue() {
        final JobQueue queue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>());
        
        try {
            new FetchQueueAsyncUriFetcher(
                    new FixedURIFetcher(new HashMap<URI, String>()), 
                    new JobScheduler(queue, Executors.newSingleThreadExecutor()),
                    new JobScheduler(queue, Executors.newSingleThreadExecutor()));
            Assert.fail();
        } catch(IllegalArgumentException e) {
            Assert.assertEquals("Los schedulers de fetcher y procesamiento no "
                    + "pueden compartir la misma queue", e.getMessage());
        }
    }
}
