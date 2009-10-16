/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;

/**
 * {@link BulkURIFetcher} that uses {@link ExecutorService} to handle work
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class ExecutorServiceBulkURIFetcher implements BulkURIFetcher {
    private final ExecutorService executorService;
    private final URIFetcher uriFetcher;
        
    /**
     * @param executorService  {@link ExecutorService} used to balance work
     * @param uriFetcher {@link URIFetcher} used to retrieve content
     */
    public ExecutorServiceBulkURIFetcher(final ExecutorService executorService,
            final URIFetcher uriFetcher) {
        Validate.notNull(executorService, "executor service is null");
        Validate.notNull(uriFetcher, "uriFetcher is null");
        
        this.executorService = executorService;
        this.uriFetcher = uriFetcher;
    }
    
    /** @see BulkURIFetcher#fetch(Collection) */
    public final BulkURIFetcherResponse fetch(final Collection<URI> urisIn) {
        final Collection<URI> uris = new ArrayList<URI>(urisIn);
        final CompletionService<URIFetcherResponse> completion = 
            new ExecutorCompletionService<URIFetcherResponse>(executorService);
        
        
        for(final URI uri : uris) {
            completion.submit(new Callable<URIFetcherResponse>() {
                public URIFetcherResponse call() throws Exception {
                    return uriFetcher.fetch(uri);
                }
            });
        }
        int n = uris.size();
        final List<URIFetcherResponse> responses = new ArrayList<URIFetcherResponse>(
                uris.size());
        for (int i = 0; i < n; i++) {
            try {
                responses.add(completion.take().get());
            } catch (final InterruptedException e) {
                throw new UnhandledException(e);
            } catch (final ExecutionException e) {
                throw new UnhandledException(e);
            }
        }
        return new InmutableBulkURIFetcherResponse(responses);
    }

    /** @see BulkURIFetcher#fetch(java.net.URI) */
    public final URIFetcherResponse fetch(final URI uri) {
        return fetch(Arrays.asList(uri)).getDetails().get(uri);
    }
}
