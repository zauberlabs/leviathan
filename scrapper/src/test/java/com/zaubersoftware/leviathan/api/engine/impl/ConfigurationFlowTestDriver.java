/*
 * Copyright (c) 2011 Zauber S.A. -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;

import com.zaubersoftware.leviathan.api.engine.impl.dto.Link;

/**
 * Test driver for the configuration flow
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ConfigurationFlowTestDriver {
    private final URI mlhome = URI.create("http://www.mercadolibre.com.ar/");
    
    /** Tests the flow */
    @Test
    public void test() throws Exception {
        // Fetcher Configuration
        final Map<URI, String> pages = new HashMap<URI, String>();
        pages.put(mlhome, "com/zaubersoftware/leviathan/api/engine/pages/homeml.html");
        
        final URIFetcher httpClientFetcher = new FixedURIFetcher(pages);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final AsyncUriFetcher fetcher = new ExecutorServiceAsyncUriFetcher(executor, httpClientFetcher);

        // Pipe chain
        final Source xsltSource = new StreamSource(getClass().getClassLoader().getResourceAsStream(
        "com/zaubersoftware/leviathan/api/engine/stylesheet/html.xsl"));
        
        
        final Closure<URIFetcherResponse> rootClosure = new PipeClosure<Object>(
            new CompositePipe<URIFetcherResponse, Object>(Arrays.asList(new Pipe<?, ?>[] { 
                    new HTMLSanitizerPipe(),
                    new XMLPipe(xsltSource),
                    new ToJavaObjectPipe<Link>(Link.class),
            })));

        fetcher.get(mlhome, rootClosure);
        fetcher.awaitIdleness();
    }

}
