/**
 * Copyright (c) 2009-2014 Zauber S.A. <http://zauberlabs.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ar.com.zauber.leviathan.impl;

import static org.junit.Assert.*;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.common.async.Job;
import ar.com.zauber.leviathan.common.fluent.Fetchers;


/**
 * Tests {@link Fetchers}.  
 * 
 * @author Juan F. Codagnone
 * @since Dec 29, 2011
 */
public class FetchersTest {
    
    /** test */
    @Test
    public final void testName() throws Exception {
        AsyncUriFetcher f = Fetchers.createAsync()
            .withFetchingScheduler()
                .withPoliteQueue()
                    .minimalDelay(500, TimeUnit.MILLISECONDS)
                    .withException("zauber.com.ar")
                    .donePoliteQueue()
                 // a la cola que acabamos de configurar la pisamos con otra
                .withRateLimitQueue()
                    .withThrowling(1000)
                    .doneRateLimitQueue()
                 // y a la anterior la piso con otra mas tonta
                .withBloquingQueue(new LinkedBlockingQueue<Job>())
                .withExecutorService(Executors.newFixedThreadPool(50))
                 // idem.. lo piso
                .withThreadedExecutorServicer()
                    .withCorePoolSize(10)
                    .withMaximumPoolSize(10)
                    .withKeepAliveTimeOut(10, TimeUnit.SECONDS)
                    .doneThreadedExecutorService()
                .doneScheduler()
            .build();

        assertNotNull(f);
    }
}
