/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import java.util.List;

import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.commons.validate.Validate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * TODO: Description of the class, Comments in english by default
 *
 *
 * @author Guido Marucci Blas
 * @since Feb 9, 2011
 */
public class ContentTypePredicate implements Predicate<URIFetcherResponse> {
    final List<String> acceptedMediaTypes;
    final boolean acceptNoContentType;


    public ContentTypePredicate(final List<String> acceptedMediaTypes, final boolean acceptNoContentType) {
        Validate.notNull(acceptedMediaTypes);
        
        this.acceptedMediaTypes = acceptedMediaTypes;
        this.acceptNoContentType = acceptNoContentType;
    }


    @Override
    public final boolean evaluate(final URIFetcherResponse value) {
        final String mediaType = value.getHttpResponse().getHeader("Content-Type");

        boolean ret = false;
        
        if(mediaType == null) {
            ret = acceptNoContentType;
        } else {
            for(final String m : acceptedMediaTypes) {
                if(mediaType.startsWith(m)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

}
