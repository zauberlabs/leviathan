package com.zaubersoftware.leviathan.api.engine.impl;

import com.zaubersoftware.leviathan.api.engine.AfterHandleWith;
import com.zaubersoftware.leviathan.api.engine.ExceptionCatchDefinition;
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler;

public abstract class AbstractExceptionCatchDefinition<T> implements ExceptionCatchDefinition<T> {

    @Override
    public <E extends Throwable> AfterHandleWith<T> onExceptionHandleWith(Class<E> throwableClass,
            ExceptionHandler handler) {
        return on(throwableClass).handleWith(handler);
    }

}
