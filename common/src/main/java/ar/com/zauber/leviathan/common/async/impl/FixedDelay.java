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
package ar.com.zauber.leviathan.common.async.impl;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;

/**
 * {@link Delayed} that recieves a fixed delay, and an element to contain.
 * 
 * @param <T> thing delayed
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

    @Override
    public final long getDelay(final TimeUnit unit) {
        return unit.convert(scheduledNanoTime - System.nanoTime(), 
                TimeUnit.NANOSECONDS);
    }

    @Override
    public final int compareTo(final Delayed o) {
        return Long.valueOf(getDelay(TimeUnit.NANOSECONDS)
                        - o.getDelay(TimeUnit.NANOSECONDS)).intValue();
    }

    public T getElement() {
        return this.element;
    }
}
