/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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

import java.net.URI;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Implementación de {@link HttpMethodCommand} para GET.
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 12, 2010
 */
public class GetHttpMethodCommand implements HttpMethodCommand {

    private final URIFetcher fetcher;
    private final URIFetcherResponse.URIAndCtx uri;

    /** Creates the GetHttpMethodCommand. */
    public GetHttpMethodCommand(final URIFetcher fetcher,
            final URIFetcherResponse.URIAndCtx uri) {
        Validate.notNull(fetcher);
        Validate.notNull(uri);
        this.fetcher = fetcher;
        this.uri = uri;
    }

    /** @see ar.com.zauber.leviathan.common.HttpMethodCommand#execute() */
    public final URIFetcherResponse execute() {
        return fetcher.get(uri);
    }

    /** @see ar.com.zauber.leviathan.common.HttpMethodCommand#getURI() */
    public final URI getURI() {
        return uri.getURI();
    }

}
