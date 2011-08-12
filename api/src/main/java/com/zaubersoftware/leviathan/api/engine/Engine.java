/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

import java.net.URI;

/**
 * This class represents a fetching engine that is an instance of Leviathan. 
 * It is the entry point for the engine configuration.
 * 
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface Engine extends ErrorTolerant<Engine> {

    /**
     * Builder method which is the entry point to configure the engine behavior for the given
     * <code>uriTemplate</code>
     * 
     * @param uriTemplate An URI template. Must not be <code>null</code> or empty.
     * @return An {@link AfterFetchingHandler} that let's the users configure what to do after
     * the resource pointed by the URI template has been fetched.
     */
    AfterFetchingHandler forUri(String uriTemplate);
    
    /**
     * Builder method which is the entry point to configure the engine behavior.
     * 
     * @return An {@link AfterFetchingHandler} that let's the users configure what to do after
     * the resource pointed by the URI template has been fetched.
     */
    AfterFetchingHandler afterFetch();
    
    /**
     * Builder method which is the entry point to configure the engine behavior for the given
     * <code>URI</code>
     * 
     * @param uri The location of the resource to be fetched. Must not be <code>null</code>.
     * @return An {@link AfterFetchingHandler} that let's the users configure what to do after
     * the resource pointed by the URI has been fetched.
     */
    AfterFetchingHandler forUri(URI uri);
    
}
