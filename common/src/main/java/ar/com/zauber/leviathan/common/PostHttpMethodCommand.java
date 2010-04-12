/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * Implementación de {@link HttpMethodCommand} para POST.
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 12, 2010
 */
public class PostHttpMethodCommand implements HttpMethodCommand {

    private final URIFetcher fetcher;
    private final URIFetcherResponse.URIAndCtx uri;
    private final InputStream body;
    private final Map<String, String> formBody;

    /** Creates the PostHttpMethodCommand. */
    public PostHttpMethodCommand(final URIFetcher fetcher, final URIAndCtx uri,
            final InputStream body) {
        Validate.notNull(fetcher);
        Validate.notNull(uri);
        Validate.notNull(body);
        this.fetcher = fetcher;
        this.uri = uri;
        this.body = body;
        this.formBody = null;
    }
    
    /** Creates the PostHttpMethodCommand. */
    public PostHttpMethodCommand(final URIFetcher fetcher, final URIAndCtx uri,
            final Map<String, String> formBody) {
        Validate.notNull(fetcher);
        Validate.notNull(uri);
        Validate.notNull(formBody);
        this.fetcher = fetcher;
        this.uri = uri;
        this.body = null;
        this.formBody = formBody;
    }    

    /** @see ar.com.zauber.leviathan.common.HttpMethodCommand#execute() */
    public final URIFetcherResponse execute() {
        if (formBody != null) {
            return fetcher.post(uri, formBody);
        }
        
        if (body != null) {
            return fetcher.post(uri, body);
        } 
        
        throw new IllegalStateException("Nothing to post!");
    }
    
    /** @see ar.com.zauber.leviathan.common.HttpMethodCommand#getURI() */
    public final URI getURI() {
        return uri.getURI();
    }

}
