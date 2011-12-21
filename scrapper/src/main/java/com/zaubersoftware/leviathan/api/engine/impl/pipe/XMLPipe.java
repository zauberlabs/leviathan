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
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.zaubersoftware.leviathan.api.engine.Pipe;

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
    private final Templates template;

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
        final TransformerFactory factory = TransformerFactory.newInstance();
        try {
            template = factory.newTemplates(xsltSource);
        } catch (final TransformerConfigurationException e) {
            throw new UnhandledException(e);
        }
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

    /**
     * Creates the XMLPipe.
     *
     * @param scraper
     */
    public XMLPipe(final Templates transformer) {
        this(transformer, null, null);
    }

    /**
     * Creates the XMLPipe.
     *
     * @param scraper
     */
    public XMLPipe(final Templates transformer, final String encoding, final Map<String, Object> extraModel) {
        this.extraModel = (extraModel == null) ? new HashMap<String, Object>() : extraModel;
        this.encoding = encoding;
        this.template = transformer; 
    }

    @Override
    public Node execute(final Node input) {
        Validate.notNull(input, "The input XML document cannot be null");

        final Map<String, Object> model = this.extraModel;
        // TODO Retrieve model from current context in local thread and merge it with extra model.
        this.logger.warn("TODO Retrieve model from current context in local thread and merge it with extra model");
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
            final Transformer transformer = template.newTransformer();
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
            if(this.encoding != null) {
                options.setProperty(OutputKeys.ENCODING, this.encoding);
            }
            transformer.setOutputProperties(options);
            final DOMResult result = new DOMResult();
            transformer.transform(new DOMSource(node), result);
            return result.getNode();
        } catch (final TransformerException e) {
            this.logger.error("An error ocurred while applying the XSL transformation", e);
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers");
        } catch (final TransformerFactoryConfigurationError e) {
            this.logger.error("An error ocurred while applying the XSL transformation", e);
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers");
        }
    }
}
