/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;

/**
 * {@link CharsetStrategy} que delega a una lista de {@link CharsetStrategy}'s.
 * Devuelve el primer Charset obtenido.
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 9, 2010
 */
public class ChainedCharsetStrategy implements CharsetStrategy {
    
    private final List<CharsetStrategy> strategies;

    /** Creates the ChainedCharsetStrategy. */
    public ChainedCharsetStrategy(final List<CharsetStrategy> strategies) {
        Validate.notNull(strategies);
        this.strategies = strategies;
    }

    /** @see CharsetStrategy#getCharset(ResponseMetadata, InputStream) */
    public final Charset getCharset(final ResponseMetadata meta, 
            final InputStream content) {
        for (CharsetStrategy strategy : strategies) {
            Charset charset = strategy.getCharset(meta, content);
            if (charset != null) {
                return charset;
            }
        }
        return null;
    }

}
