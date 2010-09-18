/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.scrapper.transformation;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Transformer;
import ar.com.zauber.commons.web.transformation.processors.impl.TidyScrapper;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.scrapper.utils.ContextualResponse;

/**
 * Transformación que aplica a un {@link URIFetcherResponse} una transformación
 * XSL dada y devuelve una {@link ContextualResponse} con la respuesta y el
 * {@link URIAndCtx} de contexto
 * 
 * @author Marcelo Turrin
 * @since Sep 9, 2010
 */
public class XSLTTransformer 
    implements Transformer<ContextualResponse<URIAndCtx, Reader>, 
                            ContextualResponse<URIAndCtx, Reader>> {

    private final TidyScrapper tidyScrapper;

    /**
     * Creates the XSLTTransformer.
     * 
     * @param tidyScrapper
     */
    public XSLTTransformer(final TidyScrapper tidyScrapper) {
        super();
        Validate.notNull(tidyScrapper);

        this.tidyScrapper = tidyScrapper;
    }

    /** @see Transformer#transform(java.lang.Object) */
    public final ContextualResponse<URIAndCtx, Reader> transform(
            final ContextualResponse<URIAndCtx, Reader> input) {
        Validate.notNull(input);
        final StringWriter sw = new StringWriter();
            tidyScrapper.scrap(input.getResponse(), sw, input
                    .getContext().getCtx());
        
        return new ContextualResponse<URIAndCtx, Reader>(input.getContext(),
                new StringReader(sw.toString()));
    }
}
