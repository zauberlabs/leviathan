/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package com.zaubersoftware.leviathan.api.engine;

import java.util.List;

import org.junit.Test;

import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.commons.dao.exception.DuplicatedEntityException;
import ar.com.zauber.leviathan.api.URIFetcherResponse;


/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Guido Marucci Blas
 * @since Jul 22, 2011
 */
public class NewApiTest {

    @Test
    public void testname() throws Exception {
        Engine engine = Leviathan.newEngine().build();
        
        engine.forUri("http://google.com/").then(new ContextAwareClosure<URIFetcherResponse>() {
            @Override
            public void execute(final URIFetcherResponse response) {
                // ok! nos llego la pagina.
            }
        });
        
        
        engine.forUri("http://www.dmoz.org/").sanitizeHTML().transformXML("raiz.xsl")
                 .transformXML("otramas.xml")
                 .toJavaObject(Categories.class)
                 .then(new Action<Categories, List<Category>>() {
                    @Override
                    public List<Category> execute(final Categories t) {
                        get("ContextVariable");
                        put("ContextObject", "VALUE");
                        return t.getCategories();
                    }
                    
                }).then(new ContextAwareClosure<List<Category>>() {
                    @Override
                    public void execute(final List<Category> categories) {
                        get("ContextVariable");
                    }
                });
        
        engine.afterFetch().sanitizeHTML().transformXML("raiz.xsl")
            .transformXML("otramas.xml")
            .toJavaObject(Categories.class)
            .then(new Action<Categories, List<Category>>() {
                @Override
                public List<Category> execute(final Categories t) {
                    get("ContextVariable");
                    put("ContextObject", "VALUE");
                    return t.getCategories();
                }
           
           }).then(new ContextAwareClosure<List<Category>>() {
               @Override
               public void execute(final List<Category> categories) {
                   get("ContextVariable");
               }
           });
            
        
        engine.forUri("http://www.dmoz.org/")
            .sanitizeHTML()
            .transformXML("raiz.xsl")
            .transformXML("otramas.xml")
            .toJavaObject(Categories.class)
            .then(new Action<Categories, Categories>() {
                @Override
                public Categories execute(final Categories t) {
                    // DO something with the categories
                    return t;
                }
            })
            .forEach(Category.class).in("categories")
                .thenFetch("http://www.dmoz.org/#[root.getName()]")
                    .then(new ContextAwareClosure<URIFetcherResponse>() {
                        @Override
                        public void execute(final URIFetcherResponse arg0) {
                        }
                    })
                    .onError(DuplicatedEntityException.class, new ExceptionHandler<DuplicatedEntityException>() {
                        @Override
                        public void handle(final DuplicatedEntityException trowable) {
                            // Log error
                        }
                    });
        
        engine.forUri("http://www.dmoz.org/").sanitizeHTML().transformXML("raiz.xsl")
            .transformXML("otramas.xml")
            .toJavaObject(Categories.class)
            .then(new ActionAndThenFetch<Categories>() {
                @Override
                public FetchRequest execute(final Categories t) {
                    return null;
                }
           });
        
            
        engine.onError(new ExceptionHandler<Throwable>() {
            @Override
            public void handle(final Throwable trowable) {
                // Log error
            }
        });
//        engine.forUri(...);
//        
//        
//        FetchingProcessor processor = engine.getFetchingProcessor();
//        processor.fetch("http://www.dmoz.org/", model);
        
    }
}
