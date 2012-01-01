package com.zaubersoftware.leviathan.api.engine.groovy

import static GAction.*
import static GClosure.*
import groovy.lang.Closure;

import com.zaubersoftware.leviathan.api.engine.Action
import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure;

/**
 * Category for enabling passing {@link Closure}s as {@link ContextAwareClosure}s
 * within {@link ActionHandler}s 
 *
 * @author flbulgarelli
 */
class ActionHandlerCategory {

  static def then(ActionHandler handler,  Closure aBlock) {
    handler.then(contextAware(aBlock))
  }

  static def thenDo(ActionHandler handler, Closure aBlock) {
    handler.thenDo(from(aBlock))
  }
}
