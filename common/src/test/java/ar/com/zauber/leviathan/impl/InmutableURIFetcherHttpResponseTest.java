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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    public final void testEquals()  {
        assertEquals(
            new InmutableURIFetcherHttpResponse("foo", 200),
                            
            new InmutableURIFetcherHttpResponse("foo", 200));
    }
    
    /** test */
    @Test
    public final void testNotEquals()  {
        assertFalse(
            new InmutableURIFetcherHttpResponse("foo", 404).equals(
            new InmutableURIFetcherHttpResponse("foo", 200)));
    }
}
