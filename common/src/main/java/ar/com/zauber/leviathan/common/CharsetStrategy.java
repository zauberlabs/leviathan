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
package ar.com.zauber.leviathan.common;

import java.io.InputStream;
import java.nio.charset.Charset;




/**
 * Strategy para poder elegir el tipo de Charset a utilizar.
 * @author Mariano Semelman
 * @since Dec 15, 2009
 */
public interface CharsetStrategy {

    /**
     * dado los parametros, decide que Charset utilizar
     * @param meta no nulo
     * @param content puede ser nulo.
     * @return el Charset a utilizar.
     */
    Charset getCharset(ResponseMetadata meta, InputStream content);

}
