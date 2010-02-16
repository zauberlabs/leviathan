/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zauber.com.ar/>
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
package ar.com.zauber.labs.kraken.fetcher.common;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

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

    /** Creates the InmutableURIFetcherResponse. */
    public InmutableURIFetcherHttpResponse(final String  content,
            final int statusCode) {
        Validate.notNull(content, "content is null");

        this.content = content;
        this.statusCode = statusCode;
    }

    /** @see URIFetcherResponse#getContent() */
    public final Reader getContent() {
        return new StringReader(content);
    }
    /** @see URIFetcherResponse#getStatusCode() */
    public final int getStatusCode() {
        return statusCode;
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
}
