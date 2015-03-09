/**
 * Copyright (c) 2009-2015 Zauber S.A. <http://zauberlabs.com/>
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

import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.xml.sax.InputSource;

import ar.com.zauber.commons.dao.Transformer;
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

    private final Unmarshaller provider;

    /** a partir de un provider crea el transformador */
    public XMLUnmarshallTransformer(final Unmarshaller provider) {
        super();
        Validate.notNull(provider);
        this.provider = provider;
    }

    /** @see Transformer#transform(Object) */
    @Override
    public final ContextualResponse<C, T> transform(
            final ContextualResponse<C, Reader> input) {
        Reader reader = null;
        try {
            return new ContextualResponse<C, T>(
                    input.getContext(),
                    (T) provider.unmarshal(new StreamSource(input.getResponse())));
        } catch (final XmlMappingException e) {
            throw new UnhandledException("Tranforming " + input.getContext(), e);
        } catch (final IOException e) {
            throw new UnhandledException("Tranforming " + input.getContext(), e);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new UnhandledException("Closing reader for" 
                            + input.getContext(), e);
                }
            }
        }
    }

}