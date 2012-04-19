/**
 * Copyright (c) 2009-2012 Zauber S.A. <http://zauberlabs.com/>
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
package ar.com.zauber.leviathan.common.fluent;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ar.com.zauber.commons.dao.Predicate;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcher;
import ar.com.zauber.leviathan.common.async.AsyncUriFetcherObserver;
import ar.com.zauber.leviathan.common.async.FetchQueueAsyncUriFetcher;
import ar.com.zauber.leviathan.common.async.Job;
import ar.com.zauber.leviathan.common.async.JobQueue;
import ar.com.zauber.leviathan.common.async.JobScheduler;
import ar.com.zauber.leviathan.common.async.impl.BlockingQueueJobQueue;
import ar.com.zauber.leviathan.common.async.impl.MultiDomainPoliteJobQueue;
import ar.com.zauber.leviathan.common.async.impl.OutputStreamAsyncUriFetcherObserver;
import ar.com.zauber.leviathan.common.async.impl.RateLimitBlockingQueueJobQueue;
import ar.com.zauber.leviathan.common.fluent.AsyncUriFetcherBuilder.MultiDomainPoliteJobQueueBuilder;
import ar.com.zauber.leviathan.common.fluent.AsyncUriFetcherBuilder.RateLimitJobQueueBuilder;
import ar.com.zauber.leviathan.common.fluent.AsyncUriFetcherBuilder.SchedulerBuilder;
import ar.com.zauber.leviathan.common.fluent.AsyncUriFetcherBuilder.ThreadedExecutorService;
import ar.com.zauber.leviathan.common.mock.FixedURIFetcher;
import ar.com.zauber.leviathan.common.utils.BlockingRejectedExecutionHandler;

/**
 * Fluent creation of fetchers
 *
 *
 * @author Juan F. Codagnone
 * @since Dec 29, 2011
 */
public final class Fetchers {

    /** Creates the Fetchers. */
    private Fetchers() {
        // utility class
    }

    /** @return a fixed uri fetcher usefull for testing  */
    public static FixedFetcherBuilder createFixed() {
        return new DefaultFixedFetcherBuilder();
    }

    /** @return a fixed uri fetcher usefull for testing  */
    public static AsyncUriFetcherBuilder createAsync() {
        return new DefaultAsyncUriFetcherBuilder();
    }
}

/**
 * Implementación default del {@link DefaultFixedFetcherBuilder}
 *
 *
 * @author Juan F. Codagnone
 * @since Dec 29, 2011
 */
final class DefaultFixedFetcherBuilder implements FixedFetcherBuilder {
    private final ThenMockFetcherBuilder then = new ThenMockFetcherBuilder() {
        @Override
        public FixedFetcherBuilder then(final String classpath) {
            map.put(pendingURI, classpath);
            return DefaultFixedFetcherBuilder.this;
        }
    };
    private final Map<URI, String> map = new HashMap<URI, String>();
    private URI pendingURI;
    private Charset ch = Charset.defaultCharset();

    @Override
    public ThenMockFetcherBuilder when(final String uri) {
        pendingURI = URI.create(uri);
        return then;
    }

    @Override
    public ThenMockFetcherBuilder when(final URI uri) {
        pendingURI = uri;
        return then;
    }

    @Override
    public URIFetcher build() {
        return new FixedURIFetcher(map, ch);
    }

    @Override
    public FixedFetcherBuilder register(final String uri, final String classpath) {
        return register(URI.create(uri), classpath);
    }

    @Override
    public FixedFetcherBuilder register(final URI uri, final String classpath) {
        map.put(uri, classpath);
        return this;
    }

    @Override
    public FixedFetcherBuilder withCharset(final String c) {
        ch = Charset.forName(c);
        return this;
    }

    @Override
    public FixedFetcherBuilder withCharset(final Charset c) {
        ch = c;
        return this;
    }
}

/** implementacion estandar */
class DefaultAsyncUriFetcherBuilder implements AsyncUriFetcherBuilder {
    private JobScheduler fetching;
    private JobScheduler processing;
    private AsyncUriFetcherObserver observer;

    @Override
    public AsyncUriFetcherBuilder withFetchingScheduler(final JobScheduler scheduler) {
        fetching = scheduler;
        return this;
    }

    @Override
    public AsyncUriFetcherBuilder withProcessingScheduler(final JobScheduler scheduler) {
        processing = scheduler;
        return this;
    }

    @Override
    public AsyncUriFetcherBuilder withFetchingObserver(final AsyncUriFetcherObserver o) {
        observer = o;
        return this;
    }

    @Override
    public AsyncUriFetcher build() {
        if(fetching == null) {
            withFetchingScheduler().doneScheduler();
            assert fetching != null;
        }
        if(processing == null) {
            withProcessingScheduler()
                .withBloquingQueue(new LinkedBlockingQueue<Job>())
                .withThreadedExecutorServicer()
                    .withCorePoolSize(2)
                    .withMaximumPoolSize(2)
                    .withKeepAliveTimeOut(10, TimeUnit.SECONDS)
                    .doneThreadedExecutorService()
                .withTimer(new Timer(true), 10000)
                .doneScheduler();
            assert processing != null;
        }


        final FetchQueueAsyncUriFetcher ret = new FetchQueueAsyncUriFetcher(fetching, processing);
        if(observer != null) {
            ret.setObserver(observer);
        }
        return ret;
    }

