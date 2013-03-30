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


/**
 * TODO: Description of the class, Comments in english by default
 *
 *
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public abstract class ExceptionHandler extends CurrentThreadURIAndContextDictionary {

    public abstract void handle(Throwable trowable);

    @SuppressWarnings("unchecked")
    public final <C> Class<C> getInputClass() {
        return (contains(ContextKeys.INPUT_CLASS_KEY)) ? (Class<C>) get(ContextKeys.INPUT_CLASS_KEY) : null;
    }

    @SuppressWarnings("unchecked")
    public final <C> C getInput() {
        return (contains(ContextKeys.INPUT_OBJECT_KEY))
        ? (C) getInputClass().cast(get(ContextKeys.INPUT_OBJECT_KEY)) : null;
    }
}
