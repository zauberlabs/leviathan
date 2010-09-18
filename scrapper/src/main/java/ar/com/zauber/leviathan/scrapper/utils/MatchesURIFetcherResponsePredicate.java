/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
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
