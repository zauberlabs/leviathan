/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.HTTPClientURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.common.ExecutorServiceBulkURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.common.mock.FixedURIFetcher;

/**
 * Helps with the creation of {@link BulkURIFetcher}s.
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public final class BulkURIFetchers {
    private static final HttpParams PARAMS = new BasicHttpParams();

    static {
        HttpConnectionParams.setConnectionTimeout(PARAMS, 20 * 1000);
        HttpProtocolParams.setVersion(PARAMS, HttpVersion.HTTP_1_1);
    }
    
    /** utility class */
    private BulkURIFetchers() {
        // void
    }

    
    /** create a single threaded {@link BulkURIFetcher} */
    public static BulkURIFetcher createHttpSingleThreaded() {
        return new ExecutorServiceBulkURIFetcher(
                Executors.newSingleThreadExecutor(),
                new HTTPClientURIFetcher(new DefaultHttpClient(PARAMS))
        );
    }
    
    /** create a single threaded {@link BulkURIFetcher} */
    public static BulkURIFetcher createHttpMultithreaded(final int nThreads) {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", 
                SSLSocketFactory.getSocketFactory(), 443));
        
        final ClientConnectionManager cm = new ThreadSafeClientConnManager(
                PARAMS, schemeRegistry);


        return new ExecutorServiceBulkURIFetcher(
                Executors.newSingleThreadExecutor(),
                new HTTPClientURIFetcher(new DefaultHttpClient(cm, PARAMS))
        );
    }

    /** @return an offline {@link BulkURIFetcher} */
    public static BulkURIFetcher createFixed(final Map<URI, String> map) {
        return new ExecutorServiceBulkURIFetcher(
                Executors.newSingleThreadExecutor(),
                new FixedURIFetcher(map));
    }
}
