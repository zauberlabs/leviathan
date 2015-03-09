/**
 * Copyright (c) 2009-2015 Zauber S.A. <http://zauberlabs.com/>
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

import java.nio.charset.Charset;

/**
 * This exception is thrown when N bytes from the request response must be skipped because a binary
 * charset mark is used. Once the charset is detected the bytes must be skipped. 
 * 
 * @author Guido Marucci Blas
 * @since 31/07/2012
 * @see http://en.wikipedia.org/wiki/Byte_order_mark
 */
public final class SkipBytesException extends RuntimeException {

    /** <code>serialVersionUID</code> */
    private static final long serialVersionUID = -4472305308775666455L;
    
    private final int bytesToSkip;
    private final Charset charset;
    
    /**
     * Creates the SkipBytesException.
     *
     * @param bytesToSkip
     */
    public SkipBytesException(final int bytesToSkip, final Charset charset) {
        super(String.format("%d bytes need to be skipped", bytesToSkip));
        this.bytesToSkip = bytesToSkip;
        this.charset = charset;
    }

    /**
     * Returns the bytesToSkip.
     * 
     * @return <code>int</code> with the bytesToSkip.
     */
    public int getBytesToSkip() {
        return bytesToSkip;
    }
    
    /**
     * Returns the charset.
     * 
     * @return <code>Charset</code> with the charset.
     */
    public Charset getCharset() {
        return charset;
    }
}
