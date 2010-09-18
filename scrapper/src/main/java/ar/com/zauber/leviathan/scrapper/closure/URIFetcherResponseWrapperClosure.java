/*
 * Copyright (c) 2010 Zauber S.A. -- All rights reserved
 */
package ar.com.zauber.leviathan.scrapper.closure;

import java.io.Reader;
import java.util.Formatter;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.Validate;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.scrapper.utils.ContextualResponse;

/**
 * 
 * 
 * @author Marcelo Turrin
 * @since Sep 10, 2010
 */
public class URIFetcherResponseWrapperClosure implements
        Closure<URIFetcherResponse> {
    private final Closure<ContextualResponse<URIAndCtx, Reader>> target;

    /** Crea el closure con target el closure pasado */
    public URIFetcherResponseWrapperClosure(
            final Closure<ContextualResponse<URIAndCtx, Reader>> target) {
        Validate.notNull(target);
        this.target = target;
    }

    /** @see Closure#execute(Object) */
    @Override
    public final void execute(final URIFetcherResponse response) {
        try {
            if (response.isSucceeded()) {
                target.execute(new ContextualResponse<URIAndCtx, Reader>(response
                        .getURIAndCtx(), response.getHttpResponse().getContent()));
            } else {
                throw response.getError();
            }
        } catch (Throwable e) {
            Formatter formatter = new Formatter();
            formatter.format(
                "Error procesando %s", response.getURIAndCtx().getURI());
            throw new UnhandledException(formatter.toString(), e);
        }
    }

}
