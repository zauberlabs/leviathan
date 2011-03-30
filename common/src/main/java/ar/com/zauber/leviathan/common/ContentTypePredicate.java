/**
 * Copyright (c) 2009-2011 Zauber S.A. <http://www.zaubersoftware.com/>
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
package ar.com.zauber.leviathan.common;

import java.util.List;

import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.commons.validate.Validate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * A <code>{@link Predicate}</code> that checks for
 * accepted media types in the HTTP Response Header.
 *
 * @author Guido Marucci Blas
 * @since Feb 9, 2011
 */
public class ContentTypePredicate implements Predicate<URIFetcherResponse> {
   
    private final List<String> acceptedMediaTypes;
    private final boolean acceptNoContentType;


    /**
     * Creates the ContentTypePredicate.
     *
     * @param acceptedMediaTypes a list of accepted media types.
     * @param acceptNoContentType flag to check whether to accept responses 
     * that have no content type.
     */
    public ContentTypePredicate(final List<String> acceptedMediaTypes, 
                                final boolean acceptNoContentType) {
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
                if(mediaType.toLowerCase().startsWith(m.toLowerCase())) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

}
