/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.io.Reader;

/**
 * HTTP response information
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface URIFetcherHttpResponse {
    
    /** status code */
    int getStatusCode();

    /** content */
    Reader getContent();
}
