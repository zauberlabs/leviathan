/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async;

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
