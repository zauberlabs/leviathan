/**
 * Copyright (c) 2009-2011 Zauber S.A. <http://www.zaubersoftware.com/>
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


/**
 * Represents a Leviathan fetching response processing flow.
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public interface ProcessingFlow {

    /**
     * Concatenates the given processing flow and returns a the concatenated ProcessingFlow.
     *
     * @param flow The flow to be concatenated.
     * @return The resulting concatenated flow.
     */
    ProcessingFlow concat(ProcessingFlow flow);

    /**
     * Appends a new pipe to the processing flow.
     *
     * @param pipe The pipe to be appended to the processing flow.
     * @return The resulting processing flow.
     */
    ProcessingFlow append(Pipe<?,?> pipe);
}
