/**
 * Copyright (c) 2009-2013 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.fluent.Fetchers;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;


/**
 * Tests {@link FixedURIFetcher}
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class FixedURIFetcherTest {
    private final URIFetcher fetcher;

    /** constructor */
    public FixedURIFetcherTest() throws URISyntaxException {
        fetcher = Fetchers.createFixed().withCharset("iso-8859-1")
                 .when("http://noexiste").then("ar/com/zauber/leviathan/impl/mock/noexiste.txt")
                 .build();
    }

    /** test found  */
    @Test
    public final void found() throws URISyntaxException, IOException {
        final URIFetcherResponse r = fetcher.createGet(new URI("http://noexiste")).execute();
        Assert.assertNotNull(r);
        Assert.assertEquals(200, r.getHttpResponse().getStatusCode());
        Assert.assertEquals("�o�o", 
                IOUtils.toString(r.getHttpResponse().getContent()));
        
        
    }
    
    /** test found  */
    @Test
    public final void notfound() throws URISyntaxException, IOException {
        final URIFetcherResponse r = fetcher.createGet(new URI("http://lalarara")).execute();
        Assert.assertNotNull(r);
        Assert.assertFalse(r.isSucceeded());
        Assert.assertEquals(UnknownHostException.class, r.getError().getClass());
    }
}
