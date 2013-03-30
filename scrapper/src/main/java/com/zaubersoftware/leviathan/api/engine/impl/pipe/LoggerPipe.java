/**
 * Copyright (c) 2009-2013 Zauber S.A. <http://zauberlabs.com/>
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

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaubersoftware.leviathan.api.engine.Pipe;

/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class LoggerPipe<I, O> implements Pipe<I, O> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @SuppressWarnings("rawtypes")
    private final Pipe<I, O> pipe;

    /**
     * Creates the DeadEndPipe.
     *
     * @param pipe
     */
    public <T> LoggerPipe(final Pipe<I, O> pipe) {
        Validate.notNull(pipe, "The pipe cannot be null");
        this.pipe = pipe;
    }

    @Override
    public O execute(final I input) {
        final O result = this.pipe.execute(input);
        logger.debug("Pipe's flow -> {}", (result == null) ? null : result.toString());
        return result;
    }

}
