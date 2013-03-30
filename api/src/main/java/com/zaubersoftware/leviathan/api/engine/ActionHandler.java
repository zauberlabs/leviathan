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
package com.zaubersoftware.leviathan.api.engine;

import java.net.URI;


/**
 * Defines all the actions that can be configured for the fetched resource in order to process it.
 *
 * @param <T> The type of the object for which the action will be performed.
 * @author Guido Marucci Blas
 * @author Mart�n Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface ActionHandler<T>  extends AfterThen {

    /**
     * Configures the next processing {@link Action} in the chain.
     * @param object the {@link Action} to execute over the T object.
     * @param <R> the result type.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the object is null.
     */
    <R> ActionAndControlStructureHandler<R> then(Action<T, R> object);


    /**
     * Configures the next processing {@link Action} in the chain and then fetches a new resource.
     * @param object the {@link Action} to execute over the T object.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the object is null.
     */
    AfterFetchingHandler then(ActionAndThenFetch<T> object);

    /**
     * Configures the last processing closure in the chain.
     * @param object the closure to execute over the T object.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the object is null.
     */
    ErrorTolerantAfterThen then(ContextAwareClosure<T> object);


    /**
     * Configures the next resource to be fetched.
     * @param uriTemplate the location of the resource.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the uriTemplate is null.
     */
    AfterFetchingHandler thenFetch(String uriTemplate);

    /**
     * Configures the next resource to be fetched.
     * @param uri the location of the resource.
     * @return {@link AfterFetchingHandler} all the available actions to performed over a fetched resource.
     * @throws IllegalStateException if the uri is null.
     */
    AfterFetchingHandler thenFetch(URI uri);

    /**
     * Null action.
     * @return An {@link Engine}
     */
    Engine thenDoNothing();

}
