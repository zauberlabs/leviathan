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

import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * Implementación de {@link HttpMethodCommand} para GET.
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 12, 2010
 */
public class GenericGetFetchingTask implements FetchingTask {

    private final URIFetcher fetcher;
    private final URIFetcherResponse.URIAndCtx uriAndCtx;

    /** Creates the GetHttpMethodCommand. */
    public GenericGetFetchingTask(final URIFetcher fetcher,
            final URIFetcherResponse.URIAndCtx uriAndCtx) {
        Validate.notNull(fetcher);
        Validate.notNull(uriAndCtx);
        this.fetcher = fetcher;
        this.uriAndCtx = uriAndCtx;
    }

    @Override
    public final URIFetcherResponse execute() {
        return fetcher.createGet(uriAndCtx).execute();
    }

    @Override
    public final URIAndCtx getURIAndCtx() {
        return uriAndCtx;
    }

}
