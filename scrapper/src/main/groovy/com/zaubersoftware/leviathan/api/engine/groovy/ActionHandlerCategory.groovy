package com.zaubersoftware.leviathan.api.engine.groovy

import static GAction.*
import static GContextAwareClosure.*

import com.zaubersoftware.leviathan.api.engine.Action
import com.zaubersoftware.leviathan.api.engine.ActionHandler

class ActionHandlerCategory {

    static def then(ActionHandler handler,  Closure aBlock) {
        handler.then(contextAware(aBlock))
    }
    
    static def thenDo(ActionHandler handler, Closure aBlock) {
        handler.thenDo(action(aBlock))
    }
}
