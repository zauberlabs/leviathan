/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.UnhandledException;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.commons.validate.Validate;
import ar.com.zauber.leviathan.api.AsyncUriFetcher;
import ar.com.zauber.leviathan.api.URIFetcherHttpResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * A closure that will follow the redirects storing in the context
 * the original URL and final URL.
 *
 * @author Guido Marucci Blas
 * @since Feb 9, 2011
 */
public final class RedirectClosure implements Closure<URIFetcherResponse> {

    /**
     * Key for the context map to store the number of hops in the redirection.
     */
    public static final String KEY_HOPS = 
        RedirectClosure.class.getCanonicalName() +  "-redirect-hops";
    
    /**
     * Key for the context map to store the original URL.
     */
    public static final String KEY_ORIGINAL_URL  = 
        RedirectClosure.class.getCanonicalName() +  "-redirect-original-url";
    
    private final AsyncUriFetcher uriFetcher;
    private final Closure<URIFetcherResponse> onSuccessTarget;
    private final Closure<URIFetcherResponse> onErrorTarget;
    private final Closure<URIFetcherResponse> onRedirectTarget;
    private final int maxHops;
    
    /**
     * Creates the RedirectClosure.
     *
     * @param uriFetcher an implementation of 
     * <code>{@link AsyncUriFetcher}</code>. Must not be null.
     * @param onSuccessTarget of the <code>{@link Closure}</code> once the redirects
     * have been succesfully followed. Must not be null.
     * @param onErrorTarget a <code>{@link Closure}</code> that will be called
     * if the response could not succeed. Must no be null.
     * @param onRedirectTarget a <code>{@link Closure}</code> that will be called
     * if redirect status code is returned in the response. Must not be null.
     * @param maxHops the maximum anount of redirect hops. Must be a positive
     * number.
     */
    public RedirectClosure(final AsyncUriFetcher uriFetcher, 
                           final Closure<URIFetcherResponse> onSuccessTarget, 
                           final Closure<URIFetcherResponse> onErrorTarget,
                           final Closure<URIFetcherResponse> onRedirectTarget,
                           final int maxHops) {
        Validate.notNull(uriFetcher);
        Validate.notNull(onSuccessTarget);
        Validate.notNull(onErrorTarget);
        Validate.notNull(onRedirectTarget);
        if (maxHops < 1) {
            throw new IllegalArgumentException(String.format(
                    "The maximum amount of hops must be a positive number. " 
                    + "The given max hops was %d", maxHops));
        }
        
        this.uriFetcher = uriFetcher;
        this.onSuccessTarget = onSuccessTarget;
        this.maxHops = maxHops;
        this.onErrorTarget  = onErrorTarget;
        this.onRedirectTarget = onRedirectTarget;
    }


    @Override
    public void execute(final URIFetcherResponse t) {
        if(t.isSucceeded()) {
            
            final URIFetcherHttpResponse r = t.getHttpResponse();
            final int code = r.getStatusCode();
            
            final String originalURI = (String) t.getURIAndCtx().getCtx()
                .get(KEY_ORIGINAL_URL);
            final Map<String, Object> ctx = new HashMap<String, Object>(
                    t.getURIAndCtx().getCtx());
            
            if (originalURI == null) {
                ctx.put(KEY_ORIGINAL_URL, t.getURIAndCtx().getURI().toString());
            }
            
            if(code == 301 || code == 302 || code == 303) {
                
                final String fowardAddress = r.getHeader("Location");
                if(fowardAddress == null) {
                    throw new IllegalStateException(String.format(
                            "Parsing redict for %s. Status code was %s"
                         + " but header location was not provided",
                         t.getURIAndCtx().getURI(), code));
                }
                
                try {
                    final URI uri = new URI(fowardAddress);
                    Integer hops = (Integer) ctx.get(KEY_HOPS);
                    
                    if (hops == null) {
                        hops = 1;
                    } else {
                        if(hops == maxHops) {
                            throw new MaxRedirectHopsException(hops, maxHops);
                        }
                        hops = hops + 1; 
                    }
                    
                    ctx.put(KEY_HOPS, hops);
                    uriFetcher.get(new InmutableURIAndCtx(uri, ctx), 
                            onRedirectTarget);
                } catch (final URISyntaxException e) {
                    throw new UnhandledException(e);
                }
        
            } else {
                onSuccessTarget.execute(t);
            }
            
        } else {
            onErrorTarget.execute(t);
        }

    }

}
