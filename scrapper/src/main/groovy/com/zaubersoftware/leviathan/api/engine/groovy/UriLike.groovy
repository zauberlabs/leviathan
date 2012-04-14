package com.zaubersoftware.leviathan.api.engine.groovy
import java.net.URI
import java.net.URL

import ar.com.zauber.leviathan.common.InmutableURIAndCtx

class UriLike {
   static toUri(String uri) {
      URI.create(uri)
   }
   static toUri(URI uri) {
      uri
   }
   static toUri(URL uri) {
      uri.toURI()
   }

   static toUriAndCtx(uriLike) {
      new InmutableURIAndCtx(toUri(uriLike));
   }
}
