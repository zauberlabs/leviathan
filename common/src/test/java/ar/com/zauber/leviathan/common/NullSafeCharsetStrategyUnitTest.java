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
package ar.com.zauber.leviathan.common;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test for {@link NullSafeCharsetStrategy}
 * 
 * @author flbulgarelli
 * @since Jun 19, 2012
 */
public class NullSafeCharsetStrategyUnitTest {

    private CharsetStrategy strategy;
    private Charset charset0;
    private Charset charset1;
    private InputStream inputStream;
    private ResponseMetadata responseMetada;

    @Before
    public void setup() {
        strategy = Mockito.mock(CharsetStrategy.class);
        responseMetada = Mockito.mock(ResponseMetadata.class);
        inputStream = Mockito.mock(InputStream.class);
        setSomeArbitraryCharsets();
    }

    private void setSomeArbitraryCharsets() {
        Iterator<Charset> iterator = Charset.availableCharsets().values().iterator();
        charset0 = iterator.next();
        charset1 = iterator.next();
    }

    @Test
    public final void answersTheOriginalCharsetIfNonNull() {
        when(strategy.getCharset(same(responseMetada), same(inputStream))).thenReturn(charset0);

        assertSame(charset0,
                new NullSafeCharsetStrategy(strategy, charset1).getCharset(responseMetada, inputStream));
    }

    @Test
    public final void answersTheDefaultCharsetIfOriginalIsNull() {
        when(strategy.getCharset(same(responseMetada), same(inputStream))).thenReturn(null);

        assertSame(charset1,
                new NullSafeCharsetStrategy(strategy, charset1).getCharset(responseMetada, inputStream));
    }

}
