/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.charset;

import java.net.URI;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.InmutableResponseMetadata;
import ar.com.zauber.labs.kraken.fetcher.common.CharsetStrategy;
import ar.com.zauber.labs.kraken.fetcher.common.ResponseMetadata;


/**
 * Test de {@link DefaultHttpCharsetStrategy}
 *
 * @author Mariano Semelman
 * @since Dec 17, 2009
 */
public class DefaultHttpCharsetStrategyTest {

    /** */
    @Test
    public final void constructorTest() throws Exception {
        final CharsetStrategy st = new FixedCharsetStrategy("utf-8");
        new DefaultHttpCharsetStrategy(st);
    }

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
        Assert.assertEquals(Charset.defaultCharset(), cs2);

    }

}
