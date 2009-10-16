/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;

import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherResponse;

/**
 * {@link URIFetcher} that uses Apache's HttpClient components
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class HTTPClientURIFetcher implements URIFetcher {
    private final HttpClient httpClient;
    
    /** constructor */
    public HTTPClientURIFetcher(final HttpClient httpClient) {
        Validate.notNull(httpClient);
        
        this.httpClient = httpClient;
    }
    /** @see URIFetcher#fetch(URI) */
    public final URIFetcherResponse fetch(final URI uri) {
        Validate.notNull(uri, "uri is null");
        
        HttpResponse response = null; 
        try {
            response = httpClient.execute(new HttpGet(uri));
            final HttpEntity entity = response.getEntity();
            
            return new InmutableURIFetcherResponse(
                uri, new InmutableURIFetcherHttpResponse(
                    new InputStreamReader(
                        new ByteArrayInputStream(EntityUtils.toByteArray(entity)),
                        getCharset(entity)
                    ), 
                    response.getStatusLine().getStatusCode()));
        } catch (final Throwable e) {
            return new InmutableURIFetcherResponse(uri, e);
        } finally {
            if(response != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (final IOException e) {
                    return new InmutableURIFetcherResponse(uri, e);
                }
            }
        }
    }
    
    
    /** the charset */
    private Charset getCharset(final HttpEntity entity) {
        Charset ret = Charset.defaultCharset();
        
        if(entity.getContentType() != null) {
            String contentType = entity.getContentType().getValue();
            if(contentType != null) {
                for(String s : contentType.split(";")) {
                    s = s.trim();
                    if(s.startsWith("charset=")) {
                        ret = Charset.forName(s.substring(8));
                    }
                }
            }
        }
        return ret;
    }

    /**
     * When HttpClient instance is no longer needed, 
     * shut down the connection manager to ensure
     */
    public final void shutdown() {
        httpClient.getConnectionManager().shutdown(); 
    }
}
