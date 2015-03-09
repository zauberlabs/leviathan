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
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import com.zaubersoftware.leviathan.api.engine.Pipe;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.validate.Validate;

/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ClosureAdapterPipe<T> implements Pipe<T, Void> {

    private final Closure<T> closure;

    /**
     * Creates the ClosureAdapterPipe.
     *
     * @param closure
     */
    public ClosureAdapterPipe(final Closure<T> closure) {
        Validate.notNull(closure, "The closure cannot be null");
        this.closure = closure;
    }

    @Override
    public Void execute(final T input) {
        closure.execute(input);
        return null;
    }

}
