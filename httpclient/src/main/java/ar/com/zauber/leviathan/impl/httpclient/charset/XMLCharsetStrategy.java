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
package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;

/**
 * {@link CharsetStrategy} para XMLs. Obtiene el charset a partir del header del
 * XML. Por defecto soporta los tipos declarados en {@link #xmlTypes}. Esta 
 * lista puede ser inyectada como property.
 * 
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 9, 2010
 */
public class XMLCharsetStrategy implements CharsetStrategy {
    
    /** Content types containing xml header */
    private String[] xmlTypes = {
        "text/xml", 
        "application/xml",
        "application/xhtml+xml",
        "application/rss+xml",
        "application/atom+xml", 
    };

    private boolean validate;
    
    /** * Creates the XMLCharsetStrategy. */
    public XMLCharsetStrategy() { 
        this.setValidate(false);
    }
    
    /** Creates the XMLCharsetStrategy.
     * @param validate */
    public XMLCharsetStrategy(final boolean validate) {
        this.setValidate(validate);
    }
    
    /** @see CharsetStrategy#getCharset(ResponseMetadata, InputStream) */
    public final Charset getCharset(final ResponseMetadata meta,
            final InputStream content) {
        Validate.notNull(meta);
        final String contentType = meta.getContentType();
        if (contentType == null
                || !StringUtils.startsWithAny(contentType, xmlTypes)) {
            return null;
        }
        
        try {
            final DocumentBuilderFactory builderFactory = 
                        DocumentBuilderFactory.newInstance();
            
            if (!validate) {
                builderFactory.setValidating(false);
                // To disable DOM DTD Validation 
                builderFactory.setFeature(
                  "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                   false);
            }
            final DocumentBuilder documentBuilder = builderFactory
                                                       .newDocumentBuilder();
            final Document dom = documentBuilder.parse(content);
            final String xmlEncoding = dom.getXmlEncoding();
            if (!StringUtils.isBlank(xmlEncoding)) {
                return Charset.forName(xmlEncoding);
            }
        } catch (ParserConfigurationException e) {
            // nada que hacer
        } catch (SAXException e) {
            // nada que hacer
        } catch (IOException e) {
            // nada que hacer
        }
        
        return null;
    }
    
    public final void setXmlTypes(final String[] xmlTypes) {
        this.xmlTypes = xmlTypes;
    }

    public final void setValidate(final boolean validate) {
        this.validate = validate;
    }

    public final boolean isValidate() {
        return validate;
    }

}
