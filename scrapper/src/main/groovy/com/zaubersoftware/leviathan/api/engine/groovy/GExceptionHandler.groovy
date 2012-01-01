package com.zaubersoftware.leviathan.api.engine.groovy

import com.zaubersoftware.leviathan.api.engine.ExceptionHandler

/**
 * {@link ExceptionHandler} wrapper for {@link Closure}s
 *
 * @author flbulgarelli
 */
class GExceptionHandler {

  static def from(closure) {
    new ExceptionHandler() {
          void handle(Throwable arg0) {
            closure(arg0)
          }
        }
  }
}
