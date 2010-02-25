/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
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
