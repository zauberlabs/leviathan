/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.mock;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Transformer;

/**
 * Extrae un argumento
 * 
 * 
 * @author Juan F. Codagnone
 * @since Sep 22, 2010
 */
public class QueryParamURITransformer implements Transformer<URI, String> {
    private final String param;
    
    public QueryParamURITransformer(final String param) {
        Validate.isTrue(StringUtils.isNotBlank(param));
        this.param = param;
    }
    @Override
    public final String transform(final URI input) {
        final String query = input.getQuery();
        String ret = null;
        
        if(query !=  null) {
            for(final String s : query.split("&")) {
                final String []para = s.split("=");
                if(para.length == 2) {
                    if(param.equals(para[0])) {
                        ret = para[1];
                        break;
                    }
                }
            }
        }
        return ret;
    }

}
