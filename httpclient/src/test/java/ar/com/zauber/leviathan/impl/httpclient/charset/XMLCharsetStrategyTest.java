/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.impl.httpclient.InmutableResponseMetadata;

/**
 * Tests para {@link XMLCharsetStrategy}
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 9, 2010
 */
public class XMLCharsetStrategyTest {
    
    /** */
    @Test
    public final void aplicarEstrategiaTest() throws Exception {
        final CharsetStrategy st = new XMLCharsetStrategy();
        final URI uri = new URI("http://example.com");
        final InputStream is = XMLCharsetStrategy.class.getClassLoader()
                .getResourceAsStream(
                        "ar/com/zauber/leviathan/impl/mock/utf8.xml");
        final InmutableResponseMetadata meta =
            new InmutableResponseMetadata(uri, "text/xml", null, 200);
        final Charset cs1 = st.getCharset(meta, is);
        Assert.assertEquals(Charset.forName("utf-8"), cs1);
    }    

}
