/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.mock;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;


/**
 * TODO Descripcion de la clase. Los comenterios van en castellano.
 * 
 * 
 * @author Juan F. Codagnone
 * @since Sep 22, 2010
 */
public class QueryParamURITransformerTest {

    /** test */
    @Test
    public final void testfParametro() throws URISyntaxException {
        Assert.assertEquals("bar", new QueryParamURITransformer("foo").transform(
                new URI("http://lala/?foo=bar&lala=popo")));
    }
    
    /** test */
    @Test
    public final void testfParametroQueNova() throws URISyntaxException {
        Assert.assertEquals(null, new QueryParamURITransformer("xxxx").transform(
                new URI("http://lala/?foo=bar")));
    }
}
