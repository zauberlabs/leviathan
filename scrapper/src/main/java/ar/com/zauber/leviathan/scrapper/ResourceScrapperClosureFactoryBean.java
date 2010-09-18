/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ar.com.zauber.leviathan.scrapper;

import java.io.Reader;

import org.springframework.beans.factory.FactoryBean;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.dao.Transformer;
import ar.com.zauber.commons.dao.closure.TargetTransformerClosure;
import ar.com.zauber.commons.web.transformation.processors.impl.TidyScrapper;
import ar.com.zauber.commons.web.transformation.schema.SchemaProvider;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.scrapper.closure.URIFetcherResponseWrapperClosure;
import ar.com.zauber.leviathan.scrapper.transformation.XMLUnmarshallTransformer;
import ar.com.zauber.leviathan.scrapper.transformation.XSLTTransformer;
import ar.com.zauber.leviathan.scrapper.utils.ContextualResponse;

/**
 * {@link FactoryBean} que genera un {@link Closure} que permite escrapear la
 * funcionalidad normal del sistema, armando la cadena de {@link Transformer} y
 * {@link Closure} mediante a {@link TargetTransformerClosure} Arma la cadena
 * mediante a la transformacion XSLT (por el {@link TidyScrapper}) si es
 * necesario agrega la Transformacion {@link XMLUnmarshallTransformer}
 * 
 * @author Marcelo Turrin
 * @since Feb 1, 2010
 */
public class ResourceScrapperClosureFactoryBean implements
        FactoryBean<Closure<URIFetcherResponse>> {

    private final TidyScrapper scrapper;
    private final SchemaProvider schemaProvider;
    private final Closure<?> closure;

    /**
     * Creates the ResourceScrapperClosureFactoryBean.
     * 
     * @param scrapper
     *            el scrapper a aplicar para la transformaci�n xslt
     * @param schemaProvider
     *            esquema para indicar si debe "unmarshalizar" si esta se hace
     *            si no se llama directo al closure, post transformaci�n
     * @param closure
     *            el closure final que se aplica en el scrapper
     */
    public ResourceScrapperClosureFactoryBean(final TidyScrapper scrapper,
            final SchemaProvider schemaProvider, final Closure<?> closure) {
        super();
        this.scrapper = scrapper;
        this.schemaProvider = schemaProvider;
        this.closure = closure;
    }

    /** @see FactoryBean#getObject() */
    @SuppressWarnings("unchecked")
    public final Closure<URIFetcherResponse> getObject() {

        Closure<?> finalClosure = closure;

        if (schemaProvider != null) {
            finalClosure = new TargetTransformerClosure<
                                        ContextualResponse<URIAndCtx, Reader>, 
                                        ContextualResponse<URIAndCtx, Object>>(
                    new XMLUnmarshallTransformer<URIAndCtx, Object>(
                            schemaProvider),
                    (Closure<ContextualResponse<URIAndCtx, Object>>) finalClosure);
        }

        return new URIFetcherResponseWrapperClosure(new TargetTransformerClosure<
                                            ContextualResponse<URIAndCtx, Reader>, 
                                            ContextualResponse<URIAndCtx, Reader>>
                (new XSLTTransformer(scrapper),
                (Closure<ContextualResponse<URIAndCtx, Reader>>) finalClosure));
    }

    /** @see FactoryBean#getObjectType() */
    @SuppressWarnings("unchecked")
    public final Class<? extends Closure<URIFetcherResponse>> getObjectType() {
        return (Class<? extends Closure<URIFetcherResponse>>) closure
                .getClass();
    }

    /** @see FactoryBean#isSingleton() */
    public final boolean isSingleton() {
        return true;
    }
}