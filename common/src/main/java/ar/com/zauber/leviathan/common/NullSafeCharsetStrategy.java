/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang.Validate;

/**
 * {@link CharsetStrategy} that has a default value. If the decorated
 * {@link CharsetStrategy} returns null, it coalesces to the given default
 * {@link Charset}
 * 
 * @author flbulgarelli
 * @since Jun 19, 2012
 */
public final class NullSafeCharsetStrategy implements CharsetStrategy {

    private final CharsetStrategy charsetStrategy;
    private final Charset defaultCharset;

    /**
     * Creates the NullSafeCharsetStrategy.
     *
     * @param charsetStrategy
     * @param defaultCharset
     */
    public NullSafeCharsetStrategy(final CharsetStrategy charsetStrategy, final Charset defaultCharset) {
        Validate.notNull(charsetStrategy);
        Validate.notNull(defaultCharset);
        this.charsetStrategy = charsetStrategy;
        this.defaultCharset = defaultCharset;
    }

    @Override
    public Charset getCharset(final ResponseMetadata meta, final InputStream content) {
        Charset charset = charsetStrategy.getCharset(meta, content);
        return charset != null ? charset : defaultCharset;
    }

}
