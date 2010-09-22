/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.mock;

import java.net.URI;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Transformer;

/**
 * Util para mapear request a archivos de un directorio
 * 
 * @author Juan F. Codagnone
 * @since Sep 20, 2010
 */
public class PrefixMockURIFetcher extends AbstractMockUriFetcher {
    private final Transformer<URI, String> transformer;

    /** Creates the FixedContentProvider. */
    public PrefixMockURIFetcher(final Transformer<URI, String> transformer) {
        this(transformer, Charset.defaultCharset());
    }
    

    /** Creates the FixedContentProvider. */
    public PrefixMockURIFetcher(final Transformer<URI, String> transformer,
                                final Charset charset) {
        super(charset);
        Validate.notNull(transformer);

        this.transformer = transformer;
    }

    @Override
    protected final String getDestinationURL(final URI uri) {
        return transformer.transform(uri);
    }
}
