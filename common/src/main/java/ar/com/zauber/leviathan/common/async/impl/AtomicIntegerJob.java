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

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.async.Job;

/**
 * {@link Job} que incrementa un {@link AtomicInteger}. Es de utilidad
 * en tests para saber cuantas veces se ha ejecutado.
 * 
 * @author Juan F. Codagnone
 * @since Feb 19, 2010
 */
public class AtomicIntegerJob implements Job {
    private final AtomicInteger atomic;
    private final int delta;

    /** Creates the AtomicIntegerFetchJob. */
    public AtomicIntegerJob(final AtomicInteger atomic) {
        this(atomic, 1);
    }
    
    /** Creates the AtomicIntegerFetchJob. */
    public AtomicIntegerJob(final AtomicInteger atomic,
            final int delta) {
        Validate.notNull(atomic);
        
        this.atomic = atomic;
        this.delta = delta;
    }
    
    /** @see Runnable#run() */
    public final void run() {
        atomic.addAndGet(delta);
    }

    /** @see Job#getUriAndCtx() */
    public URIAndCtx getUriAndCtx() {
        throw new NotImplementedException();
    }
}
