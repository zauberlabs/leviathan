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
package com.zaubersoftware.leviathan.api.engine;

/**
 * Mixin of {@link ErrorTolerant} and {@link ActionHandler}   
 * 
 * 
 * @param <T> The interface type that defines the available actions that can be configured once the error 
 * handler has been subscribed. 
 * @author Guido Marucci Blas
 * @author Mart�n Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011 
 */
public interface ErrorTolerantActionHandler<T> extends ErrorTolerant<ActionHandler<T>>, ActionHandler<T> {

}
