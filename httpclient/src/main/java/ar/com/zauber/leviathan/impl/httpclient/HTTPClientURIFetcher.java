/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.impl.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ar.com.zauber.leviathan.api.FetchingTask;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;
import ar.com.zauber.leviathan.common.AbstractURIFetcher;
import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.leviathan.common.InmutableURIFetcherResponse;
import ar.com.zauber.leviathan.common.ResponseMetadata;
import ar.com.zauber.leviathan.impl.httpclient.charset.DefaultHttpCharsetStrategy;
import ar.com.zauber.leviathan.impl.httpclient.charset.SkipBytesException;

/**
 * {@link URIFetcher} that uses Apache's HttpClient components
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class HTTPClientURIFetcher extends AbstractURIFetcher {
    private final HttpClient httpClient;
    private final CharsetStrategy charsetStrategy;

    /** constructor utiliza la default charset strategy */
    public HTTPClientURIFetcher(final HttpClient httpClient) {
        this(httpClient, new DefaultHttpCharsetStrategy());
    }
     
    /** Creates the HTTPClientURIFetcher.*/
    public HTTPClientURIFetcher(final HttpClient httpClient,
            final CharsetStrategy defaultStrategy) {
        Validate.notNull(httpClient);
        Validate.notNull(defaultStrategy);

        this.charsetStrategy = defaultStrategy;
        this.httpClient = httpClient;
    }
    
    
    
    
    @Override
    public final FetchingTask createGet(final URIAndCtx uriAndCtx) {
        return new FetchingTask() {
            
            @Override
            public URIAndCtx getURIAndCtx() {
                return uriAndCtx;
            }
            
            @Override
            public URIFetcherResponse execute() {
                return fetchInternal(uriAndCtx, new HttpGet(uriAndCtx.getURI()));
            }
        };
    }
    
    @Override
    public final FetchingTask createPost(final URIAndCtx uriAndCtx, final InputStream body) {
        return new FetchingTask() {
            @Override
            public URIAndCtx getURIAndCtx() {
                return uriAndCtx;
            }
            
            @Override
            public URIFetcherResponse execute() {
                try {
                    final HttpPost httpPost = new HttpPost(uriAndCtx.getURI());
                    httpPost.setEntity(new ByteArrayEntity(IOUtils.toByteArray(body)));
                    return fetchInternal(uriAndCtx, httpPost);
                } catch (final Throwable e) {
                    return new InmutableURIFetcherResponse(uriAndCtx, 
                            new UnhandledException("reading post entity", e));
                }
            }
        };
    }


        @Override
    public final FetchingTask createPost(final URIAndCtx uriAndCtx, final UrlEncodedPostBody body) {
        return new FetchingTask() {
            @Override
            public URIAndCtx getURIAndCtx() {
                return uriAndCtx;
            }
            
            @Override
            public URIFetcherResponse execute() {
                try {
                    final HttpPost httpPost = new HttpPost(uriAndCtx.getURI());

                    final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    
                    for (final String simpleParam : body.getSimpleParameters()) {
                        pairs.add(new BasicNameValuePair(simpleParam,
                                  body.getSimpleParameter(simpleParam)));
                    }
                    
                    for (final String collectionParam : body.getCollectionParameters()) {
                        for (final String value : body.getCollectionParameter(collectionParam)) {
                            pairs.add(new BasicNameValuePair(collectionParam, value));
                        }
                    }
                    
                    final String content = URLEncodedUtils.format(pairs, "UTF-8");
                    final ByteArrayEntity entity = new ByteArrayEntity(content.getBytes());
                    entity.setContentType("application/x-www-form-urlencoded");
                    httpPost.setEntity(entity);
                    return fetchInternal(uriAndCtx, httpPost);
                } catch(final Throwable t) {
                    return new InmutableURIFetcherResponse(uriAndCtx, t);
                }
            }
        };
    }
    

    /**
     * Actual fetching.
     * 
     * @param httpMethod 
     */
    private URIFetcherResponse fetchInternal(final URIAndCtx uriAndCtx,
            final HttpUriRequest httpMethod) {
        HttpResponse response = null;
        try {
            final URI uri = uriAndCtx.getURI();
            Validate.notNull(uri, "uri is null");
            
            response  = httpClient.execute(httpMethod);
            final Map<String, List<String>> headers = extractHeaders(response);
            Validate.notNull(response);
            
            HttpEntity entity = response.getEntity();
            Validate.notNull(entity);
            
            InputStream content = entity.getContent();
            Validate.notNull(content);
            
            byte[] data;
            ResponseMetadata meta; 
            try {
                data = IOUtils.toByteArray(content);
                meta = getMetaResponse(uri, response, entity);
            } finally {
                content.close();
                content = null;
                response = null;
                entity = null;
            }

            
            Charset charset;
            try {
               charset = charsetStrategy.getCharset(meta, new ByteArrayInputStream(data));
            } catch (final SkipBytesException e) {
               charset = e.getCharset();
               e.getBytesToSkip();
               data = Arrays.copyOfRange(data, e.getBytesToSkip() - 1, data.length);
            }
               
            return new InmutableURIFetcherResponse(
                uriAndCtx,
                new InmutableURIFetcherHttpResponse(
                    new String(data, charset.displayName()), 
                    meta.getStatusCode(), 
                    headers, data));
            
        } catch (final Throwable e) {
            return new InmutableURIFetcherResponse(uriAndCtx, e);
        } finally {
            if(response != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (final IOException e) {
                    return new InmutableURIFetcherResponse(uriAndCtx, e);
                }
            }
        }
    }


    /**
     * Extracts de headers of the {@link HttpResponse}.
     * 
     * @param response
     * @return the headers map 
     */
    private Map<String, List<String>> extractHeaders(final HttpResponse response) {
        final Map<String, List<String>> out = new TreeMap<String, List<String>>();
        
        final Header[] allHeaders = response.getAllHeaders();
        for (final Header header : allHeaders) {
            List<String> headers = out.get(header.getName());
            if (headers == null) {
                headers = new ArrayList<String>();
                headers.add(header.getValue());
                out.put(header.getName(), headers);
            } else {
                headers.add(header.getValue());
            }
        }
        
        return out;
    }

    /** obtiene el encoding */
    public static ResponseMetadata getMetaResponse(final URI uri,
            final HttpResponse response, final HttpEntity entity) {

        final String contentType = (entity.getContentType() != null)
                                ? entity.getContentType().getValue()
                                : null;
        
        
        final String contentEncoding = (entity.getContentEncoding() != null)
                                    ? entity.getContentEncoding().getValue()
                                    : null;
        
        final int status = (response.getStatusLine() != null) 
                                ? response.getStatusLine().getStatusCode() 
                                : 0;

        return new InmutableResponseMetadata(
                uri, 
                contentType, 
                contentEncoding,
                status, 
                EntityUtils.getContentCharSet(entity));
    }


    /**
     * When HttpClient instance is no longer needed,
     * shut down the connection manager to ensure
     */
    public final void shutdown() {
        httpClient.getConnectionManager().shutdown();
    }

}
