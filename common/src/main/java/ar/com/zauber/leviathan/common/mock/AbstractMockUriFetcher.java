/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.UrlEncodedPostBody;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.AbstractURIFetcher;
import ar.com.zauber.leviathan.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.leviathan.common.InmutableURIFetcherResponse;

/**
 * TODO Descripcion de la clase. Los comenterios van en castellano.
 * 
 * 
 * @author Juan F. Codagnone
 * @since Sep 20, 2010
 */
public abstract class AbstractMockUriFetcher extends AbstractURIFetcher {
    private final Charset charset;

    /** Creates the FixedContentProvider. */
    public AbstractMockUriFetcher() {
        this(Charset.defaultCharset());
    }

    /** Creates the FixedContentProvider. */
    public AbstractMockUriFetcher(final Charset charset) {
        Validate.notNull(charset);

        this.charset = charset;
    }

    /**
     * @see URIFetcher#fetch(URI)
     * @deprecated Use {@link #get(URI)} instead. Will be deprecated on
     *             next version.
     */
    @Deprecated
    public final URIFetcherResponse fetch(final URI uri) {
        return get(uri);
    }
    
    /**
     * @see URIFetcher#fetch(URIAndCtx)
     * @deprecated Use {@link #get(URIAndCtx)} instead. Will be deprecated on
     *             next version.
     */
    @Deprecated
    public final URIFetcherResponse fetch(final URIAndCtx uri) {
        return get(uri);
    }

    /** @see URIFetcher#get(URIFetcherResponse.URIAndCtx) */
    @SuppressWarnings("unchecked")
    public final URIFetcherResponse get(final URIAndCtx uriAndCtx) {
        final URI uri = uriAndCtx.getURI();
        final String destURL = getDestinationURL(uri);
        final URIFetcherResponse ret;

        if(destURL == null) {
            ret = new  InmutableURIFetcherResponse(uriAndCtx,
                    new UnknownHostException(uri.getHost()));
        } else {
            final InputStream is = getClass().getClassLoader().getResourceAsStream(
                    destURL);

            if(is == null) {
                ret = new  InmutableURIFetcherResponse(uriAndCtx,
                        new UnknownHostException(uri.getHost()));
            } else {
                try {
                    ret = new InmutableURIFetcherResponse(uriAndCtx,
                            new InmutableURIFetcherHttpResponse(new String(
                                    IOUtils.toByteArray(is), charset
                                            .displayName()), 200,
                                    Collections.EMPTY_MAP));
                } catch (IOException e) {
                    throw new UnhandledException(e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        return ret;
    }
    
    /** obtiene la URL local del archivo */
    protected abstract String getDestinationURL(URI uri);

    /** @see URIFetcher#post(URIFetcherResponse.URIAndCtx, InputStream) */
    public final URIFetcherResponse post(final URIAndCtx uri,
            final InputStream body) {
        throw new NotImplementedException("Post to classpath not implemented");
    }
    
    /** @see URIFetcher#post(URIFetcherResponse.URIAndCtx, Map) */
    public final URIFetcherResponse post(final URIAndCtx uri,
            final Map<String, String> body) {
        throw new NotImplementedException("Post to classpath not implemented");
    }

    /** @see URIFetcher#post(URIAndCtx, UrlEncodedPostBody) */
    public final URIFetcherResponse post(final URIAndCtx uriAndCtx, 
            final UrlEncodedPostBody body) {
        throw new NotImplementedException("Post to classpath not implemented");

    }

}
