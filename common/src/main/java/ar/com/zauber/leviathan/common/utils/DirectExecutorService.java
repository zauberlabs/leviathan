/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService que corre las tareas en el mismo thread que lo invocó.
 * Util para tests.
 * 
 * @author Juan F. Codagnone
 * @since Feb 16, 2010
 */
public class DirectExecutorService extends AbstractExecutorService {
    private boolean shutdown;

    /** @see ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit) */
    public final boolean awaitTermination(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return true;
    }

    /** @see ExecutorService#isShutdown() */
    public final boolean isShutdown() {
        return shutdown;
    }

    /** @see ExecutorService#isTerminated() */
    public final boolean isTerminated() {
        return shutdown;
    }

    /** @see ExecutorService#shutdown() */
    public final void shutdown() {
        shutdown = true;
    }

    /** @see ExecutorService#shutdownNow() */
    public final List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    /** @see Executor#execute(java.lang.Runnable) */
    public final void execute(final Runnable command) {
        command.run();
    }

}
