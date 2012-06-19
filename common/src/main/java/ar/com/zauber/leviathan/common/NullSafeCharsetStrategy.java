/*
 * Copyright (c) 2012 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang.Validate;

/**
 * {@link CharsetStrategy} that has a default value. If the decorated
 * {@link CharsetStrategy} returns null, it coalesces to the given default
 * {@link Charset}
 * 
 * @author flbulgarelli
 * @since Jun 19, 2012
 */
public final class NullSafeCharsetStrategy implements CharsetStrategy {

    private final CharsetStrategy charsetStrategy;
    private final Charset defaultCharset;

    /**
     * Creates the NullSafeCharsetStrategy.
     *
     * @param charsetStrategy
     * @param defaultCharset
     */
    public NullSafeCharsetStrategy(final CharsetStrategy charsetStrategy, final Charset defaultCharset) {
        Validate.notNull(charsetStrategy);
        Validate.notNull(defaultCharset);
        this.charsetStrategy = charsetStrategy;
        this.defaultCharset = defaultCharset;
    }

    @Override
    public Charset getCharset(final ResponseMetadata meta, final InputStream content) {
        Charset charset = charsetStrategy.getCharset(meta, content);
        return charset != null ? charset : defaultCharset;
    }

}
