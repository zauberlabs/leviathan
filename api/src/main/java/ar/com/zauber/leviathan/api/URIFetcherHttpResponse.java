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

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

/**
 * HTTP response information
 * 
 * @author Juan F. Codagnone
 * @since Oct 12, 2009
 */
public interface URIFetcherHttpResponse {

    /** status code */
    int getStatusCode();

    /** content */
    Reader getContent();

    /**
     * <p><strong>
     *  WARNING:
     *  It is highly possible you should be using 
     *  {@link URIFetcherHttpResponse#getContent()}
     * </strong></p>
     * 
     * <p>The caller is responsible for closing the stream after use.</p>
     * 
     * @return a new {@link InputStream} with the raw content
     * @throws IllegalStateException if raw content was not supplied
     */
    InputStream getRawContent();
    
    /**
     * Returns the first response header with the specified name, or null if it
     * is not present
     */
    String getHeader(String name);

    /**
     * Returns all the response headers with the specified name, or null if it
     * is not present
     */
    List<String> getHeaders(String name);
    
}
