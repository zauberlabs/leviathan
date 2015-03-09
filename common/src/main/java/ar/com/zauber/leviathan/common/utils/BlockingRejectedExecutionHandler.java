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
package ar.com.zauber.leviathan.common.utils;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * {@link RejectedExecutionException} agrega la tarea que no fue aceptadap
 * 
 * @author Juan F. Codagnone
 * @since Feb 25, 2010
 */
public class BlockingRejectedExecutionHandler 
  implements RejectedExecutionHandler {

    /** @see RejectedExecutionHandler#rejectedExecution(Runnable, 
     * ThreadPoolExecutor) */
    public final void rejectedExecution(final Runnable r, 
            final ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            throw new RejectedExecutionException("while waiting to put the element", 
                    e);
        }
    }
}
