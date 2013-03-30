/**
 * Copyright (c) 2009-2013 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common.fluent;

import java.net.URI;
import java.nio.charset.Charset;

import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;

/**
 * Builds {@link FixedURIFetcher}  
 * 
 * @author Juan F. Codagnone
 * @since Dec 29, 2011
 */
public interface FixedFetcherBuilder {

    /** charset used to read classpath resources */
    FixedFetcherBuilder withCharset(String ch);
    
    /** charset used to read classpath resources */
    FixedFetcherBuilder withCharset(Charset ch);
    
    /** start registering an uri */
    ThenMockFetcherBuilder  when(String uri);
    /** start registering an uri */
    ThenMockFetcherBuilder  when(URI uri);
    
    /** register uri with a classpath */
    FixedFetcherBuilder register(String uri, String classpath);
    
    /** register uri with a classpath */
    FixedFetcherBuilder register(URI uri, String classpath);
    
    /** build the {@link FixedURIFetcher} */
    URIFetcher build();
    
    /** register the classpath resource*/
    interface ThenMockFetcherBuilder {
        /** register the classpath resource*/
        FixedFetcherBuilder then(String classpath);
    }
}
