/**
 * Copyright (c) 2009-2015 Zauber S.A. <http://zauberlabs.com/>
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
package com.zaubersoftware.leviathan.api.engine;

import ar.com.zauber.leviathan.api.URIFetcherResponse;


/**
 * Represents a Leviathan fetching response processing flow.
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public interface ProcessingFlow {

    /**
     * Obtains a {@link Pipe} representation of the processing flow.
     *
     * @return The processing flow as a {@link Pipe}
     */
    Pipe<URIFetcherResponse, Void> toPipe();

}
