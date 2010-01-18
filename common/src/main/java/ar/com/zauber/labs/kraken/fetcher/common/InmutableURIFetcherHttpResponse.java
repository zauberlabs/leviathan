/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
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
