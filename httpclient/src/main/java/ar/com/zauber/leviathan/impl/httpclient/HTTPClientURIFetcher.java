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
package ar.com.zauber.leviathan.impl.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

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

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.UriFetcherRequest;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.AbstractURIFetcher;
import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.DefaultUriFetcherRequest;
import ar.com.zauber.leviathan.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.leviathan.common.InmutableURIFetcherResponse;
import ar.com.zauber.leviathan.common.ResponseMetadata;
import ar.com.zauber.leviathan.impl.httpclient.charset.DefaultHttpCharsetStrategy;

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
    
    /**
     * @see URIFetcher#fetch(URI)
     * @deprecated Use {@link #get(URI)}
     * */
    @Deprecated
    public final URIFetcherResponse fetch(final URI uri) {
        return get(uri);
    }

    /**
     * @see URIFetcher#fetch(URIFetcherResponse.URIAndCtx)
     * @deprecated Use {@link #get(URIAndCtx)}
     */
    @Deprecated
    public final URIFetcherResponse fetch(final URIAndCtx uri) {
        return get(uri);
    }

    
    @Override
    public URIFetcherResponse get(UriFetcherRequest request) {
        return fetchInternal(request, new HttpGet(request.getUriAndCtx().getURI()));
    }
    
    @Override
    public URIFetcherResponse post(UriFetcherRequest request, InputStream body) {
        URIAndCtx uriAndCtx = request.getUriAndCtx();  
        try {
            final HttpPost httpPost = new HttpPost(uriAndCtx.getURI());
            httpPost.setEntity(new ByteArrayEntity(IOUtils.toByteArray(body)));
            return fetchInternal(request, httpPost);
        } catch (final Throwable e) {
            return new InmutableURIFetcherResponse(uriAndCtx, 
                    new UnhandledException("reading post entity", e));
        }
    }
    
    /** @see URIFetcher#post(URIFetcherResponse.URIAndCtx, java.util.Map) */
    public final URIFetcherResponse post(final URIAndCtx uriAndCtx,
            final Map<String, String> body) {
        try {
            final HttpPost httpPost = new HttpPost(uriAndCtx.getURI());
            
            final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            for (String key : body.keySet()) {
                pairs.add(new BasicNameValuePair(key, body.get(key)));
            }
            final String content = URLEncodedUtils.format(pairs, "UTF-8");
            final ByteArrayEntity entity = new ByteArrayEntity(content.getBytes());
            entity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(entity);
            return fetchInternal(DefaultUriFetcherRequest.from(uriAndCtx), httpPost);
        } catch(Throwable t) {
            return new InmutableURIFetcherResponse(uriAndCtx, t);
        }
    }
    
    @Override
    public URIFetcherResponse post(UriFetcherRequest request,
            UrlEncodedPostBody body) {
        try {
            final HttpPost httpPost = new HttpPost(request.getUriAndCtx().getURI());

            final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            
            for (String simpleParam : body.getSimpleParameters()) {
                pairs.add(new BasicNameValuePair(simpleParam,
                          body.getSimpleParameter(simpleParam)));
            }
            
            for (String collectionParam : body.getCollectionParameters()) {
                for (String value : body.getCollectionParameter(collectionParam)) {
                    pairs.add(new BasicNameValuePair(collectionParam, value));
                }
            }
            
            final String content = URLEncodedUtils.format(pairs, "UTF-8");
            final ByteArrayEntity entity = new ByteArrayEntity(content.getBytes());
            entity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(entity);
            return fetchInternal(request, httpPost);
        } catch(Throwable t) {
            return new InmutableURIFetcherResponse(request.getUriAndCtx(), t);
        }
    }

    /**
     * Actual fetching.
     * 
     * @param httpMethod 
     */
    private URIFetcherResponse fetchInternal(final UriFetcherRequest request,
            final HttpUriRequest httpMethod) {
        HttpResponse response = null;
        try {
            final URI uri = request.getUriAndCtx().getURI();
            Validate.notNull(uri, "uri is null");
            
            for (Entry<String, List<String>> e : request.getHeaders().entrySet()) {
                for(String value : e.getValue()) {
                    httpMethod.addHeader(e.getKey(), value);
                }
            }
            
            response  = httpClient.execute(httpMethod);
            Validate.notNull(response);
            
            final HttpEntity entity = response.getEntity();
            Validate.notNull(entity);
            
            InputStream content = entity.getContent();
            Validate.notNull(content);
            
            final byte[] data = IOUtils.toByteArray(content);

            final ResponseMetadata meta = getMetaResponse(uri, response, entity);
            final Charset charset 
                = charsetStrategy.getCharset(meta, new ByteArrayInputStream(data));

            return new InmutableURIFetcherResponse(
                request.getUriAndCtx(),
                new InmutableURIFetcherHttpResponse(
                    new String(data, charset.displayName()), 
                    meta.getStatusCode(), 
                    extractHeaders(response), data));
            
        } catch (final Throwable e) {
            return new InmutableURIFetcherResponse(request.getUriAndCtx(), e);
        } finally {
            if(response != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (final IOException e) {
                    return new InmutableURIFetcherResponse(request.getUriAndCtx(), e);
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
        Map<String, List<String>> out = new TreeMap<String, List<String>>();
        
        final Header[] allHeaders = response.getAllHeaders();
        for (Header header : allHeaders) {
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
