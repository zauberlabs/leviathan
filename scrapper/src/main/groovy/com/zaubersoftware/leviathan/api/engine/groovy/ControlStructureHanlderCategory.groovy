package com.zaubersoftware.leviathan.api.engine.groovy

import static GClosure.*

import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure
import com.zaubersoftware.leviathan.api.engine.ControlStructureHanlder
import com.zaubersoftware.leviathan.api.engine.ErrorTolerantActionAndControlStructureHandler

/**
* Category for enabling passing {@link Closure}s as {@link ContextAwareClosure}s
* within {@link ControlStructureHanlder}s
*
* @author flbulgarelli
*/
class ControlStructureHanlderCategory {

  static def forEachIn(ControlStructureHanlder handler, String propertyName, Closure aClosure) {
    handler.forEachIn(propertyName, contextAware (aClosure))
  }
}
