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
package ar.com.zauber.leviathan.api;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * Obtención de una página de manera asincrónica
 * 
 * 
 * @author Marcelo Turrin
 * @since Jan 21, 2010
 */
public interface AsyncUriFetcher {

    
    /**
     * Busca la página deda por la uri indicada y al terminar llama al closure
     * con la respuesta
     * 
     * @param uri URIs to retrieve
     * @param closure closure donde se publica el resultado (el contenido de 
     *        la pagina, los errores; etc).
     */
    void get(URI uri, Closure<URIFetcherResponse> closure);
    
    /**
     * Busca la página deda por la uri indicada y al terminar llama al closure
     * con la respuesta
     * 
     * @param uriAndCtx URIs to retrieve
     * @param closure closure donde se publica el resultado (el contenido de 
     *        la pagina, los errores; etc).
     */
    void get(URIAndCtx uriAndCtx, Closure<URIFetcherResponse> closure);

    /**
     * Realiza un post a la uri indicada, enviando el contenido indicado en
     * <em>body</em> y al terminar llama al closure con la respuesta
     * 
     * @param uriAndCtx
     *            URIs to retrieve
     * @param body
     *            contenido a postear
     * @param closure
     *            closure donde se publica el resultado (el contenido de la
     *            pagina, los errores; etc).
     */
    void post(URIAndCtx uriAndCtx, InputStream body,
            Closure<URIFetcherResponse> closure);
    
    /**
     * Realiza un post a la uri indicada, enviando los pares clave/valor
     * indicados en <em>body</em> en forma de
     * "application/x-www-form-urlencoded", y al terminar llama al closure con
     * la respuesta
     * 
     * @param uriAndCtx
     *            URIs to retrieve
     * @param body
     *            contenido a postear (parametros simples y colecciones)
     * @param closure
     *            closure donde se publica el resultado (el contenido de la
     *            pagina, los errores; etc).
     */
    void post(URIAndCtx uriAndCtx, UrlEncodedPostBody body,
            Closure<URIFetcherResponse> closure);      
    
    
    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * 
     * Invocation has no additional effect if already shut down.
     * 
     * Bloqueante.
     */
    void shutdown();

    /**
     * Bloquea hasta que  todo el motor de fetching se encuentra idle. 
     * Esto significa que no hay trabajos en las colas internas, y que todos
     * se ejecutaron todos los closures.
     * 
     * Si el clousure de fetching vuelve a realizar fetching (recursivamente) 
     * {@link #awaitIdleness()} asegura que se termino de procesar todo.
     *  
     */
    void awaitIdleness() throws InterruptedException;
    
    /**
     * Blocks until all tasks have completed execution,
     * or the timeout occurs, or the current thread is interrupted, whichever
     * happens first.
     * 
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return <tt>true</tt> if this executor terminated and <tt>false</tt> if
     *         the timeout elapsed before termination
     */
    boolean awaitIdleness(long timeout, TimeUnit unit) throws InterruptedException;
    
    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks.
     *
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  For example, typical
     * implementations will cancel via {@link Thread#interrupt}, so any
     * task that fails to respond to interrupts may never terminate.
     */
    void shutdownNow();
}
