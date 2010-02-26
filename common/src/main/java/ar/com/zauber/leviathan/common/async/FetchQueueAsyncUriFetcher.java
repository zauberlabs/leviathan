/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.AbstractAsyncUriFetcher;
import ar.com.zauber.leviathan.common.InmutableURIFetcherResponse;

/**
 * {@link AsyncUriFetcher} que utiliza dos colas (de tipo  {@link JobQueue}
 * internas para realizar el trabajo. 
 * 
 * El flujo de la  información es mas o menos así:
 * <ol>
 *   <li>el cliente llama a {@link #fetch(URIAndCtx, Closure)}</li>
 *   <li>Todo nuevo trabajo es encolado en una Fetch Queue. Dependiendo de la 
 *    naturaleza de la cola (limitada o infinita), si llegó a su capacidad total 
 *    podria bloquear hasta que haya espacio. 
 *   </li>
 *   <li>Una vez encolado el trabajo se retorna la ejecución al cliente</li>
 *   <li>Existe otro thread que es el fetch scheduler (de tipo {@link JobScheduler})
 *       que se encarga de consumir las tareas de fetching de la cola. Quita
 *       de la cola la tarea y la pone ejecutar en un {@link ExecutorService}</li>
 *   <li>Cuando el fetching termina (ya sea OK, por error o timeout), el trabajo
 *       se encola una cola de procesamiento (también de tipo {@link JobQueue}).</li>
 *   <li>Existe otro thread (el processing scheduler) que consume todo lo que 
 *       se encoló para procesar y lo pone a procesar en un pool de workers 
 *       (tambíen un executor service). Poner a ejecutar significa ejecutar el
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
 * descargó y en base a la información decidir descargar más información. Es 
 * por esto que si las colas internas que se usan tienen un límite máximo antes
 * de bloquear, y estas se encuentra llenas; y todos los closures que se encuentran
 * en procesamiento llaman a {@link #fetch(java.net.URI, Closure)} entonces se 
 * llegará a un deadlock. Esta implementación deberia tener en cuenta esto. 
 * </p>
 * 
 * <p>
 * Es importante tener en cuenta a la hora de elegir la cantidad de workers la
 * naturaleza de los dos pools: 
 *   <ul>
 *      <li><strong>fetch workers</strong>: Es puro I/O. La cantidad de 
 *         descargas en paralelo que se desean realizar. Un parametro útil para
 *         calcular este valor es dividir el ancho de banda disponible sobre la 
 *         velocidad de descarga aceptable por documento. Muchas descargas al mismo
 *         tiempo hará mas lenta el download y las tareas tardarán mucho en llegar
 *         al procesamiento. Pocas descargar al mismo tiempo pueden desaprovechar
 *         el ancho de banda. También en la decisión influye la naturaleza de 
 *         la cola. Si la cola de fetching es polite y no entrega trabajos
 *         que involucran a la misma dirección IP hasta un rato despues de que 
 *         la última descarga haya terminado entonces se sabe que todas las descargas
 *         serán a diferentes servidores, y encontes es mas previsible el cálculo
 *         de velocidad esperada por descarga.
 *      </li>
 *      <li><strong>processing workers</strong>: esto ya depende de cada aplicación.
 *        Si las tareas son intensivas en el uso de CPU; se recomienda que 
 *        no se utilizen más threads que cores.
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
    
    private final URIFetcher fetcher;
    private final JobQueue fetcherQueue;
    private final Thread inScheduler;
    private final JobQueue processingQueue;
    private final Thread outScheduler;
        
    private final Logger logger = Logger.getLogger(FetchQueueAsyncUriFetcher.class);
    private final JobScheduler fetcherScheduler;
    private final JobScheduler processingScheduler;
    
    /** */
    public FetchQueueAsyncUriFetcher(
            final URIFetcher fetcher,
            final JobScheduler fetcherScheduler,
            final JobScheduler processingScheduler) {
        
        Validate.notNull(fetcher);
        Validate.notNull(fetcherScheduler);
        Validate.notNull(processingScheduler);
        
        
        // alertamos de posibles problemas debido a copy paste:
        // las queue y schedulers no pueden ser la misma instancia
        Validate.isTrue(fetcherScheduler.getQueue() 
                != processingScheduler.getQueue(),
                "Los schedulers de fetcher y procesamiento no pueden "
                + "compartir la misma queue");
        Validate.isTrue(fetcherScheduler != processingScheduler);
        
        this.fetcher = fetcher;
        this.fetcherQueue = fetcherScheduler.getQueue();
        this.processingQueue = processingScheduler.getQueue();
        
        this.fetcherScheduler  = fetcherScheduler;
        this.processingScheduler = processingScheduler;
        
        inScheduler = new Thread(fetcherScheduler, "JobScheduler-IN");
        outScheduler = new Thread(processingScheduler, "JobScheduler-OUT");
        
        inScheduler.start();
        outScheduler.start();
    }

    /** @see AsyncUriFetcher#fetch(URIAndCtx, Closure) */
    public void fetch(final URIAndCtx uriAndCtx, 
            final Closure<URIFetcherResponse> closure) {
        incrementActiveJobs();
        
        // esto puede bloquear, asi que es por eso que se hizo todo el lock en
        // en incrementActiveJobs.
        final boolean isDebug = logger.isDebugEnabled(); 
        try {
            fetcherQueue.add(new Job() {
                public void run() {
                    if(isDebug) {
                        logger.debug("Fetching " + uriAndCtx.getURI());
                    }
                    final long t1 = System.currentTimeMillis();
                    final URIFetcherResponse r = fetcher.fetch(uriAndCtx);
                    final long t2 = System.currentTimeMillis();
                    
                    if(isDebug) {
                        logger.debug("Elapsed " + (t2 - t1) + "ms fetching " 
                                + uriAndCtx.getURI());
                    }
                    
                    try {
                        processingQueue.add(new Job() {
                            public void run() {
                                try {
                                    if(isDebug) {
                                        logger.debug("Processing " 
                                                + uriAndCtx.getURI());
                                    }
                                    final long t1 = System.currentTimeMillis();
                                    closure.execute(r);
                                    final long t2 = System.currentTimeMillis();
                                    if(isDebug) {
                                        logger.debug("Elapsed " + (t2 - t1) 
                                                + "ms on " 
                                                + uriAndCtx.getURI());
                                    }
                                } catch(final Throwable t) {
                                    if(logger.isEnabledFor(Level.ERROR)) {
                                        logger.error("error while processing using "
                                                + closure.toString() 
                                                + " with URI: "
                                                + uriAndCtx.getURI(), t);
                                    }
                                } finally {
                                    decrementActiveJobs();
                                }
                            }
                        });
                    } catch (final Throwable e) {
                        closure.execute(new InmutableURIFetcherResponse(
                                uriAndCtx, e));
                    }
                    // TODO: notificar a la fetcherQueue que ya se 
                    // fetcheo el elemento. 
                }
            });
        } catch(final Throwable e) {
            decrementActiveJobs();   
        }
    }


    /** @see AsyncUriFetcher#shutdown() */
    public void shutdown() {
        // no aceptamos más trabajos.
        fetcherQueue.shutdown();
        waitForTermination(inScheduler);
        processingQueue.shutdown();
        waitForTermination(outScheduler);
        
    }

    /** espera que termine un thread */
    private boolean waitForTermination(final Thread thread) {
        boolean wait = true;
        
        // esperamos que el scheduler consuma todos los trabajos
        while(wait && thread.isAlive()) {
            try {
                thread.join();
                wait = false;
            } catch (InterruptedException e) {
                logger.log(Level.WARN, "interrupted while shutting down");
                try {
                    // si justo tenemos un bug, evitamos comermos  todo el cpu
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    // nada que  hacer
                }
            }
        }
        return wait;
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
}
