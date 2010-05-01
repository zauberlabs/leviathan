/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;

/**
 * {@link Delayed} that recieves a fixed delay, and an element to contain.
 * 
 * @author Mariano Cortesi
 * @since May 1, 2010
 */
public class FixedDelay<T> implements Delayed {

    private final T element;
    private final long scheduledNanoTime;
    /**
     * Creates the FixedDelay.
     */
    public FixedDelay(final T element, final long delay, final TimeUnit unit) {
        Validate.notNull(unit);
        this.element = element;
        this.scheduledNanoTime = System.nanoTime() + unit.toNanos(delay);
    }

    /** @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit) */
    public long getDelay(TimeUnit unit) {
        return unit.convert(scheduledNanoTime - System.nanoTime(), 
                TimeUnit.NANOSECONDS);
    }

    /** @see java.lang.Comparable#compareTo(java.lang.Object) */
    public int compareTo(Delayed o) {
        return Long.valueOf(
                this.getDelay(TimeUnit.NANOSECONDS)
                        - o.getDelay(TimeUnit.NANOSECONDS)).intValue();
    }

    public T getElement() {
        return this.element;
    }

}
