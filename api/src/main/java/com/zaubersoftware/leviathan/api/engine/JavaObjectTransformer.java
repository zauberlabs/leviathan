/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

/**
 * Defines the method to convert a document into a Java object. 
 * 
 * @author Guido Marucci Blas
 * @author Martín Silva
 * @author Juan F. Codagnone
 * @since Jul 22, 2011
 */
public interface JavaObjectTransformer {
    
    /**
     * Deserializes the JSON or XML document into a Java object of the given {@link Class}.
     * 
     * @param <T> The type of the object to be deserialized.
     * @param aClass The class of the target object. Must not be null.
     * @return An {@link AfterJavaObjectHandler} typed on the deserialized object's class.
     * @throws IllegalStateException if the resource to be converted to a Java object is not a JSON
     * or XML document.
     * @throws IllegalArgumentException if aClass is null.
     */
    <T> AfterJavaObjectHandler<T> toJavaObject(Class<T> aClass);

}
