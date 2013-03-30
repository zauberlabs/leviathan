/**
 * Copyright (c) 2009-2013 Zauber S.A. <http://zauberlabs.com/>
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
