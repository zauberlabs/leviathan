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

package ar.com.zauber.leviathan.impl.httpclient.charset;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import ar.com.zauber.leviathan.common.CharsetStrategy;
import ar.com.zauber.leviathan.common.ResponseMetadata;

/**
 * Implementación de {@link CharsetStrategy} que determina el {@link Charset}
 * a utilizarse de acuerdo a la "votacion" de un conjunto de estrategias
 * que recibe como parámetro (los voters).
 * <p>
 * 
 * <h3>Consideraciones:</h3>
 * 
 * <ol>
 * <li> En caso de que un voter devuelva <code>null</code>, implica que se 
 * abstiene de votar en esta votación.
 * 
 * <li> Con los votos obtenidos, se elige aquella estrategia que cuente con mayor
 * cantidad de votos. En el caso de que haya un empate entre 2 o más estrategias,
 * se establece un orden de prioridad definida por el ORDEN de los voters.
 * Esto quiere decir, que el orden en que se recibe el listado de voters, 
 * determina el orden de prioridad en caso de empate, siendo el mismo de 
 * mayor a menor. 
 * </ol>
 * 
 * @throws UnsupportedCharsetException en caso de que el charset default no 
 * sea válido.
 *
 * @author Mariano Focaraccio
 * @since Sep 16, 2010
 */
public class VotersCharsetStrategy implements CharsetStrategy {

   private List<CharsetStrategy> voters;
   private Charset defaultCharset;
    
    /** Creates the VotersCharsetStrategy. */
    public VotersCharsetStrategy(final List<CharsetStrategy> voters,
            final String charset) {
        super();
        this.voters = voters;
        Validate.notNull(charset);
        this.defaultCharset = Charset.forName(charset);
    }

    /** @see CharsetStrategy#getCharset(ResponseMetadata, InputStream) */
    public final Charset getCharset(final ResponseMetadata meta, 
                                   final InputStream content) {
        final List<Charset> votes = new ArrayList<Charset>(); 
        for (CharsetStrategy voter : voters) {
            votes.add(voter.getCharset(meta, content));
        }
        if (votes.isEmpty()) {
            return defaultCharset;
        } else {
            return voterWinner(votes);
        }
    }

    /** Determina el charset ganador de acuerdo a los votos recibidos  */
    private Charset voterWinner(final List<Charset> votes) {
        final Map<Charset, Integer> result = new HashMap<Charset, Integer>();
        final Set<Charset> winners = new HashSet<Charset>();
        int maxVotes = 0;
        
        //Sumo los votos
        for (final Charset vote : votes) {
            int newCount;
            if (result.containsKey(vote)) {
                newCount = result.get(vote) + 1;
            } else {
                newCount = 1;
            }
            if (maxVotes == newCount) {
                winners.add(vote);
            } else if (maxVotes < newCount) {
                maxVotes = newCount;
                winners.clear();
                winners.add(vote);
            }
            result.put(vote, newCount);
        }
        Charset winner = null;
        if (winners.size() > 0) {
            for (final Charset charset : votes) {
                if (winners.contains(charset)) {
                    winner = charset;
                }
            }
        } else {
            winner = winners.iterator().next();
        }
        return winner;
    }

}

