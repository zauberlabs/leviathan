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
package com.zaubersoftware.leviathan.api.engine.impl;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.AsyncUriFetcher;

import com.zaubersoftware.leviathan.api.engine.Engine;
import com.zaubersoftware.leviathan.api.engine.LeviathanBuilder;

/**
 * The Default implementation of the {@link LeviathanBuilder} interface
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class DefaultLeviathanBuilder implements LeviathanBuilder {

    private AsyncUriFetcher fetcher;

    @Override
    public LeviathanBuilder withAsyncURIFetcher(final AsyncUriFetcher f) {
        Validate.notNull(f, "The fetcher cannot be null");
        this.fetcher = f;
        return this;
    }

    @Override
    public Engine build() {
        return new DefaultEngine(this.fetcher);
    }

}
