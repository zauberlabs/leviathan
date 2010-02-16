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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            tmp.put(r.getURIAndCtx().getURI(), r);
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
