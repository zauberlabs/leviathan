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
package ar.com.zauber.leviathan.common;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * A closure that thorws an exception in case the response has an error. 
 * 
 * @author Guido Marucci Blas
 * @since Feb 10, 2011
 */
public final class FatalClosure implements Closure<URIFetcherResponse> {

    @Override
    public void execute(final URIFetcherResponse response) {
        Validate.notNull(response);
        
        if (!response.isSucceeded()) {
            throw new UnhandledException(response.getError());
        }
    }
}
