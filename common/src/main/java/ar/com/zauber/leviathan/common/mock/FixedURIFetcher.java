/**
 * Copyright (c) 2009-2015 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common.mock;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcher;

/**
 * {@link URIFetcher} used for tests. Get the content from the classpath.
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class FixedURIFetcher extends AbstractMockUriFetcher  {
    private final Map<URI, String> map;

    /** Creates the FixedContentProvider. */
    public FixedURIFetcher(final Map<URI, String> map) {
        this(map, Charset.defaultCharset());
    }

    /** Creates the FixedContentProvider. */
    public FixedURIFetcher(final Map<URI, String> map,
            final Charset charset) {
        super(charset);
        Validate.notNull(map);

        this.map = map;
    }

    @Override
    protected final String getDestinationURL(final URI uri) {
        return map.get(uri);
    }

}
