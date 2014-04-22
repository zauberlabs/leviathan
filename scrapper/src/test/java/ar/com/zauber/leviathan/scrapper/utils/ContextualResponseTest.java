/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