    @Override
    public SchedulerBuilder withFetchingScheduler() {
        return new DefaultSchedulerBuilder(this, new Predicate<JobScheduler>() {
            @Override
            public boolean evaluate(final JobScheduler value) {
                fetching = value;
                return false;
            }
        });
    }

    @Override
    public SchedulerBuilder withProcessingScheduler() {
        return new DefaultSchedulerBuilder(this, new Predicate<JobScheduler>() {
            @Override
            public boolean evaluate(final JobScheduler value) {
                processing = value;
                return false;
            }
        });
    };
};


/** default implementation */
class DefaultSchedulerBuilder implements SchedulerBuilder {
    private final AsyncUriFetcherBuilder returnTo;
    private final Predicate<JobScheduler> onBuild;
    private JobQueue<Job> queue;
    private ExecutorService executorService;
    private Timer timer;
    private int timerTimeout;
    
    /** Creates the DefaultSchedulerBuilder. */
    public DefaultSchedulerBuilder(final AsyncUriFetcherBuilder returnTo, final Predicate<JobScheduler> onBuild) {
        this.returnTo = returnTo;
        this.onBuild = onBuild;
    }
    
    @Override
    public SchedulerBuilder withQueue(final JobQueue<Job> q) {
        queue = q;
        return this;
    }

    @Override
    public SchedulerBuilder withExecutorService(final ExecutorService e) {
        executorService = e;
        return this;
    }

    @Override
    public MultiDomainPoliteJobQueueBuilder withPoliteQueue() {
        return new MultiDomainPoliteJobQueueBuilder() {
            private final Collection<String> excludedDomains = new HashSet<String>();
            private long domainDelay = 1000;
            private TimeUnit unit = TimeUnit.MILLISECONDS;

            @Override
            public MultiDomainPoliteJobQueueBuilder withExceptions(final Iterable<String> exceptions) {
                for(final String exeption : exceptions) {
                    excludedDomains.add(exeption);
                }
                return this;
            }

            @Override
            public MultiDomainPoliteJobQueueBuilder withException(final String exception) {
                excludedDomains.add(exception);
                return this;
            }

            @Override
            public MultiDomainPoliteJobQueueBuilder minimalDelay(
                    final long domainDelay, final TimeUnit unit) {
                this.domainDelay = domainDelay;
                this.unit = unit;

                return this;
            }

            @Override
            public SchedulerBuilder donePoliteQueue() {
                queue = new MultiDomainPoliteJobQueue(domainDelay, unit);
                return DefaultSchedulerBuilder.this;
            }
        };
    }

    @Override
    public RateLimitJobQueueBuilder withRateLimitQueue() {
        return new RateLimitJobQueueBuilder() {
            private BlockingQueue<Job> target;
            private long throwlingInMs = 1000;
            private long poolingTimeout = 500;

            @Override
            public RateLimitJobQueueBuilder withTargetQueue(final BlockingQueue<Job> target) {
                this.target = target;
                return this;
            }

            @Override
            public RateLimitJobQueueBuilder withThrowling(final long throwlingInMs) {
                this.throwlingInMs = throwlingInMs;
                return this;
            }

            @Override
            public RateLimitJobQueueBuilder withPoolingTimeout(final long poolingTimeout) {
                this.poolingTimeout = poolingTimeout;
                return this;
            }

            @Override
            public SchedulerBuilder doneRateLimitQueue() {
                if(target == null) {
                    target = new LinkedBlockingQueue<Job>();
                }
                queue = new RateLimitBlockingQueueJobQueue<Job>(target, poolingTimeout, throwlingInMs);
                return DefaultSchedulerBuilder.this;
            }
        };
    }

    @Override
    public SchedulerBuilder withBloquingQueue(final LinkedBlockingQueue<Job> target) {
        queue = new BlockingQueueJobQueue<Job>(target);
        return this;
    }

    @Override
    public ThreadedExecutorService withThreadedExecutorServicer() {
        return new ThreadedExecutorService() {
            private int maxPoolSize = 10;
            private int corePoolSize = 5;
            private int timeout = 10;
            private TimeUnit timeoutUnit = TimeUnit.SECONDS;
            
            @Override
            public ThreadedExecutorService withMaximumPoolSize(final int i) {
                maxPoolSize = i;
                return this;
            }
            
            @Override
            public ThreadedExecutorService withCorePoolSize(final int i) {
                corePoolSize = i;
                return this;
            }

            @Override
            public ThreadedExecutorService withKeepAliveTimeOut(final int i, final TimeUnit unit) {
                this.timeout = i;
                this.timeoutUnit = unit; 
                return this;
            }


            @Override
            public SchedulerBuilder doneThreadedExecutorService() {
                executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, timeout, timeoutUnit,
                        new SynchronousQueue<Runnable>(), new BlockingRejectedExecutionHandler());
                return DefaultSchedulerBuilder.this;
            }
        };
    }

    @Override
    public SchedulerBuilder withTimer(final Timer t, final int i) {
        this.timer = t;
        timerTimeout = i;
        
        return this;
    }

    @Override
    public AsyncUriFetcherBuilder doneScheduler() {
        if(executorService == null) {
            withThreadedExecutorServicer().doneThreadedExecutorService();
            assert executorService != null;
        }
        if(queue == null) {
            withPoliteQueue().donePoliteQueue();
            assert queue != null;
        }
        if(timer == null) {
            onBuild.evaluate(new JobScheduler(queue, executorService));
        } else {
            onBuild.evaluate(new JobScheduler(queue, executorService, timer, timerTimeout));
        }
        return returnTo;
    }

}