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
package ar.com.zauber.leviathan.common;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

import com.zaubersoftware.leviathan.api.engine.CurrentThreadURIAndContextDictionary;
import com.zaubersoftware.leviathan.api.engine.Pipe;
/**
 * A {@link Closure} that feeds {@link URIFetcherResponse}s to {@link Pipe}
 *
 * @param <O> The {@link Pipe}'s output type
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class FetcherResponsePipeAdapterClosure<O> implements Closure<URIFetcherResponse> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Pipe<URIFetcherResponse, O> pipe;

    /**
     * Creates the PipeClosure.
     *
     * @param pipe
     */
    public FetcherResponsePipeAdapterClosure(final Pipe<URIFetcherResponse, O> pipe) {
        Validate.notNull(pipe);
        this.pipe = pipe;
    }


    @Override
    public void execute(final URIFetcherResponse response) {
        Validate.notNull(response);


        try {
            CurrentThreadURIAndContextDictionary.ctxHolder.set(response.getURIAndCtx());
            final O out = this.pipe.execute(response);
            if(out != null) {
                this.logger.warn("The last pipe returned a value != null. Null was expected: {}", out);
            }
        } finally {
            CurrentThreadURIAndContextDictionary.ctxHolder.set(null);
        }
    }

}
