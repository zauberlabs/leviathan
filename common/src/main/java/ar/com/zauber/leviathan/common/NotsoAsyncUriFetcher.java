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

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * {@link AsyncUriFetcher} que no es muy inteleginte. segurante bloquente.
 * 
 * @author Juan F. Codagnone
 * @since Jan 21, 2010
 */
public class NotsoAsyncUriFetcher extends AbstractAsyncUriFetcher {

    @Override
    public final AsyncUriFetcher scheduleFetch(final FetchingTask task, 
                                               final Closure<URIFetcherResponse> closure) {
        closure.execute(execute(task));
        return this;
    }
    

    /** @see AsyncUriFetcher#shutdown() */
    public final void shutdown() {
        // nothing to do
    }
    
    /** @see AsyncUriFetcher#shutdownNow() */
    public final void shutdownNow() {
        // nothing to do
    }
}
