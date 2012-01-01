/**
 * Copyright (c) 2009-2011 Zauber S.A. <http://www.zaubersoftware.com/>
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
package com.zaubersoftware.leviathan.api.engine;


/**
 * Defines all the available control structures actions.
 *
 * @author Guido Marucci Blas
 * @author Mart�n Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface ControlStructureHanlder<T> {

    /**
     * Iterates over an {@link Iterable}
     *
     * @param <R> The type of the elements that will be returned by the Iterator.
     * @param elementClass The class of the elements that will be returned by the Iterator. Must not be null.
     * @return An {@link AfterForEachHandler} that will export the method to indicate which method should
     * be called to obtain the iterator.
     * @throws IllegalArgumentException if the elementClass is null.
     */
    <R> AfterForEachHandler<R,T> forEach(Class<R> elementClass);
    
    <R> ErrorTolerantActionAndControlStructureHandler<T> forEachIn(Class<R> elementClass,
            String propertyName, ContextAwareClosure<R> object);

    ErrorTolerantActionAndControlStructureHandler<T> forEachIn(String propertyName,
            ContextAwareClosure<Object> object);

}
