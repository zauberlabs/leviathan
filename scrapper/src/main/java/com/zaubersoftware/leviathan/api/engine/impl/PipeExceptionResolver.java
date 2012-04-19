/**
 * Copyright (c) 2009-2012 Zauber S.A. <http://zauberlabs.com/>
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
package com.zaubersoftware.leviathan.api.engine.impl;

import java.util.HashMap;
import java.util.Map;

import ar.com.zauber.commons.validate.Validate;

import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 *
 *
 * @author Martin Silva
 * @since Sep 23, 2011
 */
public final class PipeExceptionResolver {

    private final Map<Class<? extends Throwable>, ExceptionHandler> handlers = 
            new HashMap<Class<? extends Throwable>, ExceptionHandler>();
    private ExceptionHandler defaultHandler;

    /**
     * @param handler
     */
    public void setDefaultExceptionHandler(final ExceptionHandler handler) {
        Validate.notNull(handler, "The handler cannot be null");
        this.defaultHandler = handler;
    }

    public <E extends Throwable> void addExceptionHandler(final Class<E> throwableClass, 
                                                         final ExceptionHandler handler) {
        Validate.notNull(throwableClass, "The throwable class cannot be null");
        Validate.notNull(handler, "The exception handler cannot be null");
        this.handlers.put(throwableClass, handler);
    }

    public boolean hasDefaultHandler() {
        return this.defaultHandler != null;
    }

    /**
     * Returns the handlers.
     *
     * @return <code>Map<Class<? extends Throwable>,ExceptionHandler<?>></code> with the handlers.
     */
    public Map<Class<? extends Throwable>, ExceptionHandler> getHandlers() {
        return this.handlers;
    }

    /**
     * Returns the defaultHandler.
     *
     * @return <code>ExceptionHandler<?></code> with the defaultHandler.
     */
    public ExceptionHandler getDefaultHandler() {
        return this.defaultHandler;
    }
}
