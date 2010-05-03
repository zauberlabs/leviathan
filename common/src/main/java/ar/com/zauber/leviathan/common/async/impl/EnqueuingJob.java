/*
 * Copyright (c) 2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common.async.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;
import ar.com.zauber.leviathan.common.async.Job;

/**
 * TODO Descripcion de la clase. Los comentarios van en castellano.
 * 
 * 
 * @author Mariano Cortesi
 * @since May 3, 2010
 */
public class EnqueuingJob implements Job {

    private final String uri;
    private final Queue<String> uriQueue;

    public EnqueuingJob(final String uri, final Queue<String> uriQueue) {
        Validate.notNull(uri);
        this.uri = uri;
        this.uriQueue = uriQueue;
    }
    /** @see ar.com.zauber.leviathan.common.async.Job#getUriAndCtx() */
    public URIAndCtx getUriAndCtx() {
        try {
            return new InmutableURIAndCtx(new URI(this.uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("not valid uri: " + uri);
        }
    }

    /** @see java.lang.Runnable#run() */
    public void run() {
        this.uriQueue.add(this.uri);
    }

}
