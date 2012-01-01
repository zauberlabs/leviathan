package com.zaubersoftware.leviathan.api.engine.impl;

import groovy.lang.Closure;

import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure;

class GContextAwareClosure {
    
    static def contextAware(Closure aBlock) {
        def closure = new ContextAwareClosure(){
            void execute(arg0) {
              aBlock(arg0)
            }
          }
        aBlock.setDelegate(closure)
        closure
      }
}
