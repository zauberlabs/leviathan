package com.zaubersoftware.leviathan.api.engine.groovy

import static GExceptionHandler.*

import groovy.lang.Closure

import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.AfterExceptionCatchDefinition
import com.zaubersoftware.leviathan.api.engine.AfterHandleWith
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure
import com.zaubersoftware.leviathan.api.engine.ErrorTolerant
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler

/**
 * Category for enabling passing {@link Closure}s as {@link ExceptionHandler}s
 * within {@link ErrorTolerant}s
 *
 * @author flbulgarelli
 */
class ErrorTolerantCategory {

  static def handleWith(ErrorTolerant handler,  Closure aBlock) {
    handler.handleWith(from(aBlock))
  }

  static def onExceptionHandleWith(ErrorTolerant handler,  Class throwableClass, Closure aBlock) {
    handler.onExceptionHandleWith(throwableClass, from(aBlock))
  }

  static def otherwiseHandleWith(ErrorTolerant handler, Closure aBlock) {
    handler.otherwiseHandleWith(from(aBlock))
  }

  static def onAnyExceptionDo(ErrorTolerant handler, Closure aBlock) {
    handler.onAnyExceptionDo(from(aBlock))
  }
}
