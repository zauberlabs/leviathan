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

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

import com.zaubersoftware.leviathan.api.engine.Pipe;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;

/**
 * An inmutable implementation of {@link ProcessingFlow} that is backed by a {@link Closure}&lt;{@link URIFetcherResponse}&gt;
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class InmutableProcessingFlow implements ProcessingFlow {

    private final Closure<URIFetcherResponse> rootClosure;

    /**
     * Creates the InmutableProcessingFlow.
     *
     * @param rootClosure
     */
    public InmutableProcessingFlow(final Closure<URIFetcherResponse> rootClosure) {
        Validate.notNull(rootClosure, "The root closure cannot be null");
        this.rootClosure = rootClosure;
    }

    @Override
    public ProcessingFlow concat(final ProcessingFlow flow) {
        Validate.notNull(flow, "The flow to be concatenated cannot be null");
        throw new NotImplementedException();
    }

    @Override
    public ProcessingFlow append(final Pipe<?, ?> pipe) {
        Validate.notNull(pipe, "The pipe to be appended cannot be null");
        throw new NotImplementedException();
    }

    /**
     * Returns the rootClosure.
     *
     * @return <code>Closure<URIFetcherResponse></code> with the rootClosure.
     */
    Closure<URIFetcherResponse> getRootClosure() {
        return this.rootClosure;
    }

}
