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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;

/**
 * A {@link CharsetStrategy} that detects the charset taking into
 * account the byte order mark.
 * 
 * @author Guido Marucci Blas
 * @since 31/07/2012
 * @see http://en.wikipedia.org/wiki/Byte_order_mark
 */
public final class ByteOrderMarkCharsetStrategy implements CharsetStrategy {

    @SuppressWarnings("serial")
    private static final List<ByteOrderMark> BOM_LIST = new ArrayList<ByteOrderMark>() {{
        add(new ByteOrderMark(Charset.forName("UTF-8"), new byte[]{ (byte)0xEF, (byte)0xBB, (byte)0xBF }));       
        add(new ByteOrderMark(Charset.forName("UTF-16BE"), new byte[]{ (byte)0xFE, (byte)0xFF }));        
        add(new ByteOrderMark(Charset.forName("UTF-16LE"), new byte[]{ (byte)0xFF, (byte)0xFE }));         
        add(new ByteOrderMark(Charset.forName("UTF-32BE"), new byte[]{ (byte)0x00, (byte)0x00, (byte)0xFE, (byte)0xFF }));    
        add(new ByteOrderMark(Charset.forName("UTF-32LE"), new byte[]{ (byte)0xFF, (byte)0xFE, (byte)0x00, (byte)0x00 }));    
//        add(new ByteOrderMark(Charset.forName("UTF-7"), new byte[]{ (byte)0x2B, (byte)0x2F, (byte)0x76, (byte)0x38 }));
//        add(new ByteOrderMark(Charset.forName("UTF-7"), new byte[]{ (byte)0x2B, (byte)0x2F, (byte)0x76, (byte)0x39 }));
//        add(new ByteOrderMark(Charset.forName("UTF-7"), new byte[]{ (byte)0x2B, (byte)0x2F, (byte)0x76, (byte)0x2B }));
//        add(new ByteOrderMark(Charset.forName("UTF-7"), new byte[]{ (byte)0x2B, (byte)0x2F, (byte)0x76, (byte)0x2F }));
//        add(new ByteOrderMark(Charset.forName("UTF-1"), new byte[]{ (byte)0xF7, (byte)0x64, (byte)0x4C }));
//        add(new ByteOrderMark(Charset.forName("UTF-EBCDIC"), new byte[]{ (byte)0xDD, (byte)0x73, (byte)0x66, (byte)0x73 }));
//        add(new ByteOrderMark(Charset.forName("SCSU"), new byte[]{ (byte)0x0E, (byte)0xFE, (byte)0xFF }));
//        add(new ByteOrderMark(Charset.forName("BOCU-1"), new byte[]{ (byte)0xFB, (byte)0xEE, (byte)0x28 })); 
//        add(new ByteOrderMark(Charset.forName("GB-18030"), new byte[]{ (byte)0x84, (byte)0x31, (byte)0x95, (byte)0x33 }));  
    }};
    
    @Override
    public Charset getCharset(final ResponseMetadata meta, final InputStream content) {
        Charset detectedCharset = null;
        try {
            final byte[] bytes = new byte[4];
            if (content.read(bytes, 0, 4) == 4) {
                for (final ByteOrderMark bom : BOM_LIST) {
                    final byte[] copy = Arrays.copyOf(bytes, bom.bytes.length);
                    if (Arrays.equals(bom.bytes, copy)) {
                        detectedCharset = bom.charset;
                        throw new SkipBytesException(bom.bytes.length, detectedCharset);
                    }
                }
            }
        } catch (final IOException e) {
            // Nothing to be done
        }
        return detectedCharset;
    }

    private static final class ByteOrderMark {
        
        private final Charset charset;
        private final byte[] bytes;
        
        /**
         * Creates the ByteOrderMark.
         *
         * @param charset
         * @param bytes
         */
        public ByteOrderMark(final Charset charset, final byte[] bytes) {
            this.charset = charset;
            this.bytes = bytes;
        }       
        
    }
}
