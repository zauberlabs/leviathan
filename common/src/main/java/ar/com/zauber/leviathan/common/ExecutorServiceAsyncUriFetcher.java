/**
 * Copyright (c) 2009-2013 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * {@link AsyncUriFetcher} que utiliza un {@link ExecutorService} para no 
 * bloquearse. Segun el ExecutorService que se use (ver su queue) es lo que suceda
 * cuando todos los workers estan ocupados (se encolan; se descartan; etc) 
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public class ExecutorServiceAsyncUriFetcher extends AbstractAsyncUriFetcher {
    private final ExecutorService executorService;
    
    private final Logger logger = LoggerFactory.getLogger(
            ExecutorServiceAsyncUriFetcher.class);
    
    /** Creates the ExecutorServiceAsyncUriFetcher. */
    public ExecutorServiceAsyncUriFetcher(final ExecutorService executorService) {
        Validate.notNull(executorService);
        
        this.executorService = executorService;
    }

    
    @Override
    public final AsyncUriFetcher scheduleFetch(final FetchingTask methodCommand, 
                                               final Closure<URIFetcherResponse> closure) {
        Validate.notNull(methodCommand);
        Validate.notNull(closure);
        
        incrementActiveJobs();
        try {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        closure.execute(methodCommand.execute());
                    } catch(final Throwable t) {
                        if(logger.isErrorEnabled()) {
                            logger.error("error while processing using "
                                    + closure.toString() 
                                    + " with URI: "
                                    + methodCommand.getURIAndCtx(), t);
                        }
                    } finally {
                        decrementActiveJobs();
                    }
                }
            });
        } catch(final Throwable e) {
            decrementActiveJobs();   
        }
        return this;
    }
    
    
    /** @see AsyncUriFetcher#shutdown() */
    public final void shutdown() {
        executorService.shutdown();
    }

    /** @see AsyncUriFetcher#awaitTermination(long, TimeUnit) */
    public final boolean awaitTermination(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return false;
    }

    /** @see AsyncUriFetcher#shutdownNow() */
    public final void shutdownNow() {
        throw new NotImplementedException("TODO");
    }

}
