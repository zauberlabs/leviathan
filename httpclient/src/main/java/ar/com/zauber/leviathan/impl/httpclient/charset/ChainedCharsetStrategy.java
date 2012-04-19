/**
 * Copyright (c) 2009-2012 Zauber S.A. <http://zauberlabs.com/>
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
import java.util.List;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;

/**
 * {@link CharsetStrategy} que delega a una lista de {@link CharsetStrategy}'s.
 * Devuelve el primer Charset obtenido.
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 9, 2010
 */
public class ChainedCharsetStrategy implements CharsetStrategy {
    
    private final List<CharsetStrategy> strategies;

    /** Creates the ChainedCharsetStrategy. */
    public ChainedCharsetStrategy(final List<CharsetStrategy> strategies) {
        Validate.notNull(strategies);
        this.strategies = strategies;
    }

    /** @see CharsetStrategy#getCharset(ResponseMetadata, InputStream) */
    public final Charset getCharset(final ResponseMetadata meta, 
            final InputStream content) {
        for (CharsetStrategy strategy : strategies) {
            Charset charset = strategy.getCharset(meta, content);
            if (charset != null) {
                return charset;
            }
        }
        return null;
    }

}
