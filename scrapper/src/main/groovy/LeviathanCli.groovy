
import groovy.transform.TupleConstructor;

import java.net.URI
import java.net.URL
import java.util.concurrent.Executors

import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher
import ar.com.zauber.leviathan.common.fluent.Fetchers

import com.zaubersoftware.leviathan.api.engine.groovy.GLeviathan


@TupleConstructor
class LeviathanCli {
   
   final fetcher
   final asyncFetcher
   
   boolean run(String scriptName) { 
      def startUriLike
      def startFlow 
      def indexBlock = {_startUriLike, _startFlow ->
         startUriLike = _startUriLike
         startFlow = _startFlow  
      }
      runScript(scriptName, [index: indexBlock, flow: GLeviathan.&flow])
      asyncFetcher.scheduleFetch(fetcher.createGet(toUri(startUriLike)), GLeviathan.flow(startFlow)).awaitIdleness()
      true
   }
   
   
   def runScript(scriptName, binding){
      Script script = Class.forName(scriptName).newInstance()
      script.binding = new Binding(binding)
      script.run()
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
   
}
