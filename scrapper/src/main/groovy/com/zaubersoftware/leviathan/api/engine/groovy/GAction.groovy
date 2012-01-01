package com.zaubersoftware.leviathan.api.engine.groovy

import groovy.lang.Closure

import com.zaubersoftware.leviathan.api.engine.Action

/**
 * {@link Action} wrapper for {@link Closure}s
 * 
 * @author flbulgarelli
 */
class GAction {

  /**
   * Wraps a closure into an {@link Action}
   * 
   * @param aBlock
   * @return the new action
   */
  static def from(aBlock) {
    new Action() {
          def execute( arg0) {
            aBlock(arg0)
          }
        }
  }
}
