/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.net.URI;
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
}
