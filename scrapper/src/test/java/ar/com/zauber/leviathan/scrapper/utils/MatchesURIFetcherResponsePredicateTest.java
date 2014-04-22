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
package ar.com.zauber.leviathan.scrapper.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.util.UriTemplate;

import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.api.URIFetcherResponse.URIAndCtx;

/**
 * 
 * 
 * @author Marcelo Turrin
 * @since Sep 10, 2010
 */
public class MatchesURIFetcherResponsePredicateTest {

    /**
     * Chequea que el matcheo para url exacta
     * @throws URISyntaxException
     */
    @Test
    public final void testExactMatch() throws URISyntaxException {
        final String uriTemplate = "http://www.zaubersoftware.com";

        UriTemplate template = new UriTemplate(uriTemplate);
        
        MatchesURIFetcherResponsePredicate predicate 
                = new MatchesURIFetcherResponsePredicate(template);
        
        final URIAndCtx ctx = Mockito.mock(URIAndCtx.class);
        Mockito.when(ctx.getURI()).thenReturn(new URI(uriTemplate));
        
        final URIFetcherResponse response = Mockito.mock(URIFetcherResponse.class);
        Mockito.when(response.getURIAndCtx()).thenReturn(ctx);

        Assert.assertFalse(predicate.evaluate(null));
        Assert.assertTrue(predicate.evaluate(response));
        
        Mockito.when(ctx.getURI()).thenReturn(new URI("http://www.zauber.com.ar"));

        Assert.assertFalse(predicate.evaluate(response));
    }
     
}
