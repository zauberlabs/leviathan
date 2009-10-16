/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * Inmutable implementation of {@link BulkURIFetcherResponse}.
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class InmutableBulkURIFetcherResponse implements BulkURIFetcherResponse {
    private final Map<URI, URIFetcherResponse> details;
    
    /** constructor */
    public InmutableBulkURIFetcherResponse(
            final List<URIFetcherResponse> responses) {
        Validate.noNullElements(responses);
        final Map<URI, URIFetcherResponse> tmp = 
            new HashMap<URI, URIFetcherResponse>();
        for(final URIFetcherResponse r : responses) {
            tmp.put(r.getURI(), r);
        }
        details = Collections.unmodifiableMap(tmp); 
    }
    /** @see BulkURIFetcherResponse#getDetails() */
    public final Map<URI, URIFetcherResponse> getDetails() {
        return details;
    }

    /** @see BulkURIFetcherResponse#getFailedURIs() */
    public final Collection<URIFetcherResponse> getFailedURIs() {
        final Collection<URIFetcherResponse> ret = 
            new LinkedList<URIFetcherResponse>();
        for(final URIFetcherResponse e : details.values()) {
            if(!e.isSucceeded()) {
                ret.add(e);
            }
        }
        return ret;
    }
    /** @see BulkURIFetcherResponse#getSuccessfulURIs() */
    public final Collection<URIFetcherResponse> getSuccessfulURIs() {
        final Collection<URIFetcherResponse> ret = 
            new LinkedList<URIFetcherResponse>();
        for(final URIFetcherResponse e : details.values()) {
            if(e.isSucceeded()) {
                ret.add(e);
            }
        }
        return ret;
    }
    
    /** @see Object#equals(Object) */
    @Override
    public final boolean equals(final Object obj) {
        boolean ret = false;
        
        if(obj == this) {
            ret = true;
        } else if(obj instanceof InmutableBulkURIFetcherResponse) {
            final InmutableBulkURIFetcherResponse r = 
                (InmutableBulkURIFetcherResponse) obj;
            ret = details.equals(r.details);
        }
        return ret;
    }
    
    /** @see Object#hashCode() */
    @Override
    public final int hashCode() {
        return details.hashCode();
    }
    
    /** @see Object#toString() */
    @Override
    public final String toString() {
        return details.toString();
    }
}
