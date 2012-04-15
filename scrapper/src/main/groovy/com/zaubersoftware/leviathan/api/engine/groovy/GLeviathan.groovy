package com.zaubersoftware.leviathan.api.engine.groovy

import UriLike

import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.ControlStructureHanlder
import com.zaubersoftware.leviathan.api.engine.CurrentThreadURIAndContextDictionary
import com.zaubersoftware.leviathan.api.engine.ErrorTolerant
import com.zaubersoftware.leviathan.api.engine.Leviathan
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow

/**
 * Entry point for Leviathan's groovy support.
 * 
 * You can use it in two different fashions:
 * <ul>
 * <li>Globally mixing GLeviathan's categories into Leviathan's classes, and 
 * then using {@link Leviathan} hub as usual, 
 * through {@link #enableGlobalSupport()} message</li>
 * <li>Inverting control and locally mixing those categories inside a closure,
 *  through {@link #withEngine(Object)} message </li>
 * </ul>
 *   
 * @author flbulgarelli
 */
class GLeviathan {

   static def ctxMap = new CurrentThreadURIAndContextDictionary()
   
   static def enableGlobalSupport() {
      ActionHandler.mixin(ActionHandlerCategory)
      ErrorTolerant.mixin(ErrorTolerantCategory)
      ControlStructureHanlder.mixin(ControlStructureHanlderCategory)
   }

   
//   class BasicEngine {
//      @Delegate engine
//      def then(block) {
//         engine.thenDo(GAction.from(block))
//      }
//   }
//   
//   class SplittingEngine {
//      def engine
//      
//      def then(block) {
//            
//      }
//      def pack() {
//         engine.endFor()
//         engine.pack()
//      }
//   }

   static ProcessingFlow flow(closure) {
      def engine = Leviathan.flowBuilder().afterFetch();
      closure.delegate = new Object(){
         def then(it) {
            engine = engine.thenDo(GAction.from(it))
         }
         def exec(block) {
            then {
               block(it)
               it
            }
         }
         def onException(it) {
            engine = engine.onAnyExceptionDo(GExceptionHandler.from(it))
         }
         def parse(block) {
            then { response -> 
               block(new XmlSlurper().parse(response.content))
            }
         }
         def sanitize() {
            engine = engine.sanitizeHTML()
         }
         def follow(block) {
            then { 
               def (uriLike, flow) = block(it)
               ctxMap.ctx.scrapper.scrap(UriLike.toUriAndCtx(uriLike), flow)
            }
         }
      }
      closure()
      engine.pack()
   }
}
