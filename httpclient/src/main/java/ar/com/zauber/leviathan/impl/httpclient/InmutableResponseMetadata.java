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
package ar.com.zauber.leviathan.impl.httpclient;

import java.net.URI;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.web.proxy.ContentTransformer;
import ar.com.zauber.leviathan.common.ResponseMetadata;

/**
 * Un {@link ResponseMetadata} inmutable
 *
 * @author Mariano Semelman
 * @since Dec 15, 2009
 */
public class InmutableResponseMetadata implements ResponseMetadata {

    private final String contentType;
    private final int statusCode;
    private final String encoding;
    private final URI uri;
    private final String charset;



    /** Creates the InmutableResponseMetadata. */
    public InmutableResponseMetadata(final URI uri, final String contentType,
            final String contentEncoding, final int status, final String charset) {
        Validate.notNull(uri);
        this.contentType = contentType;
        this.uri = uri;
        this.statusCode = status;
        this.encoding = contentEncoding;
        this.charset = charset;
    }

    /** @see ContentTransformer.ContentMetadata#getContentType() */
    public final String getContentType() {
        return this.contentType;
    }

    /** @see ContentTransformer.ContentMetadata#getStatusCode() */
    public final int getStatusCode() {
        return this.statusCode;
    }

    /** @see ContentTransformer.ContentMetadata#getUri() */
    public final String getUri() {
        return this.uri.toString();
    }

    /** @see ResponseMetadata#getEncoding() */
    public final String getEncoding() {
        return this.encoding;
    }

    /** @see ResponseMetadata#getURI() */
    public final URI getURI() {
        return this.uri;
    }

    /** @see ResponseMetadata#getCharset() */
    public final String getCharset() {
        return charset;
    }

}
