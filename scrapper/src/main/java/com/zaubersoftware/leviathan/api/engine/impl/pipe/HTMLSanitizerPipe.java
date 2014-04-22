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
package com.zaubersoftware.leviathan.api.engine.impl.pipe;

import java.io.PrintWriter;
import java.io.Reader;

import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import com.zaubersoftware.leviathan.api.engine.Pipe;

import ar.com.zauber.commons.validate.Validate;
import ar.com.zauber.leviathan.api.URIFetcherResponse;

/**
 * A {@link Pipe} that sanitizes an HTML document using {@link Tidy}.
 * 
 * @author Guido Marucci Blas
 * @since Aug 12, 2011
 */
public final class HTMLSanitizerPipe implements Pipe<URIFetcherResponse, Node> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TidyFactory tidyFactory;

    /** Creates the HTMLSanitizerPipe */
    public HTMLSanitizerPipe(final TidyFactory tidy) {
        Validate.notNull(tidy);
        this.tidyFactory = tidy;
    }

    /**
     * Creates the HTMLSanitizerPipe with a default configuration for {@link Tidy}
     */
    public HTMLSanitizerPipe() {
        this.tidyFactory = new TidyFactory() {
            @Override
            public Tidy newInstance() {
                final Tidy tidy = new Tidy();
                tidy.setQuiet(true);
                tidy.setShowWarnings(false);
                tidy.setXmlOut(true);
                tidy.setErrfile("foo");
                tidy.setOnlyErrors(true);
                tidy.setErrout(new PrintWriter(new NullWriter()));
                tidy.setInputEncoding("utf-8");
                return tidy;
            }
        };
    }

    @Override
    public Node execute(final URIFetcherResponse response) {
        Validate.notNull(response, "The response cannot be null");
        // TODO Move response status code validation to a previous pipe
        if (response.isSucceeded()) {
            final Reader input = response.getHttpResponse().getContent();
            return tidyFactory.newInstance().parseDOM(input, new NullWriter());
        } else {
            logger.error("Fetch did not succeded: " + response.getURIAndCtx().getURI());
            // TODO
            throw new NotImplementedException("Handle exception with context stack handlers",
                    response.getError());
        }
    }
}
