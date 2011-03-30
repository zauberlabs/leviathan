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
package ar.com.zauber.leviathan.common.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import junit.framework.Assert;

import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import ar.com.zauber.leviathan.common.async.impl.BlockingQueueJobQueue;
import ar.com.zauber.leviathan.common.async.impl.NullJob;


/**
 * Tests {@link BlockingQueueJobQueue}.
 * 
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public class BlockingQueueJobQueueTest {

    /** crea una queue */
    private JobQueue create() {
        return new BlockingQueueJobQueue(new LinkedBlockingQueue<Job>());
    }
    
    /**
     *  una vez que se hizo un {@link JobQueue#shutdown()} no se deben 
     *  aceptar nuevas tareas
     */
    @Test
    public final void testShutdownRejectNewTasksNotEmptyQueue() 
        throws InterruptedException {
        final JobQueue fetchQueue = create(); 
        
        fetchQueue.add(new NullJob());
        fetchQueue.shutdown();
        try {
            fetchQueue.add(new NullJob());
            Assert.fail("No se debia aceptar la tareas luego de un shutdown");
        } catch(RejectedExecutionException e) {
            // ok
        }
    }
    
    /**
     *  una vez que se hizo un {@link JobQueue#shutdown()} no se deben 
     *  aceptar nuevas tareas
     */
    @Test
    public final void testShutdownRejectNewTasksEmptyQueue() 
      throws InterruptedException {
        final JobQueue fetchQueue = create();
        
        fetchQueue.shutdown();
        try {
            fetchQueue.add(new NullJob());
            Assert.fail("No se debia aceptar la tareas luego de un shutdown");
        } catch(RejectedExecutionException e) {
            // ok
        }
    }
    
    /**
     *  No se aceptan tareas nulas
     */
    @Test
    public final void testValidateNullJobs() throws InterruptedException {
        final JobQueue fetchQueue = create();
        try {
            fetchQueue.add(null);
            Assert.fail("No debe aceptar tareas nulas");
        } catch(IllegalArgumentException e) {
            // ok
        }
    }
    
    /**
     *  Verifica que el poll sea sincronico cuando no existan datos.
     *  
     *  El thread principal pide un poll() en una cola vacia. Esta se debería
     *  quedar bloqueado hasta que se agrege un elemento en la cola o se haga 
     *  shutdown.
     *  
     *  Desde otro thread, se espera hasta el momento antes que se hace el pool()
     *  y entonces se agrega un elemento.
     *  
     * @throws InterruptedException 
     */
    @Test(timeout = 2000)
    public final void testBlockingPoll() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        
        // Uso una cola que no se bloquea (capacidad infinita) es importante...
        final JobQueue fetchQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>()) {
            public void onPoll() {
                // disparamos el otro thread.
                latch.countDown();
            };
        };
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    latch.await();
                    // esto no deberia ser necesario, ya que el latch nos 
                    // avisó que "casi se hizo el poll()". Pero para estar
                    // seguros de  la prueba lo agrego
                    Thread.sleep(200);
                    fetchQueue.add(new NullJob());
                } catch (InterruptedException e) {
                    throw new UnhandledException(e);
                }
            }
        }).start();
        
        final Job job = fetchQueue.poll();
        Assert.assertNotNull("la cola nunca debe retorar null (segun javadoc)", 
                job);
    }
    
    /**
     *  Verifica que el poll sea sincronico y que con el shutdown termine
     *  su ejecucipon.
     *  
     *  El thread principal pide un poll() en una cola vacia. Esta se debería
     *  quedar bloqueado hasta que se agrege un elemento en la cola o se haga 
     *  shutdown.
     *  
     *  Desde otro thread, se espera hasta el momento antes que se hace el poll()
     *  y entonces se llama al shutdown.
     */  
    @Test(timeout = 200000)
    public final void testBlockingPollAndShutdown() {
        final CountDownLatch latch = new CountDownLatch(1);
        
        // Uso una cola que no se bloquea (capacidad infinita) es importante...
        final JobQueue fetchQueue = new BlockingQueueJobQueue(
                new LinkedBlockingQueue<Job>()) {
            public void onPoll() {
                // cuando se hace un poll() se espera un poco para verificar
                // que el otro thread
                latch.countDown();
            };
        };
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    latch.await();
                    // esto no deberia ser necesario, ya que el latch nos 
                    // avisó que "casi se hizo el poll()". Pero para estar
                    // seguros de  la prueba lo agrego
                    Thread.sleep(200);
                    fetchQueue.shutdown();
                } catch (InterruptedException e) {
                    throw new UnhandledException(e);
                }
            }
        }).start();
        
        Job job = null;
        try {
            job = fetchQueue.poll();
            Assert.fail("codigo al cual no se deberia llegar");
        } catch (final InterruptedException e) {
            Assert.assertNull("job deberia ser nulo ya que se deberia "
                    + "interrumpir por shutdown", job);
            // ok
        }
    }
}
