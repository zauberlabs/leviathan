/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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
package ar.com.zauber.leviathan.scrapper.transformation;

import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.apache.commons.lang.UnhandledException;
import org.junit.Test;
import org.mockito.Mockito;

import ar.com.zauber.commons.web.transformation.schema.SchemaProvider;
import ar.com.zauber.leviathan.scrapper.utils.ContextualResponse;

/**
 * Test de unidad del {@link XMLUnmarshallTransformer} que fija el
 * comportamiento del mismo.
 * 
 * @author Marcelo Turrin
 * @since Sep 10, 2010
 */
public class XMLUnmarshallTransformerTest {

    /**
     * Test del comportamiento base
     * 
     * @throws JAXBException no debe pasar, se pone por que es checked
     * @throws URISyntaxException  no debe pasar, se pone por que es checked
     */
    @Test
    public final void testBehaviour() throws JAXBException, URISyntaxException {
        final String rta = "HOLA";
        Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        Mockito.when(unmarshaller.unmarshal(Mockito.any(Reader.class)))
                .thenReturn(rta);
        final SchemaProvider provider = Mockito.mock(SchemaProvider.class);
        Mockito.when(provider.getUnmarshaller()).thenReturn(unmarshaller);
        
        final XMLUnmarshallTransformer<String,  String> transformer 
            = new XMLUnmarshallTransformer<String, String>(provider);

        final String context = "http://zaubersoftware.com";
        
        final ContextualResponse<String, Reader> input 
            = new ContextualResponse<String, Reader>(
                context, new StringReader("respuesta que no importa"));
        

        final ContextualResponse<String, String> respuesta 
                = transformer.transform(input);
        
        Assert.assertEquals(rta, respuesta.getResponse());
        Assert.assertEquals(context, respuesta.getContext());
        
        Mockito.verify(provider).getUnmarshaller();
        Mockito.verify(unmarshaller).unmarshal(Mockito.any(Reader.class));
    }
    
    /** El transformer no puede recibir argumento nulo */
    @Test(expected = IllegalArgumentException.class)
    public final void testNullSchema() {
        new XMLUnmarshallTransformer<Object, Object>(null);
    }

    /**
     * Simula que el unmarshall tire una excepcion
     * 
     * @throws JAXBException
     *             no debe pasar, se pone por que es checked
     * @throws URISyntaxException
     *             no debe pasar, se pone por que es checked
     */
    @Test(expected = UnhandledException.class)
    public final void testUnMarshalException() throws JAXBException,
            URISyntaxException {
        Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        Mockito.when(unmarshaller.unmarshal(Mockito.any(Reader.class)))
                .thenThrow(new JAXBException("No se pudo mockear"));
        final SchemaProvider provider = Mockito.mock(SchemaProvider.class);
        Mockito.when(provider.getUnmarshaller()).thenReturn(unmarshaller);
      
        final XMLUnmarshallTransformer<String, String> transformer 
            = new XMLUnmarshallTransformer<String, String>(provider);
        
        final ContextualResponse<String, Reader> input 
        = new ContextualResponse<String, Reader>(
            "contexto", new StringReader("respuesta que no importa"));

        transformer.transform(input);
    }
}
