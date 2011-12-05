/**
 * Copyright (c) 2009-2011 Zauber S.A. <http://www.zaubersoftware.com/>
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

import static java.util.Arrays.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParamBean;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.params.HttpProtocolParams;

import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.BulkURIFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ExecutorServiceBulkURIFetcher;
import ar.com.zauber.leviathan.common.async.FetchQueueAsyncUriFetcher;
import ar.com.zauber.leviathan.common.async.Job;
import ar.com.zauber.leviathan.common.async.JobScheduler;
import ar.com.zauber.leviathan.common.async.impl.BlockingQueueJobQueue;
import ar.com.zauber.leviathan.common.async.impl.MultiDomainPoliteJobQueue;
import ar.com.zauber.leviathan.common.async.impl.OutputStreamAsyncUriFetcherObserver;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;
import ar.com.zauber.leviathan.common.utils.BlockingRejectedExecutionHandler;
import ar.com.zauber.leviathan.impl.httpclient.HTTPClientURIFetcher;
import ar.com.zauber.leviathan.impl.httpclient.charset.ChainedCharsetStrategy;
import ar.com.zauber.leviathan.impl.httpclient.charset.DefaultHttpCharsetStrategy;
import ar.com.zauber.leviathan.impl.httpclient.charset.FixedCharsetStrategy;

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

    /** @return an offline {@link BulkURIFetcher} */
    public static BulkURIFetcher createFixed(final Map<URI, String> map) {
        return new ExecutorServiceBulkURIFetcher(
                Executors.newSingleThreadExecutor(),
                new FixedURIFetcher(map));
    }
    
    /** create a safe {@link URIFetcher} */
    public static URIFetcher createSafeHttpClientURIFetcher() {
        final Map<String, Scheme> registries = new HashMap<String, Scheme>();
        registries.put("http", new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registries.put("http", new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        final SchemeRegistry schemaRegistry = new SchemeRegistry();
        schemaRegistry.setItems(registries);
        
        final HttpParams params = createHttpParams();
        
        final ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemaRegistry);
        final HttpClient httpclient = new DefaultHttpClient(cm, params);
        
        final CharsetStrategy charsetStrategy = new ChainedCharsetStrategy(asList(
                new DefaultHttpCharsetStrategy(), new FixedCharsetStrategy("utf-8")));
        
        return new HTTPClientURIFetcher(httpclient, charsetStrategy);
    }
    

    /** TODO we should load these from a Properties file. */
    private static HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();
        
        final HttpProtocolParamBean httpParams = new HttpProtocolParamBean(params);
        httpParams.setContentCharset("iso-8859-1");
        httpParams.setHttpElementCharset("iso-8859-1");
        httpParams.setUseExpectContinue(true);
        httpParams.setUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6.4; "
                             +  "InfoPath.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; "
                             + ".NET CLR 3.5.30729; OfficeLiveConnector.1.3; OfficeLivePatch.0.0)");
        httpParams.setVersion(HttpVersion.HTTP_1_1);
        
        final HttpConnectionParamBean connectionParam = new HttpConnectionParamBean(params);
        connectionParam.setConnectionTimeout(10000);
        connectionParam.setSoTimeout(20000);
        connectionParam.setLinger(-1);
        connectionParam.setSocketBufferSize(8 * 1024);
        connectionParam.setStaleCheckingEnabled(true);
        connectionParam.setTcpNoDelay(true);
        
        final ClientParamBean clientParam = new ClientParamBean(params);
        clientParam.setHandleRedirects(true);
        clientParam.setRejectRelativeRedirect(false);
        clientParam.setMaxRedirects(10);
        clientParam.setAllowCircularRedirects(false);
        
        final ConnManagerParamBean connManagerParam = new ConnManagerParamBean(params);
        connManagerParam.setConnectionsPerRoute(new ConnPerRouteBean(5));
        connManagerParam.setMaxTotalConnections(100);
        
        final ConnManagerParamBean connConnectionParam = new ConnManagerParamBean(params);
        return params;
    }
    
    /** builds a safe default {@link AsyncUriFetcher} */
    public static AsyncUriFetcher createAsyncUriFetcher() {
        final JobScheduler fecherScheduler = new JobScheduler(
                new MultiDomainPoliteJobQueue(500L, TimeUnit.MILLISECONDS), 
                new ThreadPoolExecutor(1, 20, 10, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), 
                        new BlockingRejectedExecutionHandler()));
        
        final JobScheduler procesessingScheduler = new JobScheduler(
                new BlockingQueueJobQueue<Job>(new LinkedBlockingQueue<Job>()), 
                new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), 
                        new BlockingRejectedExecutionHandler()), new Timer(true), 10000);
        
        final FetchQueueAsyncUriFetcher f = new FetchQueueAsyncUriFetcher(createSafeHttpClientURIFetcher(),
                fecherScheduler, 
                procesessingScheduler);
        f.setObserver(new OutputStreamAsyncUriFetcherObserver());
        return f;
    }
}
