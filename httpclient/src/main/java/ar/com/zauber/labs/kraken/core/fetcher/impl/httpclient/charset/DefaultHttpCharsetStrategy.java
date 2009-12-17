/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.charset;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.common.CharsetStrategy;
import ar.com.zauber.labs.kraken.fetcher.common.ResponseMetadata;

/**
 * Estrategia estandar para tratar de obtener la codificacion de un http response.
 *
 * @author Mariano Semelman
 * @since Dec 15, 2009
 */
public class DefaultHttpCharsetStrategy implements CharsetStrategy {

    private final CharsetStrategy strategy;

    /** Creates the DefaultHttpCharsetStrategy.
     *  */
    public DefaultHttpCharsetStrategy() {
        this(new FixedCharsetStrategy(Charset.defaultCharset().displayName()));
    }

    /** Creates the DefaultHttpCharsetStrategy.
     * @param st una estrategia de fallback
     *  */
    public DefaultHttpCharsetStrategy(final CharsetStrategy st) {
        Validate.notNull(st);

        this.strategy = st;
    }

    /** @see CharsetStrategy#getCharset(ResponseMetadata, byte[]) */
    public final Charset getCharset(final ResponseMetadata meta,
            final InputStream documento) {
        Validate.notNull(meta);
        Charset res;
        if(meta.getEncoding() == null) {
            res = this.strategy.getCharset(meta, documento);
        } else {
            res = Charset.forName(meta.getEncoding());
        }
        return res;
    }

}
