/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
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
 * Defines the method to indicate what method should be called on the target object to obtain the iterator.
 *
 * @param <T> The type of the elements that will be returned by the Iterator.
 * @param <E> The type of the class which has an interable property
 * @author Guido Marucci Blas
 * @author Mart�n Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface AfterForEachHandler<T, E> {

    /**
     * Indicates what property should be used to obtain the {@link Iterable} of elements of type T.
     *
     * @param propertyName The property name. Must not be null.
     * @return An {@link ActionAndControlStructureHandler}.
     * @throws IllegalArgumentException if the propertyName is null, if the target object does not contain
     * an the given property or if the property's type does not match {@link Iterable}<T>
     */
     ThenWithContextAwareClosure<T, E> in(final String propertyName);

     public interface ThenWithContextAwareClosure<T,E> {

         /**
          * Configures the last processing closure in the chain.
          * @param object the closure to execute over the T object.
          * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
          * @throws IllegalStateException if the object is null.
          */
         EndFor<E> then(ContextAwareClosure<T> object);

     }

     public interface EndFor<T> {

         ErrorTolerantActionAndControlStructureHandler<T> endFor();

     }

}
