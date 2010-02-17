/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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
package ar.com.zauber.leviathan.impl;

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

import ar.com.zauber.leviathan.api.BulkURIFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ExecutorServiceBulkURIFetcher;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;
import ar.com.zauber.leviathan.impl.httpclient.HTTPClientURIFetcher;

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

    /** @return un {@link BulkURIFetchers} usando un uriFetcher especifico */
    public static BulkURIFetcher createBulkURIFetcher(final int nThreads,
            final URIFetcher uriFetcher) {

        return new ExecutorServiceBulkURIFetcher(
                Executors.newFixedThreadPool(nThreads), uriFetcher);
    }

    /** create a single threaded {@link BulkURIFetcher} */
    public static BulkURIFetcher createHttpMultithreaded(final int nThreads) {
        return createBulkURIFetcher(nThreads, createHttpURIFetcher());
    }

    /** @return un HTTP Fetcher */
    public static URIFetcher createHttpURIFetcher() {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));

        final ClientConnectionManager cm = new ThreadSafeClientConnManager(
                PARAMS, schemeRegistry);
        final URIFetcher fetcher = new HTTPClientURIFetcher(
                new DefaultHttpClient(cm, PARAMS));
        return fetcher;
    }

    /** @return un HTTP Fetcher */
    public static URIFetcher createHttpURIFetcher(final CharsetStrategy strategy) {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));

        final ClientConnectionManager cm = new ThreadSafeClientConnManager(
                PARAMS, schemeRegistry);
        final URIFetcher fetcher = new HTTPClientURIFetcher(
                new DefaultHttpClient(cm, PARAMS), strategy);
        return fetcher;
    }

    /** @return an offline {@link BulkURIFetcher} */
    public static BulkURIFetcher createFixed(final Map<URI, String> map) {
        return new ExecutorServiceBulkURIFetcher(
                Executors.newSingleThreadExecutor(),
                new FixedURIFetcher(map));
    }
}