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
package ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.charset;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.InmutableResponseMetadata;
import ar.com.zauber.labs.kraken.fetcher.common.CharsetStrategy;


/**
 * Test de FixedCharsetStrategy
 *
 * @author Mariano Semelman
 * @since Dec 17, 2009
 */
public class FixedCharsetStrategyTest {

    /** */
    @Test
    public final void wrongConstructor() throws Exception {
        try {
            CharsetStrategy strategy = new FixedCharsetStrategy("mimamamemima");
            Assert.fail("no fallo cuando tiene un charset desconocido");
        } catch (UnsupportedCharsetException e) {
            // must be empty.
        }

    }

    /** test del fixed strategy */
    @Test
    public final void fixedTest() throws Exception {
        CharsetStrategy strategy = new FixedCharsetStrategy("utf-8");
        URI uri = new URI("htt://foo.bar");
        InputStream content = null;
        Charset cs =
            strategy.getCharset(new InmutableResponseMetadata(uri, null, null, 200),
                content);
        Assert.assertEquals(Charset.forName("utf-8"), cs);
    }





}
