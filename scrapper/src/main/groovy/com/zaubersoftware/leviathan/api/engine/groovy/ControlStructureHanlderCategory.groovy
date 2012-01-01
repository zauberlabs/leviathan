package com.zaubersoftware.leviathan.api.engine.groovy

import static GContextAwareClosure.*

import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure
import com.zaubersoftware.leviathan.api.engine.ControlStructureHanlder
import com.zaubersoftware.leviathan.api.engine.ErrorTolerantActionAndControlStructureHandler

class ControlStructureHanlderCategory {

    static def forEachIn(ControlStructureHanlder handler, String propertyName, Closure aClosure) {
        handler.forEachIn(propertyName, contextAware (aClosure))
    }
}
