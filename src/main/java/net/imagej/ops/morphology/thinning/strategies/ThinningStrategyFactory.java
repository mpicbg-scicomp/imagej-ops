/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, University of Konstanz and Brian Northan.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.ops.morphology.thinning.strategies;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Andreas
 */
public class ThinningStrategyFactory {

    private boolean m_foreground;

    public ThinningStrategyFactory(final boolean foreground)
    {
        m_foreground = foreground;
    }

    public static enum Strategy {

        MORPHOLOGICAL, HILDITCH, ZHANGSUEN, GUOHALL;

        public static List<String> getNames() {
            Strategy[] algorithms = values();
            List<String> names = new LinkedList<String>();

            for (int i = 0; i < algorithms.length; ++i) {
                names.add(algorithms[i].toString());
            }
            return names;

        }

        @Override
        public String toString() {
            switch (this) {
                case MORPHOLOGICAL:
                    return "Morphological Thinning";
                case HILDITCH:
                    return "Hilditch Algorithm";
                case ZHANGSUEN:
                    return "Zhang-Suen Algorithm";
                case GUOHALL:
                    return "Guo-Hall Algorithm";
                default:
                    throw new IllegalArgumentException();
            }
        }
    };

    public ThinningStrategy getStrategy(final Strategy strategy) {
        switch (strategy) {
            case MORPHOLOGICAL:
                return new MorphologicalThinning(m_foreground);
            case HILDITCH:
                return new HilditchAlgorithm(m_foreground);
            case ZHANGSUEN:
                return new ZhangSuenAlgorithm(m_foreground);
            case GUOHALL:
                return new GuoHallAlgorithm(m_foreground);
            default:
                return new MorphologicalThinning(m_foreground);
        }

    }

    public ThinningStrategy getStrategy(final String strategy) {
        for(Strategy s: Strategy.values())
        {
            if (s.toString().equals(strategy)) {
                return getStrategy(s);
            }
        }
        return new MorphologicalThinning(m_foreground);
    }
}
