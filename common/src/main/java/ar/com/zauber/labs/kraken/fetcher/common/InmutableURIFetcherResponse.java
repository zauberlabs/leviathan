/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * Inmutable {@link URIFetcherResponse} response
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class InmutableURIFetcherResponse implements URIFetcherResponse {
    private final URI uri;
    private final Throwable throwable;
    private final URIFetcherHttpResponse httpResponse;

    /** success constructor */
    public InmutableURIFetcherResponse(final URI uri,
            final URIFetcherHttpResponse httpResponse) {
        Validate.notNull(uri, "uri is null");
        Validate.notNull(httpResponse, "http response is null");

        this.uri = uri;
        this.throwable = null;
        this.httpResponse = httpResponse;
    }
    
    /** error constructor */
    public InmutableURIFetcherResponse(final URI uri,
            final Throwable throwable) {
        Validate.notNull(uri, "uri is null");
        Validate.notNull(throwable, "Throwable is null");
        
        this.uri = uri;
        this.throwable = throwable;
        this.httpResponse = null;
    }
    
    /** @see URIFetcherResponse#getURI() */
    public final URI getURI() {
        return uri;
    }
    
    /** @see URIFetcherResponse#getError() */
    public final Throwable getError() throws IllegalStateException {
        if(throwable == null) {
            throw new IllegalStateException(
                    "Only call getError() if isSucceeded returned false");
        }
        return throwable;
    }

    /** @see URIFetcherResponse#getHttpResponse() */
    public final URIFetcherHttpResponse getHttpResponse()
            throws IllegalStateException {
        if(httpResponse == null) {
            throw new IllegalStateException(
                    "Only call getError() if isSucceeded returned true");
        }
        
        return httpResponse;
        
    }

    /** @see URIFetcherResponse#isSucceeded() */
    public final boolean isSucceeded() {
        return throwable == null;
    }
    
    /** @see java.lang.Object#toString() */
    @Override
    public final String toString() {
        return new ToStringBuilder(this, ModelUtils.STYLE)
        .append("uri", uri)
        .append("error", throwable)
        .append("response", httpResponse)
        .toString();
    }
    
    /** @see Object#equals(Object) */
    @Override
    public final boolean equals(final Object obj) {
        boolean ret = false;
        
        if(obj == this) {
            ret = true;
        } else if(obj instanceof InmutableURIFetcherResponse) {
            final InmutableURIFetcherResponse r = 
                (InmutableURIFetcherResponse) obj;
            ret = uri.equals(r.uri);
            
            if(throwable == null && r.throwable == null) {
                // nada que hacer
            } else if(throwable != null && r.throwable != null) {
                // en general las excepciones no implementan equals
                // tampoco lo tiene getClass
                ret &= throwable.getClass().getName().equals(
                        r.throwable.getClass().getName());
            } else {
                ret = false;
            }
            
            if(httpResponse == null && r.httpResponse == null) {
                // nada que hacer
            } else if(httpResponse != null && r.httpResponse != null) {
                ret &= httpResponse.equals(r.httpResponse);
            } else {
                ret = false;
            }
        }
        
        return ret;
    }
    
    /** @see Object#hashCode() */
    @Override
    public final int hashCode() {
        int ret = 17;
        
        ret = ret * 39 + uri.hashCode();
        ret = ret * 39 + (throwable == null ? 0 : throwable.hashCode());
        ret = ret * 39 + (httpResponse == null ? 0 : httpResponse.hashCode());
        
        return ret;
    }
}
