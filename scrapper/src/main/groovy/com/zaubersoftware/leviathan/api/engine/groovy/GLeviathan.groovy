package com.zaubersoftware.leviathan.api.engine.groovy

import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.AfterThen
import com.zaubersoftware.leviathan.api.engine.ControlStructureHanlder
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

  static def enableGlobalSupport() {
    ActionHandler.mixin(ActionHandlerCategory)
    ErrorTolerant.mixin(ErrorTolerantCategory)
    ControlStructureHanlder.mixin(ControlStructureHanlderCategory)
  }


  static ProcessingFlow withEngine(closure) {
    use(ActionHandlerCategory, ErrorTolerantCategory, ControlStructureHanlderCategory) {
      closure(Leviathan.flowBuilder()).pack()
    }
  }
}
