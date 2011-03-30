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
package ar.com.zauber.leviathan.common.async;

import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * Permite mostrar el progreso de los downloads.
 * 
 * @author Juan F. Codagnone
 * @since Feb 27, 2010
 */
public interface AsyncUriFetcherObserver {

    /** una nueva url se quiere encolar como trabajo */
    void newFetch(URIAndCtx uriAndCtx);
    
    /** se comienza a descargar */
    void beginFetch(URIAndCtx uriAndCtx);
    
    /** se termina a descargar */
    void finishFetch(URIAndCtx uriAndCtx, long elapsed);
    
    /** se comienza a procesar */
    void beginProcessing(URIAndCtx uriAndCtx);
    
    /** se termina a procesar */
    void finishProcessing(URIAndCtx uriAndCtx, long elapsed);
}
