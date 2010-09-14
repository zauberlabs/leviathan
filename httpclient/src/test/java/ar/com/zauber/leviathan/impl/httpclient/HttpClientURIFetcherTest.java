/**
 * Copyright (c) 2009-2010 Zauber S.A. <http://www.zaubersoftware.com/>
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
package ar.com.zauber.leviathan.impl.httpclient;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;

import ar.com.zauber.leviathan.api.URIFetcherResponse;
import ar.com.zauber.leviathan.common.InmutableURIAndCtx;

/**
 * Prueba de fetch
 * 
 * @author Pablo Martin Grigolatto
 * @since Sep 14, 2010
 */
public class HttpClientURIFetcherTest {

    private static final String ESTA_ES_LA_RESPUESTA = "esta es la respuesta";

    /** prueba el content type */
    @Test
    public final void testCharset() throws Exception {
        final HttpClient httpClient = mock(HttpClient.class);
        final URI uri = new URI("http://www.zaubersoftware.com"); 
        final InmutableURIAndCtx ctx = new InmutableURIAndCtx(uri);
        
        final StringEntity rta = new StringEntity(ESTA_ES_LA_RESPUESTA, "utf-8");
        final BasicHttpResponse httpResponse 
            = new BasicHttpResponse(mock(StatusLine.class));
        httpResponse.setEntity(rta);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);
        
        HTTPClientURIFetcher fetcher = new HTTPClientURIFetcher(httpClient);
        URIFetcherResponse ufr = fetcher.get(ctx);
        
        final Reader reader = ufr.getHttpResponse().getContent();
        final StringWriter output = new StringWriter();
        IOUtils.copy(reader, output);
        
        Assert.assertEquals(ESTA_ES_LA_RESPUESTA, output.toString());
    }
    
}
