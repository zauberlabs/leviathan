/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.scrapper.utils;

import org.apache.commons.lang.Validate;

/**
 * Clase que engloba una respuesta y un contexto que generó dicha respuesta para
 * poder pasar esa respuesta en una cadena de proceso de manera que tenga todo
 * para poder interpretarlo.
 * 
 * @param <C> La clase que tiene el contexto de la respuesta.
 * @param <R> La respuesta en si.
 *  
 * @author Marcelo Turrin
 * @since Sep 9, 2010
 */
public class ContextualResponse<C, R> {
    private final C context;
    private final R response;

    /**
     * Creates the ContextualResponse.
     * 
     * @param context
     * @param response
     */
    public ContextualResponse(final C context, final R response) {
        super();

        Validate.notNull(context);
        Validate.notNull(response);

        this.context = context;
        this.response = response;
    }

    /** retorna el contexto de la respuesta */
    public final C getContext() {
        return context;
    }

    /** la respuesta en si */
    public final R getResponse() {
        return response;
    }

}
