/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.core.fetcher.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import ar.com.zauber.labs.kraken.fetcher.api.BulkURIFetcher;

/**
 * Tests module context
 * 
 * @author Juan F. Codagnone
 * @since Oct 13, 2009
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/fetcher-impl-kraken-spring.xml"
    }
)
public class FetcherImplContextTest extends AbstractJUnit4SpringContextTests {
    // CHECKSTYLE:ALL:OFF
    @Autowired
    protected BulkURIFetcher multitheadedBulkURIFetcher;
    // CHECKSTYLE:ALL:ON

    /** test */
    @Test
    public final void foo() {
        Assert.assertNotNull(multitheadedBulkURIFetcher);
    }
}
