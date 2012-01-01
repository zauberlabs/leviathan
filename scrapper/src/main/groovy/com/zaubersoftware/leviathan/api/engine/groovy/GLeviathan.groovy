package com.zaubersoftware.leviathan.api.engine.groovy

import com.zaubersoftware.leviathan.api.engine.ActionHandler
import com.zaubersoftware.leviathan.api.engine.AfterThen;
import com.zaubersoftware.leviathan.api.engine.ControlStructureHanlder
import com.zaubersoftware.leviathan.api.engine.ErrorTolerant
import com.zaubersoftware.leviathan.api.engine.Leviathan;
import com.zaubersoftware.leviathan.api.engine.ProcessingFlow;

class GLeviathan {

    static def enableGlobalSupport() {
        ActionHandler.mixin(ActionHandlerCategory)
        ErrorTolerant.mixin(ErrorTolerantCategory)
        ControlStructureHanlder.mixin(ControlStructureHanlderCategory)
    }
    
    
    static ProcessingFlow withEngine(aClosure) {
      use(ActionHandlerCategory, ErrorTolerantCategory, ControlStructureHanlderCategory) {
        aClosure(Leviathan.flowBuilder()).pack();
      }
    }
}
