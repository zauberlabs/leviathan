package com.zaubersoftware.leviathan.api.engine.groovy
import static com.zaubersoftware.leviathan.api.engine.groovy.GLeviathan.*
import static org.junit.Assert.*
import AbstractFetchModule



job = { 
   parse mapper(jobOfferMapper)
   split { it.jobOfferDetails }
   each {
      if (it.location.length == 3)
         it.locationCountry = it.location[2].trim()
      else
         it.locationCountry = 'USA'
   }
   follow {
      [it.uri, it, jobDetail]
   }
}

jobDetail = { 
   parse mapper(jobOfferDetailsMapper)
   each {
      it.description = '' + it.content
   }
}

startOn('http://taulia.jobscore.com/list?iframe=1') {  
   parse mapper(linksMapper)
   split { it.jobOffers }
   follow {
      [it.uri, it, job]
   }
}