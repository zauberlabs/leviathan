/*
 * Copyright (c) 2009 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

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
