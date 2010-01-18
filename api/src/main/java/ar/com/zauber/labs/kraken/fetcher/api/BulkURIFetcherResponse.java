/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.net.URI;
import java.util.Collection;
import java.util.Map;


/**
 * {@link BulkURIFetcher#fetch(java.util.Collection)} result
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface BulkURIFetcherResponse {

    /** the details of each retrieval */
    Map<URI, URIFetcherResponse> getDetails();
    
    /** @return the successful uris */
    Collection<URIFetcherResponse> getSuccessfulURIs();
    
    /** @return the successful uris */
    Collection<URIFetcherResponse> getFailedURIs();
}
