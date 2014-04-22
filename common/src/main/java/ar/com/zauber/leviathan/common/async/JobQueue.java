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

import java.util.concurrent.RejectedExecutionException;

/**
 * Cola de trabajos de Fetching. La implementación DEBE ser thread-safe. 
 * 
 * @author Juan F. Codagnone
 * @since Feb 16, 2010
 */
public interface JobQueue<T> {

    /** 
     * incorpora un nuevo trabajo a la cola de fetching.
     * 
     * @throws RejectedExecutionException Si no puede aceptar nuevas tareas 
     *   (porque se invocó a {@link #shutdown()})  
     * @throws InterruptedException si el thread fue interrumpido mientras se 
     *          esperaba que hubiera espacio para agregar la tarea.
     */
    void add(T fetchJob) throws RejectedExecutionException, InterruptedException;
    
    /** 
     * obtiene una tarea; si no tiene nada para entregar bloquea. Nunca retorna null.
     */
    T poll() throws InterruptedException;
    
    /** @return <code>true</code> si la cola está vacía */
    boolean isEmpty();
    
    /** shutdown */
    void shutdown();
    
    /**
     * return <code>true</code> si se esta apagando. 
     * No se aceptaran nuevas tareas
     */
    boolean isShutdown();
    
    /** @return cantidad actual de trabajos en espera */
    int size();
}
