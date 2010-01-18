/**
 *  Copyright (c) 2009-2010 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.labs.kraken.fetcher.common;

import org.apache.commons.lang.builder.StandardToStringStyle;


/**
 * Cosas comunes para el modelo
 * 
 * @author Juan F. Codagnone
 * @since Oct 7, 2009
 */
public final class ModelUtils {
    /** utility class */
    private ModelUtils() {
        // void
    }
    
    /** estilo del toString */
    public static final StandardToStringStyle STYLE = new StandardToStringStyle();
    static {
        STYLE.setUseShortClassName(true);
        STYLE.setUseIdentityHashCode(false);
    }
    
    
}
