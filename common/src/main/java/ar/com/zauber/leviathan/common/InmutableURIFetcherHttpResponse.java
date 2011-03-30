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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;


import ar.com.zauber.leviathan.api.URIFetcherHttpResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Inmutable {@link URIFetcherHttpResponse}.
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class InmutableURIFetcherHttpResponse implements URIFetcherHttpResponse,
                                                        Serializable {
    private static final long serialVersionUID = 2726687465186262473L;

    private final String content;
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final byte[] rawContent;

    /** Creates the InmutableURIFetcherResponse. */
    public InmutableURIFetcherHttpResponse(final String  content,
            final int statusCode, final Map<String, List<String>> headers,
            final byte[] rawContent) {
        Validate.notNull(content, "content is null");
        Validate.notNull(headers);
        
        this.content = content;
        this.statusCode = statusCode;
        this.headers = new HashMap<String, List<String>>();
        this.rawContent = rawContent;
        
        //All the headers will be treated in lower case. Transparent for the user
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            this.headers.put(entry.getKey().toLowerCase(), entry.getValue());
        }
    }
    /**Creates the InmutableURIFetcherHttpResponse.*/
    public InmutableURIFetcherHttpResponse(final String  content,
            final int statusCode, final Map<String, List<String>> headers) {
        this(content, statusCode, headers, null);
    }

    /** @see URIFetcherResponse#getContent() */
    public final Reader getContent() {
        return new StringReader(content);
    }
    
    /** @see URIFetcherResponse#getStatusCode() */
    public final int getStatusCode() {
        return statusCode;
    }
    
    /** @see URIFetcherHttpResponse#getHeader(String) */
    public final String getHeader(final String name) {
        if (headers != null && name != null) {
            List<String> auxHeaders = headers.get(name.toLowerCase());
            if (auxHeaders != null && auxHeaders.size() > 0) {
                return auxHeaders.get(0);
            }
        }
        
        return null;
    }
    
    /** @see URIFetcherHttpResponse#getHeaders(java.lang.String) */
    public final List<String> getHeaders(final String name) {
        if (headers != null && name != null) {
            List<String> auxHeaders = headers.get(name.toLowerCase());
            if (auxHeaders != null && auxHeaders.size() > 0) {
                return auxHeaders;
            }
        }
        
        return null;
    }

    /** @see Object#toString() */
    @Override
    public final String toString() {
        return new ToStringBuilder(this, ModelUtils.STYLE)
        .append("status", statusCode)
        .toString();
    }

    /** @see Object#equals(Object) */
    @Override
    public final boolean equals(final Object obj) {
        boolean ret = false;

        if(obj == this) {
            ret = true;
        } else if(obj instanceof InmutableURIFetcherHttpResponse) {
            final InmutableURIFetcherHttpResponse r =
                (InmutableURIFetcherHttpResponse) obj;

            ret = r.statusCode == statusCode;
        }

        return ret;
    }

    /** @see Object#hashCode() */
    @Override
    public final int hashCode() {
        int ret = 17;

        ret = ret * 39 + statusCode;

        return ret;
    }

    /** @see URIFetcherHttpResponse#getRawContent() */
    @Override
    public final InputStream getRawContent() {
        if(null == rawContent) {
            throw new IllegalStateException("Raw content not setted");
        }
        return new ByteArrayInputStream(rawContent);
    }
}
