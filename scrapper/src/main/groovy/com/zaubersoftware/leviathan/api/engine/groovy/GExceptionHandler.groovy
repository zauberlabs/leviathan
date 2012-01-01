package com.zaubersoftware.leviathan.api.engine.groovy

import com.zaubersoftware.leviathan.api.engine.ExceptionHandler

class GExceptionHandler {
    static def exceptionHandler(aBlock) {
        new ExceptionHandler() {
                    void handle(Throwable arg0) {
                        aBlock(arg0)
                    }
                }
    }
}
