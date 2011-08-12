/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl;

import java.io.Reader;

import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import ar.com.zauber.commons.validate.Validate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * A {@link Pipe} that sanitizes an HTML document using {@link Tidy}.
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class HTMLSanitizerPipe implements Pipe<URIFetcherResponse, Document> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Tidy tidy;

    /**
     * Creates the HTMLSanitizerPipe.
     *
     * @param tidy
     */
    public HTMLSanitizerPipe(final Tidy tidy) {
        Validate.notNull(tidy);
        this.tidy = tidy;
    }

    /**
     * Creates the HTMLSanitizerPipe with a default configuration for {@link Tidy}
     */
    public HTMLSanitizerPipe() {
        this.tidy = new Tidy();
        // TODO XXX Poner buenos defaults
    }

    @Override
    public Document execute(final URIFetcherResponse response) {
        Validate.notNull(response);
        // TODO Move response status code validation to a previous pipe
        try {
            if (response.isSucceeded()) {
                final Reader input = response.getHttpResponse().getContent();
                return tidy.parseDOM(input, new NullWriter());
            } else {
                logger.error("Fetch did not succeded: " + response.getURIAndCtx().getURI());
                // TODO
                throw new NotImplementedException("Handle exception with context stack handlers", 
                        response.getError());
            }
        } catch (final Throwable e) {
            logger.error("An error ocurred while processing " + response.getURIAndCtx().getURI(), e);
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers");
        }
    }

}
