/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.impl.httpclient.InmutableResponseMetadata;

/**
 * Tests para {@link ChainedCharsetStrategy}
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 9, 2010
 */
public class ChainedCharsetStrategyTest {

    /** */
    @Test
    public final void aplicarEstrategiaTest() throws Exception {
        final List<CharsetStrategy> strategies = new ArrayList<CharsetStrategy>();

        // si es xml, leo el header
        strategies.add(new XMLCharsetStrategy());
        // sino, leo de meta
        strategies.add(new DefaultHttpCharsetStrategy());
        // sino, fallback
        strategies.add(new FixedCharsetStrategy("utf-8"));

        final CharsetStrategy st = new ChainedCharsetStrategy(strategies);

        // XML con encoding
        final URI uri = new URI("http://example.com");
        final InputStream is = XMLCharsetStrategy.class.getClassLoader()
                .getResourceAsStream(
                        "ar/com/zauber/leviathan/impl/mock/utf8.xml");
        final InmutableResponseMetadata meta = new InmutableResponseMetadata(
                uri, "text/xml", "ISO-8859-1", 200);
        final Charset cs1 = st.getCharset(meta, is);
        Assert.assertEquals(Charset.forName("utf-8"), cs1);

        // XML sin encoding
        final InputStream is2 = XMLCharsetStrategy.class.getClassLoader()
                .getResourceAsStream(
                        "ar/com/zauber/leviathan/impl/mock/noencoding.xml");
        final Charset cs2 = st.getCharset(meta, is2);
        Assert.assertEquals(Charset.forName("ISO-8859-1"), cs2);
        
        // no XML
        final InmutableResponseMetadata meta2 = new InmutableResponseMetadata(
                uri, "text/html", null, 200);
        final Charset cs3 = st.getCharset(meta2, null);
        Assert.assertEquals(Charset.forName("utf-8"), cs3);
    }
}
