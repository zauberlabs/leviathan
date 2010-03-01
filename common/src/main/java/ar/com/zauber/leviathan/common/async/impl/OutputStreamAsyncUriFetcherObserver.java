package ar.com.zauber.leviathan.common.async.impl;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.async.AsyncUriFetcherObserver;
import ar.com.zauber.leviathan.common.async.FetchQueueAsyncUriFetcher;

/**
 * Muestra el estado de la cola
 * 
 * @author Juan F. Codagnone
 * @since Feb 27, 2010
 */
public class OutputStreamAsyncUriFetcherObserver implements
        AsyncUriFetcherObserver {
    private AsyncUriFetcherObserver target;
    private PrintStream out = System.out;
    private AtomicLong queueDownloads = new AtomicLong(0);
    private AtomicInteger activeDownloads = new AtomicInteger(0);
    private AtomicLong queueProcess = new AtomicLong(0);
    private AtomicInteger activeProcess = new AtomicInteger(0);
    private AtomicLong done = new AtomicLong(0);

    /** OutputStreamAsyncUriFetcherObserver. */
    public OutputStreamAsyncUriFetcherObserver() {
        this(System.out, new NullAsyncUriFetcherObserver());
    }
    
    /** OutputStreamAsyncUriFetcherObserver. */
    public OutputStreamAsyncUriFetcherObserver(
            final AsyncUriFetcherObserver target) {
        this(System.out, target);
    }
    
    /** OutputStreamAsyncUriFetcherObserver. */
    public OutputStreamAsyncUriFetcherObserver(final PrintStream out,
            final AsyncUriFetcherObserver target) {
        Validate.notNull(out);
        Validate.notNull(target);
        
        this.out = out;
        this.target = target;
    }

    /** @see AsyncUriFetcherObserver#newFetch(URIFetcherResponse.URIAndCtx) */
    public final void newFetch(final URIFetcherResponse.URIAndCtx uriAndCtx) {
        queueDownloads.incrementAndGet();
        target.newFetch(uriAndCtx);
        update();
    }

    /** @see AsyncUriFetcherObserver#beginFetch(URIAndCtx) */
    public final void beginFetch(final URIFetcherResponse.URIAndCtx uriAndCtx) {
        queueDownloads.decrementAndGet();
        activeDownloads.incrementAndGet();
        target.beginFetch(uriAndCtx);
        update();
    }

    /** @see AsyncUriFetcherObserver#finishFetch(URIAndCtx, long) */
    public final void finishFetch(final URIFetcherResponse.URIAndCtx uriAndCtx, 
            final long elapsed) {
        activeDownloads.decrementAndGet();
        queueProcess.incrementAndGet();
        target.finishFetch(uriAndCtx, elapsed);
        update();
    }

    /** @see AsyncUriFetcherObserver#beginProcessing(URIAndCtx) */
    public final void beginProcessing(final URIFetcherResponse.URIAndCtx uriAndCtx) {
        queueProcess.decrementAndGet();
        activeProcess.incrementAndGet();
        target.beginProcessing(uriAndCtx);
        update();
    }

    /** @see AsyncUriFetcherObserver#finishProcessing(URIAndCtx, long) */
    public final void finishProcessing(final URIFetcherResponse.URIAndCtx uriAndCtx,
            final long elapsed) {
        activeProcess.decrementAndGet();
        done.incrementAndGet();
        target.finishProcessing(uriAndCtx, elapsed);
        update();
    }

    /** actualiza */
    private void update() {
        final long f1 = queueDownloads.get();
        final long f2 = activeDownloads.get();
        final long p1 = queueProcess.get();
        final long p2 = activeProcess.get();
        final long todo = f1 + f2 + p1 + p2;
        final long ndone = done.get();

        out.printf(
      "%3.0f%%\t(%05d queued %3d active | %05d queued %3d active) %d done   \r",
      (todo + ndone == 0) ? 0 : ((double) ndone / (todo + ndone) * 100.0), 
              f1, f2, p1, p2, ndone);
    }
}