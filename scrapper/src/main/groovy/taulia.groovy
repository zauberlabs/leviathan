//jobDetail = flow {
//   
//}
//


/*
 * A leviathan file defines and connects a bunch 
 * of scrapping flows. 
 * 
 * Flows are declared through the flow expression. 
 * A flow may contain the following actions:
 * exec: performs an effect on the scrapped value
 * then: alters the transformed value. It also may, but shouldn't, perform effects
 * follow: connects this flow with another scrapping flow
 * 
 * TODO split, parse, sanitize, 
 * TODO compose flow parts
 * 
 */

job = flow {
   exec {
      print it
   }
}

index('http://taulia.jobscore.com/list?iframe=1') {
   exec {
      println "Raw response is ${it}"
   }
//   sanitizeHTML() 
//   parse {
//      [name: it.head.text(), 
//       foo: it.children().h1.text(),
//       jobUri: 'dasdasd']
//   }
   exec {
      print "Parsed response is ${it}"
   }
   then {
      println "Replacing response by something else"
      [jobUri: 'www.google.com']
   }
//   split {
//       it.links  
//   }
   follow {
      [it.jobUri, job]
   }
}