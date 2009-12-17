/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

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
