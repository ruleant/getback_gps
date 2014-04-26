/**
 * Implementation of a low pass filter.
 *
 * Copyright (C) 2014 Dieter Adriaenssens
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @package com.github.ruleant.getback_gps.lib
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

/**
 * Provides methods to apply a low pass filter to a single value
 * or to a set of values.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LowPassFilter {
    /**
     * Hidden constructor, to prevent instantiating.
     */
    protected LowPassFilter() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }

    /**
     * Implements a low pass filter, topping of high frequency changes,
     * reducing the jumpiness of the signal.
     *
     * @param previousValue previous sensor value
     * @param newValue new sensor value
     * @param alpha Alpha value of low pass filter (valid range : 0-1)
     * @return filtered value
     */
    public static float filterValue(
            final float previousValue, final float newValue,
            final float alpha) {
        // check alpha value range
        if (alpha > 1 || alpha < 0) {
            throw new IllegalArgumentException(
                    "parameter alpha is not in range 0.0 .. 1.0");
        }

        return previousValue + alpha * (newValue - previousValue);
    }

    /**
     * Runs a low pass filter on an array of unrelated values in parallel.
     *
     * There is no relation between the values,
     * this method passes a set of separate signals in parallel.
     *
     * Not be confused by an array of values from the same sensor (FIFO),
     * where the result of passing each value in an array is influenced
     * by the result of the previous value.
     *
     * @param previousArray array of previous values
     * @param newArray array of current values
     * @param alpha Alpha value of low pass filter (valid range : 0-1)
     * @return array with filtered values.
     */
    public static float[] filterValueSet(
            final float[] previousArray, final float[] newArray,
            final float alpha) {
        // newArray should not be empty
        if (newArray == null || newArray.length == 0) {
            throw new IllegalArgumentException(
                    "parameter newArray should not be an empty array");
        }

        float[] returnArray = new float[newArray.length];

        if (previousArray == null) {
            return newArray;
        }

        // previousArray should have the same size as newArray
        if (newArray.length != previousArray.length) {
            throw new IllegalArgumentException(
                String.format(
                    "parameter previousArray (length = %1$d) should have the "
                        + "same size as parameter newArray (length = %2$d)",
                    previousArray.length,
                    newArray.length));
        }

        for (int i = 0; i < newArray.length; i++) {
            returnArray[i] = filterValue(
                    previousArray[i], newArray[i], alpha);
        }

        return returnArray;
    }
}
