/*
 * Copyright (c) 2012 Zauber S.A. -- All rights reserved
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
