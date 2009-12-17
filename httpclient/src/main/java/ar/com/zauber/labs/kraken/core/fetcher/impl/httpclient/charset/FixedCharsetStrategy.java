/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl.httpclient.charset;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.lang.Validate;

import ar.com.zauber.labs.kraken.fetcher.common.CharsetStrategy;
import ar.com.zauber.labs.kraken.fetcher.common.ResponseMetadata;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 *
 *
 * @author Mariano Semelman
 * @since Dec 15, 2009
 */
public class FixedCharsetStrategy implements CharsetStrategy {


    private final Charset charset;

    /** un strategy que tiene como estrategy elegir utf-8 como encoding */
    public FixedCharsetStrategy() {
        this("utf-8");
    }

    /** constructor  falla si el charset no corresponde.*/
    public FixedCharsetStrategy(final String charset)
        throws UnsupportedCharsetException {
        this.charset = Charset.forName(charset);
    }

    /** @see CharsetStrategy#getCharset(ResponseMetadata, byte[]) */
    public final Charset getCharset(final ResponseMetadata meta,
            final InputStream documento) {
        Validate.notNull(meta);
        return this.charset;
    }

}
