package com.zaubersoftware.leviathan.api.engine.groovy

import groovy.lang.Closure

import com.zaubersoftware.leviathan.api.engine.ContextAwareClosure

/**
 * {@link ar.com.zauber.commons.dao.Closure} wrappers for {@link Closure}s
 *
 * @author flbulgarelli
 */
class GClosure {

  /**
   * Answers a {@link ContextAwareClosure} that wraps the given 
   * closure, and sets the closure's delegate to the context
   * 
   * @param closure
   * @return a new {@link ContextAwareClosure}
   */
  static def contextAware(Closure closure) {
    //requires further discussion: closure = closure.clone()
    def contextAwareClosure = new ContextAwareClosure(){
          void execute(arg0) {
            closure(arg0)
          }
        }
    closure.delegate = contextAwareClosure
    contextAwareClosure
  }


  /**
   * Answers a plain {@link Closure}  that wraps the given
   * closure
   *
   * @param closure
   * @return a new {@link Closure}
   */
  static def from(Closure closure) {
    new ar.com.zauber.commons.dao.Closure() {
          void execute(arg0) {
            closure(arg0)
          }
        }
  }
}
