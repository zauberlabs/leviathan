package com.zaubersoftware.leviathan.api.engine.impl

import com.zaubersoftware.leviathan.api.engine.Action
import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler

class ActionHandlerCategory {

  static def then(ActionHandler handler,  Closure aBlock) {
    handler.then(contextAware(aBlock))
  }

  static def contextAware(Closure aBlock) {
    def closure = new ContextAwareClosure(){
        void execute(arg0) {
          aBlock(arg0)
        }
      }
    aBlock.setDelegate(closure)
    closure
  }
}
