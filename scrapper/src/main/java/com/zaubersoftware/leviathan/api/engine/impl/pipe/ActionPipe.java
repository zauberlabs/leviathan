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
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import com.zaubersoftware.leviathan.api.engine.Action;
import com.zaubersoftware.leviathan.api.engine.Pipe;

import ar.com.zauber.commons.validate.Validate;

/**
 * TODO: Description of the class, Comments in english by default
 * 
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ActionPipe<I, O> implements Pipe<I, O> {

    private final Action<I, O> action;
    /**
     * Creates the ActionPipe.
     *
     */
    public ActionPipe(final Action<I, O> action) {
        Validate.notNull(action, "The action can not be null!");
        this.action = action;
    }

    @Override
    public O execute(final I input) {
        return this.action.execute(input);
    }

}
