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
package ar.com.zauber.leviathan.common;

import java.net.URI;

import ar.com.zauber.commons.web.proxy.ContentTransformer.ContentMetadata;

/**
 * Esta interfaz permite tener mas datos de una respuesta
 * como la URI de forma completa
 * y un posible encoding.
 *
 * @author Mariano Semelman
 * @since Dec 15, 2009
 */
public interface ResponseMetadata extends ContentMetadata {


    /** @return el encoding de este response (puede ser nulo)*/
    String getEncoding();

    /** @return el URI al que de donde provino este response (no nulo) */
    URI getURI();

}
