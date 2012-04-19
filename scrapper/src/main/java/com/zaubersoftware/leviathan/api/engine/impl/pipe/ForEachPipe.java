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
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import ar.com.zauber.commons.validate.Validate;

import com.zaubersoftware.leviathan.api.engine.Pipe;


/**
 * TODO: Description of the class, Comments in english by default
 *
 * @param <T> The type of object from which the {@link Iterable} will be extracted.
 * @param <V> The type of the iterated object
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ForEachPipe<T, V> implements Pipe<T, T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Pipe<V, ?> pipe;
    private final String propertyName;

    /**
     * Creates the ForEachPipe.
     *
     * @param pipe
     */
    public ForEachPipe(final String propertyName, final Pipe<V, ?> pipe) {
        Validate.notNull(pipe, "The pipe cannot be null");
        Validate.notBlank(propertyName, "The property name cannot be blank");
        this.pipe = pipe;
        this.propertyName = propertyName;
    }

    @Override
    public T execute(final T input) {
        for (final V element : getIterable(input, this.propertyName)) {
            this.pipe.execute(element);
        }
        return input;
    }

    /**
     * Invokes the method using reflection to obtain the {@link Iterable}
     *
     * @param object
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    private Iterable<V> getIterable(final T object, final String property) {
        final String methodName = "get" + StringUtils.capitalize(property);
        Method method;
        try {
            method = object.getClass().getDeclaredMethod(methodName);
            return (Iterable<V>) method.invoke(object);
        } catch (final SecurityException e) {
            throw new IllegalStateException(e);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch (final IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (final InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
