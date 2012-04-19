/**
 * Copyright (c) 2009-2012 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * URIAndCtx inmutable implementation
 *
 * @author Mariano Semelman
 * @since Dec 2, 2009
 */
public class InmutableURIAndCtx implements URIFetcherResponse.URIAndCtx,
                                           Serializable {
    private static final long serialVersionUID = 5270026214580346190L;

    private final URI uri;
    private final transient Map<String, Object> ctx;

    /** Creates the InmutableURIAndCtx. */
    @SuppressWarnings("unchecked")
    public InmutableURIAndCtx(final URI uri) {
        this(uri, Collections.EMPTY_MAP);
    }

    /** Creates the InmutableURIAndCtx. */
    public InmutableURIAndCtx(final URI uri, final Map<String, Object> ctx) {
        Validate.notNull(uri);
        Validate.notNull(ctx);

        this.uri = uri;
        this.ctx = Collections.unmodifiableMap(ctx);
    }

    @Override
    public final Map<String, Object> getCtx() {
        return ctx;
    }

    @Override
    public final URI getURI() {
        return uri;
    }
    
    @Override
    public final String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                                  .append(uri).append(ctx).toString();
    }
}
