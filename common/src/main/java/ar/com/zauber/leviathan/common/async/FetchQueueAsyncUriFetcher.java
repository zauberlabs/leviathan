/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
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

import static ar.com.zauber.leviathan.common.async.ThreadUtils.*;

import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.AbstractAsyncUriFetcher;
import ar.com.zauber.leviathan.common.InmutableURIFetcherResponse;
import ar.com.zauber.leviathan.common.async.impl.DebugLoggerAsyncUriFetcherObserver;

/**
 * {@link AsyncUriFetcher} que utiliza dos colas (de tipo  {@link JobQueue}
 * internas para realizar el trabajo. 
 * 
 * El flujo de la  informaci�n es mas o menos as�:
 * <ol>
 *   <li>el cliente llama a {@link #scheduleFetch(URIAndCtx, Closure)}</li>
 *   <li>Todo nuevo trabajo es encolado en una Fetch Queue. Dependiendo de la 
 *    naturaleza de la cola (limitada o infinita), si lleg� a su capacidad total 
 *    podria bloquear hasta que haya espacio. 
 *   </li>
 *   <li>Una vez encolado el trabajo se retorna la ejecuci�n al cliente</li>
 *   <li>Existe otro thread que es el fetch scheduler (de tipo {@link JobScheduler})
 *       que se encarga de consumir las tareas de fetching de la cola. Quita
 *       de la cola la tarea y la pone ejecutar en un {@link ExecutorService}</li>
 *   <li>Cuando el fetching termina (ya sea OK, por error o timeout), el trabajo
 *       se encola una cola de procesamiento (tambi�n de tipo {@link JobQueue}).</li>
 *   <li>Existe otro thread (el processing scheduler) que consume todo lo que 
 *       se encol� para procesar y lo pone a procesar en un pool de workers 
 *       (tamb�en un executor service). Poner a ejecutar significa ejecutar el
 *       {@link Closure} del usuario.</li>
 * </ol>
 * <pre>
 *                          Fetch workers
 *                               _
 *                              |_|
 *                              |_|                                 processing
 *                              |_|                                  workers
 *  +-------+    +----------+   |_|   +------------+   +----------+    _
 *  | Fetch |    |  fetch   |   |_|   | processing |   |processing|   |_|
 *  | Queue |-->-| scheduler|->-|_|->-|    queue   |->-|scheduler |->-|_|-->
 *  +-------+    +----------+   |_|   +------------+   +----------+   |_|
 *                              |_|
 *                              |_|
 * </pre>
 * 
 * <p>
 * Los closures de los usuarios, por ejemplo pueden procesar el documento que se
 * descarg� y en base a la informaci�n decidir descargar m�s informaci�n. Es 
 * por esto que si las colas internas que se usan tienen un l�mite m�ximo antes
 * de bloquear, y estas se encuentra llenas; y todos los closures que se encuentran
 * en procesamiento llaman a {@link #scheduleFetch(java.net.URI, Closure)} entonces se 
 * llegar� a un deadlock. Esta implementaci�n deberia tener en cuenta esto. 
 * </p>
 * 
 * <p>
 * Es importante tener en cuenta a la hora de elegir la cantidad de workers la
 * naturaleza de los dos pools: 
 *   <ul>
 *      <li><strong>fetch workers</strong>: Es puro I/O. La cantidad de 
 *         descargas en paralelo que se desean realizar. Un parametro �til para
 *         calcular este valor es dividir el ancho de banda disponible sobre la 
 *         velocidad de descarga aceptable por documento. Muchas descargas al mismo
 *         tiempo har� mas lenta el download y las tareas tardar�n mucho en llegar
 *         al procesamiento. Pocas descargar al mismo tiempo pueden desaprovechar
 *         el ancho de banda. Tambi�n en la decisi�n influye la naturaleza de 
 *         la cola. Si la cola de fetching es polite y no entrega trabajos
 *         que involucran a la misma direcci�n IP hasta un rato despues de que 
 *         la �ltima descarga haya terminado entonces se sabe que todas las descargas
 *         ser�n a diferentes servidores, y encontes es mas previsible el c�lculo
 *         de velocidad esperada por descarga.
 *      </li>
 *      <li><strong>processing workers</strong>: esto ya depende de cada aplicaci�n.
 *        Si las tareas son intensivas en el uso de CPU; se recomienda que 
 *        no se utilizen m�s threads que cores.
 *      </li>
 *   </ul>
 * </p>
 * <p>
 * Por otro lado, dado que ya todas las tareas vienen de colas internas, no 
 * es recomendable que los {@link ExecutorService} usados para procesar el trabajo
 * tengan internamente colas. 
 * </p>
 * @author Juan F. Codagnone
 * @since Feb 17, 2010
 */
public final class FetchQueueAsyncUriFetcher extends AbstractAsyncUriFetcher {
    // la clase es final ya que como se lanzan threads desde el constructor
    // si se extiende los threads se van a lanzar antes que se termine de crear
    // el objeto...no nos afectaria, pero asi somo correctos 
    
