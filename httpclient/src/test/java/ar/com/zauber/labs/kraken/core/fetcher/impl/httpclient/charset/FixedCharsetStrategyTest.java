/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
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
