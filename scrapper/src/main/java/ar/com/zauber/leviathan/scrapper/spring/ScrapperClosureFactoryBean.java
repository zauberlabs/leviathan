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
package ar.com.zauber.leviathan.scrapper.spring;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.util.UriTemplate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.commons.dao.closure.ClosureWrapperFactory;
import ar.com.zauber.commons.dao.closure.ErrorLoggerWrapperClosure;
import ar.com.zauber.commons.dao.closure.PredicateClosureEntry;
import ar.com.zauber.commons.dao.closure.SwitchClosure;
import ar.com.zauber.commons.dao.closure.wrapper.NullClosureWrapperFactory;
import ar.com.zauber.commons.dao.predicate.TruePredicate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.scrapper.utils.MatchesURIFetcherResponsePredicate;

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
    private final ClosureWrapperFactory<URIFetcherResponse> wrapperFactory;
    
    /**
     * Creates the ScrapperClosureFactoryBean.
     * 
     * @param caseblocks
     *            los caseblocks que indican para cada {@link UriTemplate} que
     *            {@link Closure} se aplica
     */
    public ScrapperClosureFactoryBean(
            final Map<String, Closure<URIFetcherResponse>> caseblocks,
            final ClosureWrapperFactory<URIFetcherResponse> aWrapperFactory) {
        
        Validate.notNull(caseblocks);
        Validate.notNull(aWrapperFactory);
        
        this.wrapperFactory = aWrapperFactory;
        final List<Entry<Predicate<URIFetcherResponse>, 
                         Closure<URIFetcherResponse>>> blocks = 
                    new LinkedList<Entry<Predicate<URIFetcherResponse>, 
                                        Closure<URIFetcherResponse>>>();
        
        for(final Entry<String, Closure<URIFetcherResponse>> caseblock : caseblocks
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
        
        closure = wrapperFactory.decorate(new SwitchClosure<URIFetcherResponse>(
                        blocks));
    }
    
    /**
     * Creates the ScrapperClosureFactoryBean. Uses as wrapper a 
     * {@link NullClosureWrapperFactory}
     *@param caseblocks
     *            los caseblocks que indican para cada {@link UriTemplate} que
     *            {@link Closure} se aplica
     */
    public ScrapperClosureFactoryBean(
            final Map<String, Closure<URIFetcherResponse>> caseblocks) {
        this(caseblocks, new NullClosureWrapperFactory<URIFetcherResponse>());
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
