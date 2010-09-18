/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.scrapper.transformation;

import java.io.Reader;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Transformer;
import ar.com.zauber.commons.web.transformation.schema.SchemaProvider;
import ar.com.zauber.leviathan.scrapper.utils.ContextualResponse;

/**
 * {@link Transformer} que aplica para una entrada que representa un xml la
 * deserealizacion del mismo a objeto usando javax.xml
 * @param <T> el tipo de objeto de salida 
 * @param <C> el tipo de objeto del contexto, que no se toca si no que se pasa 
 * 
 * @author Marcelo Turrin
 * @since Sep 9, 2010
 */
@SuppressWarnings("unchecked")
public class XMLUnmarshallTransformer<C, T>
        implements Transformer<ContextualResponse<C, Reader>, 
                                ContextualResponse<C, T>> {

    private final SchemaProvider provider;

    /** a partir de un provider crea el transformador */
    public XMLUnmarshallTransformer(final SchemaProvider provider) {
        super();
        Validate.notNull(provider);
        this.provider = provider;
    }

    /** @see Transformer#transform(Object) */
    @Override
    public final ContextualResponse<C, T> transform(
            final ContextualResponse<C, Reader> input) {
        try {
            return new ContextualResponse<C, T>(
                    input.getContext(),
                    (T) provider.getUnmarshaller().unmarshal(input.getResponse()));
        } catch (JAXBException e) {
            throw new UnhandledException(e);
        }
    }

}