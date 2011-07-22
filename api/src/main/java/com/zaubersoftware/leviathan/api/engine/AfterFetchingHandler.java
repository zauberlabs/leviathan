/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
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

}
