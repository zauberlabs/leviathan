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
package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.impl.httpclient.InmutableResponseMetadata;

/**
 * Tests para {@link XMLCharsetStrategy}
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 9, 2010
 */
public class XMLCharsetStrategyTest {
    
    /** prueba del {@link XMLCharsetStrategy} */
    @Test
    public final void aplicarEstrategiaTest() throws Exception {
        final InputStream is = getClass().getClassLoader()
            .getResourceAsStream("ar/com/zauber/leviathan/impl/mock/utf8.xml");
        
        final InmutableResponseMetadata meta =
            new InmutableResponseMetadata(
                new URI("http://example.com"), "text/xml", null, 200, "iso-9851");
        
        Assert.assertEquals(
            Charset.forName("utf-8"), 
            new XMLCharsetStrategy().getCharset(meta, is));
    }    

}
