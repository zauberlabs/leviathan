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
package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.net.URI;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;
import ar.com.zauber.leviathan.impl.httpclient.InmutableResponseMetadata;
import ar.com.zauber.leviathan.impl.httpclient.charset.DefaultHttpCharsetStrategy;


/**
 * Test de {@link DefaultHttpCharsetStrategy}
 *
 * @author Mariano Semelman
 * @since Dec 17, 2009
 */
public class DefaultHttpCharsetStrategyTest {

    /** */
    @Test
    public final void aplicarEstrategiaTest() throws Exception {
        final CharsetStrategy st = new DefaultHttpCharsetStrategy();
        final URI uri = new URI("http://example.com");
        final ResponseMetadata meta =
            new InmutableResponseMetadata(uri , null, "utf-8", 200);
        final InmutableResponseMetadata meta2 =
            new InmutableResponseMetadata(uri, null, null, 200);
        final Charset cs1 = st.getCharset(meta, null);
        final Charset cs2 = st.getCharset(meta2, null);
        Assert.assertEquals(Charset.forName("utf-8"), cs1);
        Assert.assertEquals(null, cs2);
    }

}
