package com.zaubersoftware.leviathan.api.engine.groovy
import javax.xml.transform.stream.StreamSource

import org.springframework.core.io.ClassPathResource

class AbstractFetchModule {

   final fetcher

   def fetch() {
      fetch(toUri(startUrl), withEngine(startFlow))
   }
   
   def toUri(String uri) {
      URI.create(uri)
   }
   def toUri(URI uri) {
      uri
   }
   def toUri(URL uri) {
      uri.toURI()
   }
   
   def fetch(uri, flow) {
      fetcher.scheduleFetch(fetcher.createGet(uri), withEngine(flow)).awaitIdleness()
   }
   
   
   def xslt = {name -> new StreamSource(new ClassPathResource(name).inputStream )}
   
}
