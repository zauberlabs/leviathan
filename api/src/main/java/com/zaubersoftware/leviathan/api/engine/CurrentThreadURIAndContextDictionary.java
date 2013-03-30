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
import java.util.Map;

import ar.com.zauber.leviathan.api.URIAndContextDictionary;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * TODO: Description of the class, Comments in english by default
 *
 *
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public class CurrentThreadURIAndContextDictionary implements URIAndContextDictionary {
    public final static ThreadLocal<URIAndCtx> ctxHolder = new ThreadLocal<URIAndCtx>();

    @Override
    public final URI getURI() {
        return ctxHolder.get().getURI();
    }

    @Override
    public final Map<String, Object> getCtx() {
        return ctxHolder.get().getCtx();
    }

    @Override
    public final Object get(final String key) {
        return ctxHolder.get().getCtx().get(key);
    }

    public final boolean contains(final String key) {
        return ctxHolder.get().getCtx().containsKey(key);
    }

    @Override
    public final void put(final String key, final Object value) {
        ctxHolder.get().getCtx().put(key, value);
    }

}
