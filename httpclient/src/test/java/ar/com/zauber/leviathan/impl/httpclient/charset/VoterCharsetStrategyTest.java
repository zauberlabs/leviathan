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
/*
 * Copyright (c) 2010 TCPN -- All rights reserved
 */

package ar.com.zauber.leviathan.impl.httpclient.charset;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;


/**
 * Test para {@link VotersCharsetStrategy}
 * 
 * 
 * @author Mariano Focaraccio
 * @since Sep 16, 2010
 */
public class VoterCharsetStrategyTest {
    
    private CharsetStrategy voter1;
    private CharsetStrategy voter2;
    private CharsetStrategy voter3;
    
    /**Inicial Setup */
    @Before
    public final void setUp() {
        voter1 = mock(CharsetStrategy.class);
        voter2 = mock(CharsetStrategy.class);
        voter3 = mock(CharsetStrategy.class);
    }
    
    
    /**Test */
    @Test(expected = UnsupportedCharsetException.class)
    public final void defaultCharsetInvalido() {
       @SuppressWarnings("unused")
    final VotersCharsetStrategy strategy = 
       new VotersCharsetStrategy(Arrays.asList(voter1, voter2, voter3) , "saraza");
    }

    /**Test default charset null*/
    @Test(expected = Exception.class)
    public final void defaultCharsetNull() { 
        @SuppressWarnings("unused")
        final VotersCharsetStrategy strategy = 
            new VotersCharsetStrategy(Arrays.asList(voter1, voter2, voter3),
                    null);    
    }
    
    /**Test todos se abstienen*/
    @Test
    public final void todosSeAbstienen() { 
        when(voter1.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(null);
        when(voter2.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(null);
        when(voter3.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(null);
        final VotersCharsetStrategy strategy = 
            new VotersCharsetStrategy(Arrays.asList(voter1, voter2, voter3)
                                      , "utf-8");

        Charset charset = strategy.getCharset(mock(ResponseMetadata.class), 
                                                mock(InputStream.class));
        Assert.assertEquals(charset.name(), "UTF-8");
    }
   
    /**Test en el que hay un empate entre los voters*/
    @Test
    public final void multipleEmpate() {
        when(voter1.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(Charset.forName("utf-8"));
        when(voter2.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(Charset.forName("ISO8859-5"));
        when(voter3.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(null);
        final VotersCharsetStrategy strategy = 
            new VotersCharsetStrategy(Arrays.asList(voter1, voter2, voter3)
                                      , "utf-8");
        Charset charset = strategy.getCharset(mock(ResponseMetadata.class), 
                                                mock(InputStream.class));
        Assert.assertEquals(charset.name(), "UTF-8");
        
    }
    
    /** Test ganador Unico*/
    @Test
    public final void ganadorUnico() {
        when(voter1.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(Charset.forName("utf-8"));
        when(voter2.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(null);
        when(voter3.getCharset(any(ResponseMetadata.class), 
                any(InputStream.class))).thenReturn(Charset.forName("utf-8"));
        final VotersCharsetStrategy strategy = 
            new VotersCharsetStrategy(Arrays.asList(voter1, voter2, voter3)
                                      , "utf-8");
        Charset charset = strategy.getCharset(mock(ResponseMetadata.class), 
                                                mock(InputStream.class));
        Assert.assertEquals(charset.name(), "UTF-8");
        
    }
    
    /** Test donde no hay voters*/
    @Test
    public final void noVoters() {
        final VotersCharsetStrategy strategy = 
                    new VotersCharsetStrategy(null, "utf-8");
        Charset charset = strategy.getCharset(mock(ResponseMetadata.class), 
                mock(InputStream.class));
        Assert.assertEquals(charset.name(), "UTF-8");

    }
}

