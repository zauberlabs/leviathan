package com.zaubersoftware.leviathan.api.engine.groovy

import static GExceptionHandler.*

import groovy.lang.Closure

import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.AfterExceptionCatchDefinition
import com.zaubersoftware.leviathan.api.engine.AfterHandleWith
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure
import com.zaubersoftware.leviathan.api.engine.ErrorTolerant
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler

class ErrorTolerantCategory {

    static def handleWith(ErrorTolerant handler,  Closure aBlock) {
        handler.handleWith(exceptionHandler(aBlock))
    }

    static def onExceptionHandleWith(ErrorTolerant handler,  Class throwableClass, Closure aBlock) {
        handler.onExceptionHandleWith(throwableClass, exceptionHandler(aBlock))
    }

    static def otherwiseHandleWith(ErrorTolerant handler, Closure aBlock) {
        handler.otherwiseHandleWith(exceptionHandler(aBlock))
    }

    static def onAnyExceptionDo(ErrorTolerant handler, Closure aBlock) {
        handler.onAnyExceptionDo(exceptionHandler(aBlock))
    }
}
