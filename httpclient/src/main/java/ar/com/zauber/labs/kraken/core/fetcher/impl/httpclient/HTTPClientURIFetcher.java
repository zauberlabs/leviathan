/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.charset.DefaultHttpCharsetStrategy;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.labs.kraken.fetcher.common.CharsetStrategy;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIAndCtx;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.labs.kraken.fetcher.common.InmutableURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.common.ResponseMetadata;

/**
 * {@link URIFetcher} that uses Apache's HttpClient components
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class HTTPClientURIFetcher implements URIFetcher {
    private final HttpClient httpClient;
    private final CharsetStrategy charsetStrategy;

    /** constructor
     * utiliza la default charset strategy */
    public HTTPClientURIFetcher(final HttpClient httpClient) {
        this(httpClient, new DefaultHttpCharsetStrategy());
    }



    /**
     * Creates the HTTPClientURIFetcher.
     *
     * @param httpClient2
     * @param defaultStrategy
     */
    public HTTPClientURIFetcher(final HttpClient httpClient,
            final CharsetStrategy defaultStrategy) {
        Validate.notNull(httpClient);
        Validate.notNull(defaultStrategy);

        this.charsetStrategy = defaultStrategy;
        this.httpClient = httpClient;
    }



    /** @see URIFetcher#fetch(URI) */
    public final URIFetcherResponse fetch(final URI uri) {
        return fetch(new InmutableURIAndCtx(uri));
    }

    /** @see URIFetcher#fetch(URIFetcherResponse.URIAndCtx) */
    public final URIFetcherResponse fetch(final URIAndCtx uriAndCtx) {
        final URI uri = uriAndCtx.getURI();
        Validate.notNull(uri, "uri is null");
        HttpResponse response = null;
        try {
            ResponseMetadata meta = null;
            InputStream content = null;
            response  = httpClient.execute(new HttpGet(uri));
            final HttpEntity entity = response.getEntity();
            if(entity != null) {
                content = response.getEntity().getContent();
                meta = getMetaResponse(uri, response, entity);
            }
            final Charset charset = charsetStrategy.getCharset(meta, content);

            final int status = response.getStatusLine().getStatusCode();
            return new InmutableURIFetcherResponse(uriAndCtx,
                new InmutableURIFetcherHttpResponse(
                                IOUtils.toString(content, charset.displayName()),
                                status));
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
     * @param uri
     * @param response
     * @param entity
     * @return
     */
    private ResponseMetadata getMetaResponse(final URI uri,
            final HttpResponse response, final HttpEntity entity) {
        String contentType = null;
        String contentEncoding = null;
        if(entity.getContentType() != null) {
            contentType = entity.getContentType().getValue();
        }
        if(entity.getContentEncoding() != null) {
            contentEncoding = entity.getContentEncoding().getValue();
        }
        final int status = response.getStatusLine().getStatusCode();
        return new InmutableResponseMetadata(uri, contentType,
                contentEncoding, status);
    }



    /**
     * When HttpClient instance is no longer needed,
     * shut down the connection manager to ensure
     */
    public final void shutdown() {
        httpClient.getConnectionManager().shutdown();
    }
}
