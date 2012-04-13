package com.zaubersoftware.leviathan.api.engine.groovy

import org.springframework.core.io.ClassPathResource

import com.zaubersoftware.leviathan.api.engine.Action
import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.ControlStructureHanlder
import com.zaubersoftware.leviathan.api.engine.ErrorTolerant
import com.zaubersoftware.leviathan.api.engine.FetchRequest;
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

   static def enableGlobalSupport() {
      ActionHandler.mixin(ActionHandlerCategory)
      ErrorTolerant.mixin(ErrorTolerantCategory)
      ControlStructureHanlder.mixin(ControlStructureHanlderCategory)
   }


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
         def follow(it) {
            def uriLike, flow = it()
            engine = engine.thenDoAndFetch(GAction.from { new FetchRequest  })
         }
         
      }
      closure()
      engine.pack()
   }
}
