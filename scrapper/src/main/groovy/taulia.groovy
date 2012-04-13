//jobDetail = flow {
//   
//}
//
job = flow {
   
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
   follow {
      [it.jobUri, job]
   }
}