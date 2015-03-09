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

import org.slf4j.Logger;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Apr 29, 2011
 */
public final class ThreadUtils {
    
    /** utility class */ 
    private ThreadUtils() {
        // void
    }

    public static boolean waitForTermination(final Thread thread) {
        return waitForTermination(thread, null);
    }
    
    /** espera que termine un thread */
    public static boolean waitForTermination(final Thread thread, final Logger logger) {
        boolean wait = true;
        
        // esperamos que el scheduler consuma todos los trabajos
        while(wait && thread.isAlive()) {
            try {
                thread.join();
                wait = false;
            } catch (InterruptedException e) {
                if(logger != null) {
                    logger.warn("interrupted while shutting down");
                }
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
    
}
