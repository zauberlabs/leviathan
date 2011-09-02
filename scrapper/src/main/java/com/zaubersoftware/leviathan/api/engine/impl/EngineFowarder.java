/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.zaubersoftware.leviathan.api.engine.AfterFetchingHandler;
import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlowBinding;

/**
 * An {@link Engine} fowarder
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public abstract class EngineFowarder implements Engine {

    private final Engine target;

    /**
     * Creates the EngineFowarder.
     *
     * @param target
     */
    public EngineFowarder(final Engine target) {
        this.target = target;
    }

    @Override
    public Engine onAnyExceptionDo(final ExceptionHandler<Throwable> handler) {
        return this.target.onAnyExceptionDo(handler);
    }

    @Override
    public <E extends Throwable> Engine onError(final Class<E> throwableClass, final ExceptionHandler<E> handler) {
        return this.target.onError(throwableClass, handler);
    }

    @Override
    public AfterFetchingHandler forUri(final String uriTemplate) {
        return this.target.forUri(uriTemplate);
    }

    @Override
    public ProcessingFlowBinding bindURI(final URI uri) {
        return this.target.bindURI(uri);
    }

    @Override
    public ProcessingFlowBinding bindURI(final String uriTemplate) {
        return this.bindURI(uriTemplate);
    }

    @Override
    public AfterFetchingHandler forUri(final URI uri) {
        return this.forUri(uri);
    }

    @Override
    public AfterFetchingHandler afterFetch() {
        return this.afterFetch();
    }

    @Override
    public Engine doGet(final URI uri) {
        return this.target.doGet(uri);
    }

    @Override
    public Engine doGet(final URI uri, final ProcessingFlow flow) {
        return this.doGet(uri, flow);
    }

    @Override
    public Engine awaitIdleness() throws InterruptedException {
        return this.awaitIdleness();
    }

    @Override
    public boolean awaitIdleness(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.target.awaitIdleness(timeout, unit);
    }


}
