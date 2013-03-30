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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.InmutableURIFetcherHttpResponse;
import ar.com.zauber.leviathan.common.InmutableURIFetcherResponse;


/**
 * Test
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class InmutableURIFetcherResponseTest {

    /** test failed */
    @Test
    public final void failed() throws Exception {
        final URIFetcherResponse r = new InmutableURIFetcherResponse(
                new URI("http://bar"),
                new IllegalArgumentException("foo"));
        assertFalse(r.isSucceeded());
        assertEquals(IllegalArgumentException.class, r.getError().getClass());
        assertEquals("foo", r.getError().getMessage());
        assertEquals(new URI("http://bar"), r.getURIAndCtx().getURI());
        try {
            r.getHttpResponse();
            fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    /** test failed */
    @SuppressWarnings("unchecked")
    @Test
    public final void success() throws Exception {
        final URIFetcherResponse r = new InmutableURIFetcherResponse(
                new URI("http://bar"),
                new InmutableURIFetcherHttpResponse("foo", 200, 
                        Collections.EMPTY_MAP));
        assertTrue(r.isSucceeded());
        assertEquals(200, r.getHttpResponse().getStatusCode());
        assertEquals("foo", IOUtils.toString(r.getHttpResponse().getContent()));
        assertEquals(new URI("http://bar"), r.getURIAndCtx().getURI());

        try {
            r.getError();
            fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    /** test failed */
    @SuppressWarnings("unchecked")
    @Test
    public final void equalsHttp() throws Exception {
        assertEquals(
            new InmutableURIFetcherResponse(new URI("http://bar"),
                new InmutableURIFetcherHttpResponse("foo", 200, 
                        Collections.EMPTY_MAP)),
            new InmutableURIFetcherResponse(new URI("http://bar"),
                new InmutableURIFetcherHttpResponse("foo", 200, 
                        Collections.EMPTY_MAP)));
    }
    /** test failed */
    @Test
    public final void equalsFail() throws Exception {
        assertEquals(
            new InmutableURIFetcherResponse(new URI("http://bar"),
                new IllegalArgumentException("a")),
            new InmutableURIFetcherResponse(new URI("http://bar"),
                    new IllegalArgumentException("a")));
    }

}
