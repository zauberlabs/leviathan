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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;

/**
 * {@link CharsetStrategy} que siempre retorna el mismo {@link Charset}
 *
 * @author Mariano Semelman
 * @since Dec 15, 2009
 */
public class FixedCharsetStrategy implements CharsetStrategy {

    private final Charset charset;

    /** un strategy que tiene como estrategy elegir utf-8 como encoding */
    public FixedCharsetStrategy() {
        this("utf-8");
    }

    /** constructor  falla si el charset no corresponde.*/
    public FixedCharsetStrategy(final String charset)
        throws UnsupportedCharsetException {
        this.charset = Charset.forName(charset);
    }

    /** @see CharsetStrategy#getCharset(ResponseMetadata, byte[]) */
    public final Charset getCharset(final ResponseMetadata meta,
            final InputStream documento) {
        Validate.notNull(meta);
        return this.charset;
    }

}
