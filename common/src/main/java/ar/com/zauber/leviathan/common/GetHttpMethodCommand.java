/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import java.net.URI;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Implementación de {@link HttpMethodCommand} para GET.
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 12, 2010
 */
public class GetHttpMethodCommand implements HttpMethodCommand {

    private final URIFetcher fetcher;
    private final URIFetcherResponse.URIAndCtx uri;

    /** Creates the GetHttpMethodCommand. */
    public GetHttpMethodCommand(final URIFetcher fetcher,
            final URIFetcherResponse.URIAndCtx uri) {
        Validate.notNull(fetcher);
        Validate.notNull(uri);
        this.fetcher = fetcher;
        this.uri = uri;
    }

    /** @see ar.com.zauber.leviathan.common.HttpMethodCommand#execute() */
    public final URIFetcherResponse execute() {
        return fetcher.get(uri);
    }

    /** @see ar.com.zauber.leviathan.common.HttpMethodCommand#getURI() */
    public final URI getURI() {
        return uri.getURI();
    }

}
