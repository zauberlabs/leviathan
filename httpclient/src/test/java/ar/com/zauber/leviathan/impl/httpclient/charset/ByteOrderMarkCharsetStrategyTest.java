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
package ar.com.zauber.leviathan.impl.httpclient.charset;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import org.junit.Test;

import ar.com.zauber.leviathan.impl.httpclient.InmutableResponseMetadata;

/**
 * Tests for {@link ByteOrderMarkCharsetStrategy}
 * 
 * @author Guido Marucci Blas
 * @since 31/07/2012
 */
public class ByteOrderMarkCharsetStrategyTest {

    @Test
    public void testGetChasetWithXMLWithUTF8ByteOrderMark() {
        final ByteOrderMarkCharsetStrategy strategy = new ByteOrderMarkCharsetStrategy();
        final URI uri = URI.create("http://example.com");
        final InputStream is = DetectorCharsetStrategy.class
                .getClassLoader()
                .getResourceAsStream("ar/com/zauber/leviathan/impl/mock/bom-utf8.xml");
        final InmutableResponseMetadata meta = new InmutableResponseMetadata(uri, "text/xml", null, 200, null);
        
        Charset charset = null;
        int bytesToSkip = 0;
        try {
            strategy.getCharset(meta, is);                       
        } catch(final SkipBytesException e) {
            charset = e.getCharset();
            bytesToSkip = e.getBytesToSkip();
        }
        
        assertNotNull(charset);
        assertEquals("UTF-8", charset.displayName());
        assertEquals(3, bytesToSkip);
    }

}
