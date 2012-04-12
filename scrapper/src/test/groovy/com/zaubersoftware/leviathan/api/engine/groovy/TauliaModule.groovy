package com.zaubersoftware.leviathan.api.engine.groovy
/**
 * Copyright (c) 2009-2011 Zauber S.A. <http://www.zaubersoftware.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.zaubersoftware.leviathan.api.engine.groovy.GLeviathan.*
import static org.junit.Assert.*
import AbstractFetchModule

startUrl = 'http://taulia.jobscore.com/list?iframe=1'

job = {
   parse mapper(jobOfferMapper)
   split { it.jobOfferDetails }
   each {
      if (it.location.length ==3)
         it.locationCountry = it.location[2].trim()
      else
         it.location_country = 'USA'
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

startFlow = {
   parse mapper(linksMapper)
   split { it.jobOffers }
   follow {
      [it.uri, it, job]
   }
}