    private final JobQueue<Job> fetcherQueue;
    private final Thread inScheduler;
    private final JobQueue<Job> processingQueue;
    private final Thread outScheduler;
        
    private final Logger logger = LoggerFactory.getLogger(
            FetchQueueAsyncUriFetcher.class);
    private final JobScheduler fetcherScheduler;
    private final JobScheduler processingScheduler;
    
    private AsyncUriFetcherObserver observer =  
        new DebugLoggerAsyncUriFetcherObserver(
                LoggerFactory.getLogger(FetchQueueAsyncUriFetcher.class));
    
    /** */
    public FetchQueueAsyncUriFetcher(
            final JobScheduler fetcherScheduler,
            final JobScheduler processingScheduler) {
        
        Validate.notNull(fetcherScheduler);
        Validate.notNull(processingScheduler);
        
        
        // alertamos de posibles problemas debido a copy paste:
        // las queue y schedulers no pueden ser la misma instancia
        Validate.isTrue(fetcherScheduler.getQueue() 
                != processingScheduler.getQueue(),
                "Los schedulers de fetcher y procesamiento no pueden "
                + "compartir la misma queue");
        Validate.isTrue(fetcherScheduler != processingScheduler);
        
        this.fetcherQueue = fetcherScheduler.getQueue();
        this.processingQueue = processingScheduler.getQueue();
        
        this.fetcherScheduler  = fetcherScheduler;
        this.processingScheduler = processingScheduler;
        
        inScheduler = new Thread(fetcherScheduler, "JobScheduler-IN");
        outScheduler = new Thread(processingScheduler, "JobScheduler-OUT");
        
        inScheduler.start();
        outScheduler.start();
    }
    
    @Override
    public AsyncUriFetcher scheduleFetch(final FetchingTask methodCommand, final Closure<URIFetcherResponse> closure) {
        observer.newFetch(methodCommand.getURIAndCtx());
        incrementActiveJobs();
         
        try {
            fetcherQueue.add(new Job() {
                /** @see Job#getUriAndCtx() */
                public URIAndCtx getUriAndCtx() {
                    return methodCommand.getURIAndCtx();
                }
                
                public void run() {
                    final URIAndCtx uriAndCtx = methodCommand.getURIAndCtx();
                    observer.beginFetch(uriAndCtx);
                    final long t1 = System.currentTimeMillis();
                    final URIFetcherResponse rr = execute(methodCommand);
                    final URIFetcherResponse r = rr;    
                    final long t2 = System.currentTimeMillis();
                    
                    observer.finishFetch(uriAndCtx, t2 - t1);
                    try {
                        processingQueue.add(new Job() {
                            public void run() {
                                Long t1 = null;
                                try {
                                    observer.beginProcessing(uriAndCtx);
                                    t1 = System.currentTimeMillis();
                                    closure.execute(r);
                                } catch(final Throwable t) {
                                    if(logger.isErrorEnabled()) {
                                        logger.error("error while processing using "
                                                + closure.toString() 
                                                + " with URI: "
                                                + uriAndCtx, t);
                                    }
                                } finally {
                                    if(null == t1) {
                                        //Shouldn't enter here, its very rare.
                                        //This is because if it fails before the line
                                        //t1 = System.currentTimeMillis() at the try,
                                        //this will be null and then will fail calculating
                                        //the time elapsed. Plus, will show trash data
                                        t1 = System.currentTimeMillis();
                                    }
                                    long t2 = System.currentTimeMillis();
                                    observer.finishProcessing(uriAndCtx, t2 - t1);
                                    decrementActiveJobs();
                                }
                            }
                            /** @see Job#getUriAndCtx() */
                            public URIAndCtx getUriAndCtx() {
                                return uriAndCtx;
                            }
                        });
                    } catch (final Throwable e) {
                        closure.execute(new InmutableURIFetcherResponse(
                                uriAndCtx, e));
                    }
                    // TODO notificar a la fetcherQueue que ya se 
                    // fetcheo el elemento. 
                }

            });
        } catch(final Throwable e) {
            decrementActiveJobs();   
            logger.error("error adding job to the fetch queue "
                    + closure.toString() 
                    + " with URI: "
                    + methodCommand.getURIAndCtx().getURI(), e);
        }
        
        return this;
    }


    /** @see AsyncUriFetcher#shutdown() */
    public void shutdown() {
        // no aceptamos m�s trabajos.
        fetcherQueue.shutdown();
        waitForTermination(inScheduler);
        processingQueue.shutdown();
        waitForTermination(outScheduler);
        
    }

    /** @see AsyncUriFetcher#shutdownNow() */
    public void shutdownNow() {
        fetcherQueue.shutdown();
        processingQueue.shutdown();
        fetcherScheduler.shutdownNow();
        processingScheduler.shutdownNow();
        
        inScheduler.interrupt();
        outScheduler.interrupt();
    }
    
    /** Sets the observer. <code>null</code> sets a internal. s*/
    public void setObserver(final AsyncUriFetcherObserver o) {
        Validate.notNull(observer);
        this.observer = o;
    }
}
