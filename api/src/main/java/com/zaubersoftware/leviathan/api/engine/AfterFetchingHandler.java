/**
 * Copyright (c) 2009-2012 Zauber S.A. <http://zauberlabs.com/>
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
package com.zaubersoftware.leviathan.api.engine;

import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Mixin interface that defines all the available actions that can be configured after a resource 
 * has been successfully fetched.
 * 
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface AfterFetchingHandler extends AfterXMLTransformer, 
    ErrorTolerantActionHandler<URIFetcherResponse>, JavaObjectTransformer {

    /**
     * Sanitize the resulting HTML document converting it in a valid XML document and allows to configure
     * what are the next actions to be done once the XML transformation was done.
     * 
     * @return An {@link AfterXMLTransformer} interface that publishes all the actions that can be performed
     * after an XML Transformation.
     * @throws IllegalStateException if the fetched resource is not an HTML document.
     */
    AfterXMLTransformer sanitizeHTML();
    
    /**
     * The request body is an XML 
     */
    AfterXMLTransformer isXML();

}
