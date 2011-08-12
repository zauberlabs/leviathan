/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * A {@link Pipe} that applies an XSL transformation
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class XMLPipe implements Pipe<Node, Node> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Object> extraModel;
    private final String encoding;
    private final Source xsltSource;
    
    /**
     * Creates the XMLPipe.
     *
     * @param scrapper
     */
    public XMLPipe(
            final Source xsltSource, 
            final Map<String, Object> extraModel, 
            final String encoding) {
        Validate.notNull(xsltSource, "The xslt source cannot be null");
        this.extraModel = (extraModel == null) ? new HashMap<String, Object>() : extraModel;
        this.encoding = encoding;
        this.xsltSource = xsltSource;
    }

    /**
     * Creates the XMLPipe.
     *
     * @param scraper
     * @param extraModel
     * @param xsltSource
     */
    public XMLPipe(final Source xsltSource, final Map<String, Object> extraModel) {
        this(xsltSource, extraModel, null);
    }

    /**
     * Creates the XMLPipe.
     *
     * @param scraper
     * @param encodig
     */
    public XMLPipe(final Source xsltSource, final String encodig) {
        this(xsltSource, null, null);
    }
    
    /**
     * Creates the XMLPipe.
     *
     * @param scraper
     */
    public XMLPipe(final Source xsltSource) {
        this(xsltSource, null, null);
    }

    @Override
    public Node execute(final Node input) {
        Validate.notNull(input, "The input XML document cannot be null");
        
        final Map<String, Object> model = extraModel;
        // TODO Retrieve model from current context in local thread and merge it with extra model.
        logger.warn("TODO Retrieve model from current context in local thread and merge it with extra model");
        return applyXSLT(input, model, null);
    }

    /**
     * Applies the XSL Transformation and returns the resulting Node.
     * 
     * @param node
     * @param model
     * @param oformat
     * @return
     * @throws TransformerFactoryConfigurationError
     */
    private Node applyXSLT(
            final Node node, 
            final Map<String, Object> model,
            final Properties oformat) {
        Validate.notNull(node, "The node cannot be null.");
        // NOTE: This code was extracted from ar.com.zauber.leviathan.scrapper.transformation.XSLTTransformer
        
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Transformer transformer = factory.newTransformer(xsltSource);
            Validate.notNull(transformer);
            for(final Entry<String, Object> entry : model.entrySet()) {
                transformer.setParameter(entry.getKey(), entry.getValue());
            }
            Properties options;
            if(oformat != null) {
                options = new Properties(oformat);
            } else {
                options = new Properties();
            }
            if(encoding != null) {
                options.setProperty(OutputKeys.ENCODING, encoding);
            }
            transformer.setOutputProperties(options);
            final DOMResult result = new DOMResult();
            transformer.transform(new DOMSource(node), result);
            return result.getNode();
        } catch (final TransformerException e) {
            logger.error("An error ocurred while applying the XSL transformation", e);
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers");
        } catch (final TransformerFactoryConfigurationError e) {
            logger.error("An error ocurred while applying the XSL transformation", e);
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers");
        }
    }
}
