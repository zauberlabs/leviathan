/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

import javax.xml.transform.Source;
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
    AfterXMLTransformer transformXML(Transformer xsl);
    
}
