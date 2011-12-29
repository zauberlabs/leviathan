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
package ar.com.zauber.leviathan.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.zauber.commons.async.AbstractAsyncTaskExecutor;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Clase base para los {@link AsyncUriFetcher}.
 * 
 * Las implementaciones deben (para que funcione debidamente el 
 * {@link #awaitIdleness()}) llamar al {@link #incrementActiveJobs()} cuando
 * hay nuevas tareas (y si falla el procesamiento de la misma (ej: no se puede
 * aceptar como trabajo; restituir el contador con {@link #decrementActiveJobs()}).
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public abstract class AbstractAsyncUriFetcher extends AbstractAsyncTaskExecutor
                                           implements AsyncUriFetcher {
    private Logger logger  = LoggerFactory.getLogger(getClass());
    
    
    /** execute a {@link FetchingTask} watching out for exceptions */
    protected final URIFetcherResponse execute(final FetchingTask task) {
        URIFetcherResponse rr = null;
        try {
            rr = task.execute();
        } catch(final Throwable t) {
            // esto jamas deberia pasar. fetch() nunca tira
            // excepciones
            logger.warn("fetching task " + task 
              + " no respeta el contrato!. está tirando excepciones");
            rr = new InmutableURIFetcherResponse(task.getURIAndCtx(), t);
        }
        return rr;
    }
}
