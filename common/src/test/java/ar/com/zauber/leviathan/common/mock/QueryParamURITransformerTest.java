/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
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
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;


/**
 * TODO Descripcion de la clase. Los comenterios van en castellano.
 * 
 * 
 * @author Juan F. Codagnone
 * @since Sep 22, 2010
 */
public class QueryParamURITransformerTest {

    /** test */
    @Test
    public final void testfParametro() throws URISyntaxException {
        Assert.assertEquals("bar", new QueryParamURITransformer("foo").transform(
                new URI("http://lala/?foo=bar&lala=popo")));
    }
    
    /** test */
    @Test
    public final void testfParametroQueNova() throws URISyntaxException {
        Assert.assertEquals(null, new QueryParamURITransformer("xxxx").transform(
                new URI("http://lala/?foo=bar")));
    }
}
