package com.zaubersoftware.leviathan.api.engine.impl

import static GContextAwareClosure.*
import com.zaubersoftware.leviathan.api.engine.Action
import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure
import com.zaubersoftware.leviathan.api.engine.ExceptionHandler

class ActionHandlerCategory {

    static def then(ActionHandler handler,  Closure aBlock) {
        handler.then(contextAware(aBlock))
    }
}
