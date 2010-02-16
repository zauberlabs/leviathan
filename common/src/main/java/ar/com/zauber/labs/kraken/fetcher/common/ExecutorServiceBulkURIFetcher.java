/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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
package ar.com.zauber.labs.kraken.fetcher.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcher;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse;
import ar.com.zauber.labs.kraken.fetcher.api.URIFetcherResponse.URIAndCtx;

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

    /** @see BulkURIFetcher#fetchCtx(Collection) */
    public final BulkURIFetcherResponse fetch(final Iterable<URI> urisIn) {
        return fetchCtx(new Iterable<URIAndCtx>() {
            public Iterator<URIAndCtx> iterator() {
                final Iterator<URI> iter = urisIn.iterator();
                return new Iterator<URIAndCtx>() {
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    public URIAndCtx next() {
                        return new InmutableURIAndCtx(iter.next());
                    }

                    public void remove() {
                        iter.remove();
                    }
                };
            }
        });
    }


    /** @see BulkURIFetcher#fetch(Collection) */
    public final BulkURIFetcherResponse fetchCtx(
            final Iterable<URIAndCtx> uriAndCtxs) {
        final Collection<URIAndCtx> uris = new ArrayList<URIAndCtx>();
        for(final URIAndCtx u : uriAndCtxs) {
            uris.add(u);
        }

        final CompletionService<URIFetcherResponse> completion =
            new ExecutorCompletionService<URIFetcherResponse>(executorService);


        for(final URIAndCtx contexteableURI : uris) {
            completion.submit(new Callable<URIFetcherResponse>() {
                public URIFetcherResponse call() throws Exception {
                    return uriFetcher.fetch(contexteableURI);
                }
            });
        }
        int n = uris.size();
        final List<URIFetcherResponse> responses = new ArrayList<URIFetcherResponse>(
                uris.size());
        for (int i = 0; i < n; i++) {
            try {
                final Future<URIFetcherResponse> future = completion.take();
                responses.add(future.get());
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

    /** @see URIFetcher#fetch(URIFetcherResponse.URIAndCtx) */
    public final URIFetcherResponse fetch(final URIAndCtx uri) {
        return fetchCtx(Arrays.asList(uri)).getDetails().get(uri);
    }
}
