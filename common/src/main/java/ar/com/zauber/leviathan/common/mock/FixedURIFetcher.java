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
package ar.com.zauber.leviathan.common.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.AbstractURIFetcher;
import ar.com.zauber.leviathan.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.leviathan.common.InmutableURIFetcherResponse;

/**
 * {@link URIFetcher} used for tests. Get the content from the classpath.
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class FixedURIFetcher extends AbstractURIFetcher {
    private final Map<URI, String> map;
    private final Charset charset;

    /** Creates the FixedContentProvider. */
    public FixedURIFetcher(final Map<URI, String> map) {
        this(map, Charset.defaultCharset());
    }

    /** Creates the FixedContentProvider. */
    public FixedURIFetcher(final Map<URI, String> map,
            final Charset charset) {
        Validate.notNull(map);
        Validate.notNull(charset);

        this.map = map;
        this.charset = charset;
    }

    /**
     * @see URIFetcher#fetch(URI)
     * @deprecated Use {@link #get(URI)} instead. Will be deprecated on
     *             next version.
     */
    @Deprecated
    public final URIFetcherResponse fetch(final URI uri) {
        return get(uri);
    }
    
    /**
     * @see URIFetcher#fetch(URIAndCtx)
     * @deprecated Use {@link #get(URIAndCtx)} instead. Will be deprecated on
     *             next version.
     */
    @Deprecated
    public final URIFetcherResponse fetch(final URIAndCtx uri) {
        return get(uri);
    }

    /** @see URIFetcher#get(URIFetcherResponse.URIAndCtx) */
    @SuppressWarnings("unchecked")
    public final URIFetcherResponse get(final URIAndCtx uriAndCtx) {
        final URI uri = uriAndCtx.getURI();
        final String destURL = map.get(uri);
        final URIFetcherResponse ret;

        if(destURL == null) {
            ret = new  InmutableURIFetcherResponse(uriAndCtx,
                    new UnknownHostException(uri.getHost()));
        } else {
            final InputStream is = getClass().getClassLoader().getResourceAsStream(
                    destURL);

            if(is == null) {
                ret = new  InmutableURIFetcherResponse(uriAndCtx,
                        new UnknownHostException(uri.getHost()));
            } else {
                try {
                    ret = new InmutableURIFetcherResponse(uriAndCtx,
                            new InmutableURIFetcherHttpResponse(new String(
                                    IOUtils.toByteArray(is), charset
                                            .displayName()), 200,
                                    Collections.EMPTY_MAP));
                } catch (IOException e) {
                    throw new UnhandledException(e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        return ret;
    }
    
    /** @see URIFetcher#post(URIFetcherResponse.URIAndCtx, InputStream) */
    public final URIFetcherResponse post(final URIAndCtx uri,
            final InputStream body) {
        throw new NotImplementedException("Post to classpath not implemented");
    }
    
    /** @see URIFetcher#post(URIFetcherResponse.URIAndCtx, Map) */
    public final URIFetcherResponse post(final URIAndCtx uri,
            final Map<String, String> body) {
        throw new NotImplementedException("Post to classpath not implemented");
    }

}
