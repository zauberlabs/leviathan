/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.api;

import java.net.URI;

/**
 * {@link URIFetcher} response.
 * 
 * 
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface URIFetcherResponse {
    
    /** @return the URI associated with this response */
    URI getURI();
    
    /**
     * @return <code>true</code> if the request was able to connect the server
     * an execute the request, retriving HTTP information.
     * If it failed you can call {@link #getError()} to get more information.   
     */
    boolean isSucceeded();
    
    /** 
     * @see #isSucceeded()
     * @throws IllegalStateException if called when {@link #isSucceeded()} 
     * is <code>true</code>  
     */
    Throwable getError() throws IllegalStateException;
    
    /** 
     * @see #isSucceeded()
     * @throws IllegalStateException if called when {@link #isSucceeded()} 
     * is <code>false</code>  
     */
    URIFetcherHttpResponse getHttpResponse() throws IllegalStateException;
}
