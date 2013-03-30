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
package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.mozilla.universalchardet.UniversalDetector;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;


/**
 * Implementación de {@link CharsetStrategy} que analiza el contenido del 
 * documento para intentar determinar el encoding del mismo.
 * En caso de no poder lograrlo retorna <code>null</code>
 * 
 * 
 * @author Mariano Focaraccio
 * @since Sep 16, 2010
 */
public class DetectorCharsetStrategy implements CharsetStrategy {

    /** @see CharsetStrategy#getCharset(ResponseMetadata, InputStream) */
    public final Charset getCharset(final ResponseMetadata meta, 
                                    final InputStream content) {
        
        final UniversalDetector detector = new UniversalDetector(null);
        final byte[] buf = new byte[4096];
        int nread;
        try {
            while ((nread = content.read(buf)) > 0 && !detector.isDone()) {
              detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            final String charset = detector.getDetectedCharset();
            final Charset result = (charset == null) ? null 
                                     : Charset.forName(charset);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

