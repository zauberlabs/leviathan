/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class ToJavaObjectPipe<T> implements Pipe<Node, T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JAXBContext newInstance;

    /** Creates the ToJavaObjectPipe  */
    public ToJavaObjectPipe(final Class<T> classToBeBounded) throws JAXBException {
        Validate.notNull(classToBeBounded);
        
        newInstance = JAXBContext.newInstance(classToBeBounded);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T execute(final Node input) {
        try {
            // TODO falta agregar la configuración de schemas etc
            final Unmarshaller u = newInstance.createUnmarshaller();
            return (T) u.unmarshal(input);
        } catch (JAXBException e) {
            logger.error("An error ocurred while applying the XSL transformation", e);
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers");
        }
    }

}
