package com.zaubersoftware.leviathan.api.engine.groovy


import groovy.transform.TupleConstructor
import ar.com.zauber.leviathan.api.AsyncUriFetcher
import ar.com.zauber.leviathan.api.URIFetcher
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx
import ar.com.zauber.leviathan.common.InmutableURIAndCtx

import com.zaubersoftware.leviathan.api.engine.ProcessingFlow

/**
 * @author franco
 *
 */
@TupleConstructor
class Scrapper {

   @Delegate
   final AsyncUriFetcher asyncUriFetcher;
   final URIFetcher fetcher;

   def scrap(URIAndCtx uriAndCtx, ProcessingFlow flow) {
      asyncUriFetcher.scheduleFetch(fetcher.createGet(putThisToContext(uriAndCtx)), flow);
   }
   
   def putThisToContext(uriAndCtx) {
      def newCtx = [:]
      newCtx << uriAndCtx.getCtx()
      newCtx["scrapper"] =  this
      new InmutableURIAndCtx(uriAndCtx.getURI(), newCtx)
   }

}
