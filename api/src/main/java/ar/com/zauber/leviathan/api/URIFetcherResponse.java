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
package ar.com.zauber.leviathan.api;

import java.net.URI;
import java.util.Map;

/**
 * {@link URIFetcher} response.
 *
 *
 *
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface URIFetcherResponse {

    /** @return the URI associated with this response */
    @Deprecated
    URI getURI();

    /** @return the URI associated with this response and it's context */
    URIAndCtx getURIAndCtx();

    /**
     * @return <code>true</code> if the request was able to connect the server
     * an execute the request, retriving HTTP information.
     * If it failed you can call {@link #getError()} to get more information.
     */
    boolean isSucceeded();

    /**
     * @see #isSucceeded()
     * @throws IllegalStateException if called when {@link #isSucceeded()}
     * is <code>true</code>
     */
    Throwable getError() throws IllegalStateException;

    /**
     * @see #isSucceeded()
     * @throws IllegalStateException if called when {@link #isSucceeded()}
     * is <code>false</code>
     */
    URIFetcherHttpResponse getHttpResponse() throws IllegalStateException;


    /** an uri and a retrival ctx */
    interface URIAndCtx {
        /** @return the uri */
        URI getURI();
        /** @return it's context */
        Map<String, Object> getCtx();
    }
}
