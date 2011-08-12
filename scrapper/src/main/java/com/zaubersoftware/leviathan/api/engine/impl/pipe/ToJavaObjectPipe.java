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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;
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
    public ToJavaObjectPipe(final Class<T> classToBeBounded) {
        Validate.notNull(classToBeBounded);

        try {
            newInstance = JAXBContext.newInstance(classToBeBounded);
        } catch (final JAXBException e) {
            throw new UnhandledException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T execute(final Node input) {
        Validate.notNull(input, "The input node cannot be null");
        try {
            // TODO falta agregar la configuración de schemas etc
            final Unmarshaller u = newInstance.createUnmarshaller();
            return (T) u.unmarshal(input);
        } catch (final JAXBException e) {
            logger.error("An error ocurred while applying the XSL transformation", e);
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers");
        }
    }

}
