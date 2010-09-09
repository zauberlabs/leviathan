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

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * Implementación de {@link HttpMethodCommand} para POST.
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 12, 2010
 */
public class PostHttpMethodCommand implements HttpMethodCommand {

    private final URIFetcher fetcher;
    private final URIFetcherResponse.URIAndCtx uri;
    private final InputStream body;
    private final UrlEncodedPostBody encodedBody;

    /** Creates the PostHttpMethodCommand. */
    public PostHttpMethodCommand(final URIFetcher fetcher, final URIAndCtx uri,
            final InputStream body) {
        Validate.notNull(fetcher);
        Validate.notNull(uri);
        Validate.notNull(body);
        
        this.fetcher = fetcher;
        this.uri = uri;
        this.body = body;
        this.encodedBody = null;
    }
    
    /** Creates the PostHttpMethodCommand. */
    public PostHttpMethodCommand(final URIFetcher fetcher, final URIAndCtx uri,
            final Map<String, String> formBody) {
        Validate.notNull(fetcher);
        Validate.notNull(uri);
        Validate.notNull(formBody);
        
        this.fetcher = fetcher;
        this.uri = uri;
        this.body = null;
        this.encodedBody = createEncodedBody(formBody);
    }    

    
    /** Creates an UrlEncodedPostBody using a Map<String, String>
     * @param formBody
     * @return
     */
    private UrlEncodedPostBody createEncodedBody
                        (final Map<String, String> formBody) {
        final UrlEncodedPostBody result = new UrlEncodedPostBody();
        for (Entry<String, String> entry : formBody.entrySet()) {
            result.addSimpleParameter(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Creates the PostHttpMethodCommand.
     *
     * @param fetcher
     * @param uri
     * @param body
     */
    
    public PostHttpMethodCommand(final URIFetcher fetcher, 
            final URIAndCtx uri,
            final UrlEncodedPostBody body) {
        
        Validate.notNull(fetcher);
        Validate.notNull(uri);
        Validate.notNull(body);
        
        this.fetcher = fetcher;
        this.uri = uri;
        this.body = null;
        this.encodedBody = body;
    }

    /** @see HttpMethodCommand#execute() */
    public final URIFetcherResponse execute() {
        URIFetcherResponse ret;
        
        if (encodedBody != null) {
            ret = fetcher.post(uri, encodedBody);
        } else if (body != null) {
            ret = fetcher.post(uri, body);
        } else {
            ret = new InmutableURIFetcherResponse(uri, 
                    new IllegalStateException("Nothing to post!"));
        }
        
        return ret;
    }
    
    /** @see HttpMethodCommand#getURI() */
    public final URI getURI() {
        return uri.getURI();
    }

}
