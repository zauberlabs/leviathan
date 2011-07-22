/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

import ar.com.zauber.leviathan.api.AsyncUriFetcher;


/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public interface LeviathanBuilder {

    /**
     * Termina de configurar todo lo que es la instanciacion del {@link AsyncUriFetcher}.
     */
    Engine build();

}
