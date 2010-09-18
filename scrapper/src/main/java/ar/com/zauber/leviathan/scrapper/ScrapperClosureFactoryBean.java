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
package ar.com.zauber.leviathan.scrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.util.UriTemplate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.commons.dao.closure.PredicateClosureEntry;
import ar.com.zauber.commons.dao.closure.SwitchClosure;
import ar.com.zauber.commons.dao.predicate.TruePredicate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Arma el closure que dada la respuesta del fetcher elije que scrapper usar.
 * Esto esta hecho en factory bean para que sea mas facil de mantener la
 * configuracion xml. Podria ser un custom-tag.
 * 
 * @author Juan F. Codagnone
 * @since Jan 22, 2010
 */
public class ScrapperClosureFactoryBean implements
        FactoryBean<Closure<URIFetcherResponse>> {
    private final Closure<URIFetcherResponse> closure;

    /**
     * Creates the ScrapperClosureFactoryBean.
     * 
     * @param caseblocks
     *            los caseblocks que indican para cada {@link UriTemplate} que
     *            {@link Closure} se aplica
     * @param dryrun flag para decidir si se hace commit o no
     */
    public ScrapperClosureFactoryBean(
            final Map<String, Closure<URIFetcherResponse>> caseblocks, 
            final boolean dryrun) {
        
        Validate.notNull(caseblocks);

        final List<Entry<Predicate<URIFetcherResponse>, 
                            Closure<URIFetcherResponse>>> blocks = 
                                new LinkedList<Entry<Predicate<URIFetcherResponse>, 
                                                Closure<URIFetcherResponse>>>();
        
        for (final Entry<String, Closure<URIFetcherResponse>> caseblock : caseblocks
                .entrySet()) {
            Validate.notNull(caseblock);
            Validate.notNull(caseblock.getKey());
            Validate.notNull(caseblock.getValue());

            blocks.add(new PredicateClosureEntry<URIFetcherResponse>(
                    new MatchesURIFetcherResponsePredicate(new UriTemplate(
                            caseblock.getKey())), caseblock.getValue()));
        }
        
        blocks.add(new PredicateClosureEntry<URIFetcherResponse>(
                new TruePredicate<URIFetcherResponse>(),
                new Closure<URIFetcherResponse>() {

                    public void execute(final URIFetcherResponse t) {
                        throw new IllegalArgumentException(
                                "No existe transformación registrada para: "
                                        + t.getURIAndCtx().getURI().toString());
                    }
                }));
        
        closure = new SwitchClosure<URIFetcherResponse>(blocks);
    }

    /** @see FactoryBean#getObject() */
    public final Closure<URIFetcherResponse> getObject() throws Exception {
        return closure;
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
