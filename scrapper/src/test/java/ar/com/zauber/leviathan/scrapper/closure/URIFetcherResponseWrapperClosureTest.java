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
package ar.com.zauber.leviathan.scrapper.closure;

import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.UnhandledException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import ar.com.zauber.commons.dao.Closure;
import ar.com.zauber.leviathan.api.URIFetcherHttpResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;
import ar.com.zauber.leviathan.scrapper.utils.ContextualResponse;

/**
 * 
 * 
 * @author Marcelo Turrin
 * @since Sep 10, 2010
 */
@SuppressWarnings("unchecked")
public class URIFetcherResponseWrapperClosureTest {

    /**
     * Test de comportamiento correcto cada vez que se lo llama con todo debe
     * llamar al target
     */
    @Test
    public final void testBehaviour() {
        URIAndCtx ctx = Mockito.mock(URIAndCtx.class);
        URIFetcherHttpResponse httpResponse 
                = Mockito.mock(URIFetcherHttpResponse.class);
        Mockito.when(httpResponse.getContent()).thenReturn(new StringReader(""));

        URIFetcherResponse response = Mockito.mock(URIFetcherResponse.class);
        Mockito.when(response.isSucceeded()).thenReturn(Boolean.TRUE);
        Mockito.when(response.getHttpResponse()).thenReturn(httpResponse);
        Mockito.when(response.getURIAndCtx()).thenReturn(ctx);
        
        Closure<ContextualResponse<URIAndCtx, Reader>> target 
                    = Mockito.mock(Closure.class);

        Closure<URIFetcherResponse> closure 
                = new URIFetcherResponseWrapperClosure(target);

        closure.execute(response);

        Mockito.verify(httpResponse).getContent();
        Mockito.verify(response).isSucceeded();
        Mockito.verify(response).getHttpResponse();
        Mockito.verify(response).getURIAndCtx();
        Mockito.verify(target).execute(Mockito.any(ContextualResponse.class));
    }

    /** Test que se fija que halla unhandled cuando la respuesta no es correcta  */
    @Test
    public final void testUnsuccesfullResponse() throws URISyntaxException {
        
        URIFetcherResponse response = null;
        Closure<ContextualResponse<URIAndCtx, Reader>> target = null;
        try {
            URIAndCtx ctx = Mockito.mock(URIAndCtx.class);
            Mockito.when(ctx.getURI()).thenReturn(new URI("http://www.zbr.com"));
            URIFetcherHttpResponse httpResponse 
                    = Mockito.mock(URIFetcherHttpResponse.class);
            Mockito.when(httpResponse.getStatusCode()).thenReturn(403);
            
            response = Mockito.mock(URIFetcherResponse.class);
            Mockito.when(response.isSucceeded()).thenReturn(Boolean.FALSE);
            Mockito.when(response.getURIAndCtx()).thenReturn(ctx);
            Mockito.when(response.getHttpResponse()).thenReturn(httpResponse);
            final Exception ex = Mockito.mock(IllegalArgumentException.class);
            Mockito.when(response.getError()).thenReturn(ex);
            
            target = Mockito.mock(Closure.class);

            Closure<URIFetcherResponse> closure 
                    = new URIFetcherResponseWrapperClosure(target);

            closure.execute(response);
            Assert.fail();
        } catch (UnhandledException e) {
            Assert.assertTrue(IllegalArgumentException.class
                    .isAssignableFrom(e.getThrowable(1).getClass()));
            Mockito.verify(response).isSucceeded();
            Mockito.verify(response).getURIAndCtx();
            Mockito.verify(target, new Times(0)).execute(
                    Mockito.any(ContextualResponse.class));

        }
    }

    /** Simula un error en la cadena de closures 
     * @throws URISyntaxException  */
    @Test
    public final void testErrorEnCadena() throws URISyntaxException {
        URIFetcherResponse response = null;
        Closure<ContextualResponse<URIAndCtx, Reader>> target = null;
        final String message = "no se pudo";
        try {
            URIAndCtx ctx = Mockito.mock(URIAndCtx.class);
            Mockito.when(ctx.getURI()).thenReturn(new URI("http://www.zbr.com"));
            URIFetcherHttpResponse httpResponse 
                    = Mockito.mock(URIFetcherHttpResponse.class);
            Mockito.when(httpResponse.getContent()).thenReturn(new StringReader(""));
            Mockito.when(httpResponse.getStatusCode()).thenReturn(403);
            
            response = Mockito.mock(URIFetcherResponse.class);
            Mockito.when(response.isSucceeded()).thenReturn(Boolean.TRUE);
            Mockito.when(response.getURIAndCtx()).thenReturn(ctx);
            Mockito.when(response.getHttpResponse()).thenReturn(httpResponse);
            
            target = Mockito.mock(Closure.class);

            Mockito.doThrow(new RuntimeException(message)).when(target)
                    .execute(Mockito.any(ContextualResponse.class));
            
            Closure<URIFetcherResponse> closure 
                    = new URIFetcherResponseWrapperClosure(target);

            closure.execute(response);
            Assert.fail();
        } catch (UnhandledException e) {
            
            final Throwable throwable = e.getThrowable(1);
            Assert.assertEquals(RuntimeException.class, throwable.getClass());
            Assert.assertEquals(message, throwable.getMessage());
            Mockito.verify(response).isSucceeded();

        }
        
    }
}
