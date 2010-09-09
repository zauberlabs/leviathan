
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

package ar.com.zauber.leviathan.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;



/**
 * UrlEncodedPostBody: body to post that also handles {@link List}'s parameters.
 * 
 * 
 * @author Mariano Focaraccio
 * @since Sep 9, 2010
 */

public class UrlEncodedPostBody {

    private static final String EMPTY_VALUE = "";
    private Map<String, String> simpleParameters;
    private Map<String, List<String>> listParameters; 
    
    /**
     * 
     * Creates the UrlEncodedPostBody.
     *
     */
    public UrlEncodedPostBody() {
        simpleParameters = new HashMap<String, String>();
        listParameters = new HashMap<String, List<String>>();
    }
    
    /**
     * To add a string parameter
     * @param name
     * @param value
     */
    public final void addSimpleParameter(final String name, final String value) {
        Validate.isTrue(!name.isEmpty());
        simpleParameters.put(name, value);
    }

    /**
     * @param name
     * @param parameter
     */
    public final void addCollectionParameter(final String name, 
                                       final List<String> parameter) {
        Validate.isTrue(!name.isEmpty());
        listParameters.put(name, parameter);
    }

    /** 
     * @return the simple parameter's name
     */
    public final List<String> getSimpleParameters() {
        final List<String> parameters = new ArrayList<String>();
        for (Entry<String, String> entry : simpleParameters.entrySet()) {
            parameters.add(entry.getKey());
        }
        return parameters;
    }

    /**
     * @param name
     * @return the parameter with that name
     */
    public final String getSimpleParameter(final String name) {
        return simpleParameters.containsKey(name) 
                        ? simpleParameters.get(name) : EMPTY_VALUE;
    }

    /**
     * @return the list parameter's name
     */
    public final List<String> getCollectionParameters() {
        final List<String> parameters = new ArrayList<String>();
        for (Entry<String, List<String>> entry : listParameters.entrySet()) {
            parameters.add(entry.getKey());
        }
        return parameters;
    }

    /**
     * @param name
     * @return the parameter with that name
     */
    public final List<String> getCollectionParameter(final String name) {
        return listParameters.containsKey(name) 
                     ? listParameters.get(name) : new ArrayList<String>();
    }


}

