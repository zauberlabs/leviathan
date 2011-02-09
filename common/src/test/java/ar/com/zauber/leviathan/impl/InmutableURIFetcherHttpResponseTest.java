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
package ar.com.zauber.leviathan.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import ar.com.zauber.leviathan.common.InmutableURIFetcherHttpResponse;


/**
 * Tests {@link InmutableURIFetcherHttpResponse
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public class InmutableURIFetcherHttpResponseTest {

    /** test */
    @Test
    public final void testHeaders() throws Exception {
        Map<String, List<String>> headers = new TreeMap<String, List<String>>();
        headers.put("Accept", Arrays.asList("application/json"));
        headers.put("Header1", Arrays.asList("value1", "value2"));
        InmutableURIFetcherHttpResponse response = 
            new InmutableURIFetcherHttpResponse("foo", 200, headers);
        
        assertEquals("application/json", response.getHeader("Accept"));
        assertEquals(null, response.getHeader("Content-Type"));
        assertEquals("value1", response.getHeader("Header1"));
        
        List<String> headers2 = response.getHeaders("Header1");
        assertEquals(2, headers2.size());
        assertEquals("value1", headers2.get(0));
        assertEquals("value2", headers2.get(1));
    }

    /** test */
    @SuppressWarnings("unchecked")
    @Test
    public final void testEquals()  {
        assertEquals(
            new InmutableURIFetcherHttpResponse("foo", 200, Collections.EMPTY_MAP),
            new InmutableURIFetcherHttpResponse("foo", 200, Collections.EMPTY_MAP));
    }
    
    /** test */
    @SuppressWarnings("unchecked")
    @Test
    public final void testNotEquals()  {
        assertFalse(
            new InmutableURIFetcherHttpResponse("foo", 404, 
                    Collections.EMPTY_MAP).equals(
            new InmutableURIFetcherHttpResponse("foo", 200, 
                    Collections.EMPTY_MAP)));
    }
    
    @Test
    public final void testGetHeaderInsensitiveCase() throws Exception {
        final Map<String, List<String>> headers = 
            new HashMap<String, List<String>>();
        headers.put("Content-Type", Arrays.asList("text/html; charset=utf8"));
        
        assertNotNull(new InmutableURIFetcherHttpResponse("", 200, headers)
        .getHeader("content-type"));
    }
}
