package ar.com.zauber.leviathan.impl.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;

/**
 * Para que todas las páginas salgan con gzip. 
 * 
 * @author Juan F. Codagnone
 * @since Feb 27, 2010
 */
public class GZipInterceptor implements HttpRequestInterceptor, 
                                        HttpResponseInterceptor {
    
    /** @see HttpRequestInterceptor#process(HttpRequest, HttpContext) */
    public final void process(final HttpRequest request, final HttpContext context) 
         throws HttpException, IOException {
        if (!request.containsHeader("Accept-Encoding")) {
            request.addHeader("Accept-Encoding", "gzip");
        }
    }

    /** @see HttpResponseInterceptor#process(HttpResponse, HttpContext) */
    public final void process(final HttpResponse response, 
            final HttpContext context) throws HttpException, IOException {
        final HttpEntity entity = response.getEntity();
        final Header ceheader = entity.getContentEncoding();
        if (ceheader != null) {
            final HeaderElement[] codecs = ceheader.getElements();
            for (int i = 0; i < codecs.length; i++) {
                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                    response.setEntity(new GzipDecompressingEntity(
                            response.getEntity())); 
                    return;
                }
            }
        }
    }
    
}

/**
 * GZIP Entity
 * 
 * @author Juan F. Codagnone
 * @since Feb 27, 2010
 */
class GzipDecompressingEntity extends HttpEntityWrapper {

    /** constructor */
    public GzipDecompressingEntity(final HttpEntity entity) {
        super(entity);
    }

    /** @see HttpEntityWrapper#getContent() */
    @Override
    public final InputStream getContent() throws IOException, IllegalStateException {

        // the wrapped entity's getContent() decides about repeatability
        InputStream wrappedin = wrappedEntity.getContent();

        return new GZIPInputStream(wrappedin);
    }

    /** @see HttpEntityWrapper#getContentLength() */
    @Override
    public final long getContentLength() {
        // length of ungzipped content is not known
        return -1;
    }
} 
