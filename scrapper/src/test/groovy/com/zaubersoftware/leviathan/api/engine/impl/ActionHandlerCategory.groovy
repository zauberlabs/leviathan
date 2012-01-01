package com.zaubersoftware.leviathan.api.engine.impl

import static com.zaubersoftware.leviathan.api.engine.impl.GAction.*
import static com.zaubersoftware.leviathan.api.engine.impl.GContextAwareClosure.*

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
