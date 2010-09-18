/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.scrapper.utils;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test de unidad {@link ContextualResponse}
 * 
 * 
 * @author Marcelo Turrin
 * @since Sep 10, 2010
 */
public class ContextualResponseTest {

    /** Testeo de comportamiento, construir y obtener */
    @Test
    public final void testBehaviour() {
        final String context = "contexto";
        final String response = "respuesta";
        ContextualResponse<String, String> contextualResponse 
                = new ContextualResponse<String, String>(context, response);
        
        Assert.assertEquals(context, contextualResponse.getContext());
        Assert.assertEquals(response, contextualResponse.getResponse());
    }
    
    /** El contexto no puede ser null */
    @Test(expected = IllegalArgumentException.class)
    public final void testNullContext() {
        new ContextualResponse<String, String>(null, "respuesta");
    }

    /** La respuesta no puede ser null */
    @Test(expected = IllegalArgumentException.class)
    public final void testNullResponse() {
        new ContextualResponse<String, String>("contexto", null);
    }

}
