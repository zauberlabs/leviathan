/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import java.net.URI;

import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * Abstracción del método utilizado para el fetching (GET o POST)
 * 
 * @author Francisco J. González Costanzó
 * @since Apr 12, 2010
 */
public interface HttpMethodCommand {

    /** fetch */
    URIFetcherResponse execute();

    /** uri a fetchear */
    URI getURI();
}
