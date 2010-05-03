/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;


import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 * 
 * 
 * @author Mariano Cortesi
 * @since May 3, 2010
 */
public class MultiDomainPoliteJobQueueTest {

    private Queue<String> finishedUris;

    /** set up */
    @Before
    public final void setUp() {
        finishedUris = new LinkedList<String>();
    }

    /** test */
    @Test(timeout = 1000)
    public final void testJobsWithDifferentDomains() throws InterruptedException {
        String[] uris = new String[] {
                "http://www.first.com/bleble/ppepee",
                "http://www.cono.con/fuuu",
                "http://www.third.com/bla",
        };
        
        MultiDomainPoliteJobQueue q = 
            new MultiDomainPoliteJobQueue(10, TimeUnit.SECONDS);
        for (int i = 0; i < uris.length; i++) {
            q.add(new EnqueuingJob(uris[i], finishedUris));
        }
        
        q.shutdown();
        try {
            while(true) {
                q.poll().run();
            }
        } catch(InterruptedException e) {
            // ok
        }
        
        assertEquals(Arrays.asList(uris), finishedUris);
    }

    /** test */
    @Test(timeout = 1000)
    public final void testJobsWithExcludedDomains() throws InterruptedException {
        String[] uris = new String[] {
                "http://www.first.com/1",
                "http://www.first.com/2",
                "http://www.first.com/3",
        };
        
        MultiDomainPoliteJobQueue q = new MultiDomainPoliteJobQueue(
                Collections.singleton("www.first.com"), 10, TimeUnit.SECONDS);
        
        for (int i = 0; i < uris.length; i++) {
            q.add(new EnqueuingJob(uris[i], finishedUris));
        }
        
        q.shutdown();
        try {
            while(true) {
                q.poll().run();
            }
        } catch(InterruptedException e) {
            // ok
        }
        
        assertEquals(Arrays.asList(uris), finishedUris);
    }
    
    /** test */
    @Test(timeout = 1000)
    public final void testJobsWithSameDomain() throws InterruptedException {
        String[] uris = new String[] {
                "http://www.first.com/1",
                "http://www.first.com/2",
                "http://www.first.com/3",
        };
        
        int timeout = 100;
        MultiDomainPoliteJobQueue q = new MultiDomainPoliteJobQueue(
                timeout, TimeUnit.MILLISECONDS);
        
        for (int i = 0; i < uris.length; i++) {
            q.add(new EnqueuingJob(uris[i], finishedUris));
        }
        
        q.shutdown();
        final long before = System.currentTimeMillis();
        try {
            while(true) {
                q.poll().run();
            }
        } catch(InterruptedException e) {
            // ok
        }
        final long after = System.currentTimeMillis();
        
        Assert.assertTrue("tiempo esperado: " + timeout * uris.length 
                + " tiempo real: " + (after - before), 
                (after - before) >= timeout * uris.length);
        
        assertEquals(new HashSet<String>(Arrays.asList(uris)), 
                new HashSet<String>(finishedUris));
    }
    
    /** test */
    @Test(timeout = 1000)
    public final void testJobsMultipleDomains() throws InterruptedException {
        String[] uris = new String[] {
                "http://www.first.com/1",
                "http://www.first.com/2",
                "http://www.second.com/1",
                "http://www.second.com/2",
                "http://www.first.com/3",
        };
        
        int timeout = 100;
        MultiDomainPoliteJobQueue q = new MultiDomainPoliteJobQueue(
                timeout, TimeUnit.MILLISECONDS);
        
        for (int i = 0; i < uris.length; i++) {
            q.add(new EnqueuingJob(uris[i], finishedUris));
        }
        
        q.shutdown();
        final long before = System.currentTimeMillis();
        try {
            while(true) {
                q.poll().run();
            }
        } catch(InterruptedException e) {
            // ok
        }
        final long after = System.currentTimeMillis();
        
        Assert.assertTrue("tiempo esperado: " + timeout * uris.length 
                + " tiempo real: " + (after - before), 
                (after - before) >= timeout * 3);
        
        assertEquals(Arrays.asList(uris[0], uris[2], uris[1], uris[3], uris[4]), 
                finishedUris);
    }
}
