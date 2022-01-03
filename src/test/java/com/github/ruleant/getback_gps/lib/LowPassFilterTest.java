/**
 * Unit tests for LowPassFilter class
 *
 * Copyright (C) 2014-2021 Dieter Adriaenssens
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
 * @package com.github.ruleant.getback_gps
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LowPassFilterTest {
    /**
     * Value for alpha parameter.
     */
    private static final float ALPHA_VALUE = 0.6f;

    /**
     * Accuracy.
     */
    private static final double ACCURACY = 0.0001;

    /**
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        Assertions.assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 0), ACCURACY);
        Assertions.assertEquals(
                0.0f,
                LowPassFilter.filterValueSet(new float[1], new float[1], 0)[0],
                ACCURACY);
    }

    /**
     * Tests range of alpha parameter.
     */
    @Test
    public final void testAlphaParameterRange() {
        // valid range for parameter alpha
        Assertions.assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 0), ACCURACY);
        Assertions.assertEquals(
                0.0f,
                LowPassFilter.filterValue(0, 0, ALPHA_VALUE),
                ACCURACY);
        Assertions.assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 1), ACCURACY);
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // invalid range for parameter alpha
            LowPassFilter.filterValue(0, 0, -1);
        });
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // invalid range for parameter alpha
            LowPassFilter.filterValue(0, 0, -1 * 2);
        });
    }

    /**
      * Tests out of range value, bigger than highest allowed value.
      */
    @Test
    public final void testOutOfRangeValueBigger() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            LowPassFilter.filterValue(0, 0, 2);
        });
    }

    /**
     * Tests filterValue() method.
     */
    @Test
    public final void testFilterValue() {
        // value changes from 0 to 1, with different alpha values
        Assertions.assertEquals(0.0f, LowPassFilter.filterValue(0, 1, 0), ACCURACY);
        Assertions.assertEquals(
                ALPHA_VALUE,
                LowPassFilter.filterValue(0, 1, ALPHA_VALUE),
                ACCURACY);
        Assertions.assertEquals(1.0f, LowPassFilter.filterValue(0, 1, 1), ACCURACY);

        // value changes from 1 to 0, with different alpha values
        Assertions.assertEquals(1.0f, LowPassFilter.filterValue(1, 0, 0), ACCURACY);
        Assertions.assertEquals(
                1 - ALPHA_VALUE,
                LowPassFilter.filterValue(1, 0, ALPHA_VALUE),
                ACCURACY);
        Assertions.assertEquals(0.0f, LowPassFilter.filterValue(1, 0, 1), ACCURACY);

        // value changes from 1 to 2, with different alpha values
        Assertions.assertEquals(1.0f, LowPassFilter.filterValue(1, 2, 0), ACCURACY);
        Assertions.assertEquals(
                1 + ALPHA_VALUE,
                LowPassFilter.filterValue(1, 2, ALPHA_VALUE),
                ACCURACY);
        Assertions.assertEquals(2.0f, LowPassFilter.filterValue(1, 2, 1), ACCURACY);

        // value stays the same, alpha value should have no effect
        Assertions.assertEquals(1.0f, LowPassFilter.filterValue(1, 1, 0), ACCURACY);
        Assertions.assertEquals(
                1.0f,
                LowPassFilter.filterValue(1, 1, ALPHA_VALUE),
                ACCURACY);
        Assertions.assertEquals(
                1.0f,
                LowPassFilter.filterValue(1, 1, 1),
                ACCURACY);

        // value changes from 0 to -1, with different alpha values
        Assertions.assertEquals(
                0.0f,
                LowPassFilter.filterValue(0, -1, 0),
                ACCURACY);
        Assertions.assertEquals(
                -1 * ALPHA_VALUE,
                LowPassFilter.filterValue(0, -1, ALPHA_VALUE),
                ACCURACY);
        Assertions.assertEquals(-1.0f, LowPassFilter.filterValue(0, -1, 1), ACCURACY);
    }

    /**
     * Tests filterValueSet array parameters, with unset arrays.
     */
    @Test
    public final void testFilterValueUnSetArrays() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // empty newArray
            LowPassFilter.filterValueSet(null, null, 0f);
        });
    }

    /**
     * Tests filterValueSet array parameters, with an empty array.
     */
    @Test
    public final void testFilterValueEmptyArrays() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // zero length newArray
            float[] newArray = new float[0];

            LowPassFilter.filterValueSet(null, newArray, 0f);
        });
    }

    /**
     * Tests filterValueSet array parameters, with an empty array.
     */
    @Test
    public final void testFilterValueUnequalArrays() {
        // empty previousArray, result should be new array
        float[] newArray = {1};
        float[] testArray = LowPassFilter.filterValueSet(null, newArray, 0f);
        Assertions.assertEquals(1, testArray.length);
        Assertions.assertEquals(1, testArray[0], ACCURACY);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // size of newArray and previousArray is not equal, throws exception
            float[] previousArray = new float[2];
            LowPassFilter.filterValueSet(previousArray, newArray, 0f);
        });
    }

    /**
     * Tests filterValueSet.
     */
    @Test
    public final void testFilterValueSet() {
        float[] newArray = {1, 0};
        float[] previousArray = {0, 1};

        float[] filteredArray = LowPassFilter.filterValueSet(
                previousArray, newArray, ALPHA_VALUE);

        Assertions.assertEquals(ALPHA_VALUE, filteredArray[0], ACCURACY);
        Assertions.assertEquals(1 - ALPHA_VALUE, filteredArray[1], ACCURACY);
    }
}
