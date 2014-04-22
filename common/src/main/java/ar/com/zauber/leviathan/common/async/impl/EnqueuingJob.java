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
