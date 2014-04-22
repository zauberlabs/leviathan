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

import org.apache.commons.lang.Validate;
import org.springframework.web.util.UriTemplate;

import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Predicado que  retorna true si la URI de un {@link URIFetcherResponse} 
 * matchea con un {@link UriTemplate}.
 * 
 * @author Juan F. Codagnone
 * @since Jan 22, 2010
 */
public class MatchesURIFetcherResponsePredicate 
   implements Predicate<URIFetcherResponse> {
    private final UriTemplate template;
    
    /** Creates the MatchesURIFetcherResponsePredicate. */
    public MatchesURIFetcherResponsePredicate(final UriTemplate template) {
        Validate.notNull(template);
        
        this.template = template;
    }
    
    /** @see Predicate#evaluate(Object) */
    public final boolean evaluate(final URIFetcherResponse value) {
        boolean ret = false;
        if(value != null) {
            ret = template.matches(value.getURIAndCtx().getURI().toString());
        }
        
        return ret;
    }
}
