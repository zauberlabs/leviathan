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
/*
 * Copyright (c) 2010 TCPN -- All rights reserved
 */

package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.impl.httpclient.InmutableResponseMetadata;


/**
 * Test para {@link DetectorCharsetStrategy}
 * 
 * @author Mariano Focaraccio
 * @since Sep 16, 2010
 */

public class DetectorCharsetStrategyTest {

    /**Test para aplicar estrategia de detección de encoding */
    @Test
    public final void aplicarEstrategiaTest() throws Exception {
        final DetectorCharsetStrategy detector = new DetectorCharsetStrategy();
        final URI uri = new URI("http://example.com");
        final InputStream is = DetectorCharsetStrategy.class.getClassLoader()
                .getResourceAsStream(
                        "ar/com/zauber/leviathan/impl/mock/decoder.xml");
        final InmutableResponseMetadata meta =
            new InmutableResponseMetadata(uri, "text/xml", null, 200, null);
        final Charset cs1 = detector.getCharset(meta, is);
        Assert.assertNotNull(cs1);
    }
}

