package com.zaubersoftware.leviathan.api.engine.groovy;

import com.zaubersoftware.leviathan.api.engine.Action;

class GAction {
    
    static def action(aBlock) {
        new Action() {
            def execute( arg0) {
              aBlock(arg0)
            }
          }
      }

}
