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
package com.zaubersoftware.leviathan.api.engine;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;

/**
 * Interface that defines all the available actions that can be configured for an XML resource 
 * that was successfully fetched or is the result of a transformation of a resource into an XML document.
 * 
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface AfterXMLTransformer extends JavaObjectTransformer {
    
    /**
     * Applies an XSL transformation over the XML document
     * 
     * @param xsl The path to the XSL Transformation file. Must not be <code>null</code> or empty.
     * @return An {@link AfterXMLTransformer} interface that publishes all the actions that can be performed
     * after an XML Transformation.
     */
    AfterXMLTransformer transformXML(String xsl);
    
    /**
     * Applies an XSL transformation over the XML document
     * 
     * @param xsl The {@link Source} object for the XSL Transformation. Must not be <code>null</code>. 
     * @return An {@link AfterXMLTransformer} interface that publishes all the actions that can be performed
     * after an XML Transformation.
     */
    AfterXMLTransformer transformXML(Source xsl);

    /**
     * Applies an XSL transformation over the XML document
     * 
     * @param xsl The the XSL Transformation. Must not be <code>null</code>.
     * @return An {@link AfterXMLTransformer} interface that publishes all the actions that can be performed
     * after an XML Transformation.
     */
    AfterXMLTransformer transformXML(Templates xsl);
    
}
