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
package com.zaubersoftware.leviathan.api.engine.impl;

/**
 * A Mock {@link RuntimeException}
 *
 * @author Martin Silva
 * @since Sep 2, 2011
 */
public final class MockException extends RuntimeException {

    /** <code>serialVersionUID</code> */
    private static final long serialVersionUID = 3200379173102048867L;

    /**
     * Creates the MockException.
     *
     */
    public MockException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates the MockException.
     *
     * @param message
     * @param cause
     */
    public MockException(final String message, final Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates the MockException.
     *
     * @param message
     */
    public MockException(final String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates the MockException.
     *
     * @param cause
     */
    public MockException(final Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }


}
