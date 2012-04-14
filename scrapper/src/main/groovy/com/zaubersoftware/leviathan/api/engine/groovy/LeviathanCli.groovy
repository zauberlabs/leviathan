package com.zaubersoftware.leviathan.api.engine.groovy

import groovy.lang.Closure;
import groovy.transform.TupleConstructor

import java.net.URI
import java.net.URL
import static UriLike.*
import ar.com.zauber.leviathan.common.InmutableURIAndCtx

import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;


@TupleConstructor
class LeviathanCli {

   final scrapper

   boolean run(String scriptName) {
      def startUriLike
      def startFlow
      def indexBlock = {_startUriLike, _startFlow ->
         startUriLike = _startUriLike
         startFlow = _startFlow
      }
      runScript(scriptName, [
               index: indexBlock,
               flow: GLeviathan.&flow])

      runScrapper(startUriLike, startFlow)
      true
   }

   def runScrapper(startUriLike, startFlow) {
      scrapper.scrap(toUriAndCtx(startUriLike), toFlow(startFlow))
      scrapper.awaitIdleness()
   }


   def runScript(scriptName, binding){
      Script script = Class.forName(scriptName).newInstance()
      script.binding = new Binding(binding)
      script.run()
   }


   def toFlow(ProcessingFlow flow){
      flow
   }
   def toFlow(Closure block) {
      GLeviathan.flow block
   }
}
