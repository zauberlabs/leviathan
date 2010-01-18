/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient;

import java.net.URI;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.web.proxy.ContentTransformer;
import ar.com.zauber.labs.kraken.fetcher.common.ResponseMetadata;

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



    /** Creates the InmutableResponseMetadata.*/
    public InmutableResponseMetadata(final URI uri, final String contentType,
            final String contentEncoding, final int status) {
        Validate.notNull(uri);
        this.contentType = contentType;
        this.uri = uri;
        this.statusCode = status;
        this.encoding = contentEncoding;
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

}
