/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
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

    private final Map<Class<? extends Throwable>, ExceptionHandler> handlers = new HashMap<Class<? extends Throwable>, ExceptionHandler>();
    private ExceptionHandler defaultHandler;

    /**
     * @param handler
     */
    public void setDefaultExceptionHandler(final ExceptionHandler handler) {
        Validate.notNull(handler, "The handler cannot be null");
        this.defaultHandler = handler;
    }

    public <E extends Throwable> void addExceptionHandler(final Class<E> throwableClass, final ExceptionHandler handler) {
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
