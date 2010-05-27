/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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
package ar.com.zauber.leviathan.common.async;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;


/**
 * Contiene la informacion necesaria para obtener recursos. Estas tareas se 
 * suelen encolar hasta que se puedan realizar. 
 * 
 * @author Juan F. Codagnone
 * @since Feb 16, 2010
 */
public interface Job extends Runnable {
    
    /** @return un UriAndCtx */
    URIAndCtx getUriAndCtx();
}
