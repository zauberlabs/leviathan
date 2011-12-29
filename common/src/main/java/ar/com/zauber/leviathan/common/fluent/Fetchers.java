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
package ar.com.zauber.leviathan.common.fluent;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;

/**
 * Fluent creation of fetchers
 *
 *
 * @author Juan F. Codagnone
 * @since Dec 29, 2011
 */
public final class Fetchers {

    /** Creates the Fetchers. */
    private Fetchers() {
        // utility class
    }

    /** @return a fixed uri fetcher usefull for testing  */
    public static FixedFetcherBuilder createFixed() {
        return new DefaultFixedFetcherBuilder();
    }
}

/**
 * Implementación default del {@link DefaultFixedFetcherBuilder}
 *
 *
 * @author Juan F. Codagnone
 * @since Dec 29, 2011
 */
final class DefaultFixedFetcherBuilder implements FixedFetcherBuilder {
    private final ThenMockFetcherBuilder then = new ThenMockFetcherBuilder() {
        @Override
        public FixedFetcherBuilder then(final String classpath) {
            map.put(pendingURI, classpath);
            return DefaultFixedFetcherBuilder.this;
        }
    };
    private final Map<URI, String> map = new HashMap<URI, String>();
    private URI pendingURI;
    private Charset ch = Charset.defaultCharset();

    @Override
    public ThenMockFetcherBuilder when(final String uri) {
        pendingURI = URI.create(uri);
        return then;
    }

    @Override
    public ThenMockFetcherBuilder when(final URI uri) {
        pendingURI = uri;
        return then;
    }

    @Override
    public URIFetcher build() {
        return new FixedURIFetcher(map, ch);
    }

    @Override
    public FixedFetcherBuilder register(final String uri, final String classpath) {
        return register(URI.create(uri), classpath);
    }

    @Override
    public FixedFetcherBuilder register(final URI uri, final String classpath) {
        map.put(uri, classpath);
        return this;
    }

    @Override
    public FixedFetcherBuilder withCharset(final String c) {
        ch = Charset.forName(c);
        return this;
    }

    @Override
    public FixedFetcherBuilder withCharset(final Charset c) {
        ch = c;
        return this;
    }
}
