/**
 * Unit tests for LowPassFilter class
 *
 * Copyright (C) 2014-2015 Dieter Adriaenssens
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class LowPassFilterTest {
    /**
     * Expected Exception.
     */
    @Rule public final ExpectedException thrown = ExpectedException.none();

    /**
     * Value for alpha parameter.
     */
    private static final float ALPHA_VALUE = 0.6f;

    /**
     * Accuracy.
     */
    private static final double ACCURACY = 0.0001;

    /**
     * Exception message when value is out of range.
     */
    private static final String MESSAGE_VALUE_RANGE
            = "parameter alpha is not in range 0.0 .. 1.0";

    /**
     * Exception message when array is empty.
     */
    private static final String MESSAGE_EMPTY_ARRAY
            = "parameter newArray should not be an empty array";

    /**
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 0), ACCURACY);
        assertEquals(
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
        assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 0), ACCURACY);
        assertEquals(
                0.0f,
                LowPassFilter.filterValue(0, 0, ALPHA_VALUE),
                ACCURACY);
        assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 1), ACCURACY);
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        // invalid range for parameter alpha
        LowPassFilter.filterValue(0, 0, -1);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller2() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        // invalid range for parameter alpha
        LowPassFilter.filterValue(0, 0, -1 * 2);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
      * Tests out of range value, bigger than highest allowed value.
      */
    @Test
    public final void testOutOfRangeValueBigger() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        LowPassFilter.filterValue(0, 0, 2);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests filterValue() method.
     */
    @Test
    public final void testFilterValue() {
        // value changes from 0 to 1, with different alpha values
        assertEquals(0.0f, LowPassFilter.filterValue(0, 1, 0), ACCURACY);
        assertEquals(
                ALPHA_VALUE,
                LowPassFilter.filterValue(0, 1, ALPHA_VALUE),
                ACCURACY);
        assertEquals(1.0f, LowPassFilter.filterValue(0, 1, 1), ACCURACY);

        // value changes from 1 to 0, with different alpha values
        assertEquals(1.0f, LowPassFilter.filterValue(1, 0, 0), ACCURACY);
        assertEquals(
                1 - ALPHA_VALUE,
                LowPassFilter.filterValue(1, 0, ALPHA_VALUE),
                ACCURACY);
        assertEquals(0.0f, LowPassFilter.filterValue(1, 0, 1), ACCURACY);

        // value changes from 1 to 2, with different alpha values
        assertEquals(1.0f, LowPassFilter.filterValue(1, 2, 0), ACCURACY);
        assertEquals(
                1 + ALPHA_VALUE,
                LowPassFilter.filterValue(1, 2, ALPHA_VALUE),
                ACCURACY);
        assertEquals(2.0f, LowPassFilter.filterValue(1, 2, 1), ACCURACY);

        // value stays the same, alpha value should have no effect
        assertEquals(1.0f, LowPassFilter.filterValue(1, 1, 0), ACCURACY);
        assertEquals(
                1.0f,
                LowPassFilter.filterValue(1, 1, ALPHA_VALUE),
                ACCURACY);
        assertEquals(
                1.0f,
                LowPassFilter.filterValue(1, 1, 1),
                ACCURACY);

        // value changes from 0 to -1, with different alpha values
        assertEquals(
                0.0f,
                LowPassFilter.filterValue(0, -1, 0),
                ACCURACY);
        assertEquals(
                -1 * ALPHA_VALUE,
                LowPassFilter.filterValue(0, -1, ALPHA_VALUE),
                ACCURACY);
        assertEquals(-1.0f, LowPassFilter.filterValue(0, -1, 1), ACCURACY);
    }

    /**
     * Tests filterValueSet array parameters, with unset arrays.
     */
    @Test
    public final void testFilterValueUnSetArrays() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_EMPTY_ARRAY);

        // empty newArray
        LowPassFilter.filterValueSet(null, null, 0f);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests filterValueSet array parameters, with an empty array.
     */
    @Test
    public final void testFilterValueEmptyArrays() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_EMPTY_ARRAY);

        // zero length newArray
        float[] newArray = new float[0];

        LowPassFilter.filterValueSet(null, newArray, 0f);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests filterValueSet array parameters, with an empty array.
     */
    @Test
    public final void testFilterValueUnequalArrays() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(
                "parameter previousArray (length = 2) should have the "
                + "same size as parameter newArray (length = 1)");

        // empty previousArray, result should be new array
        float[] newArray = {1};
        float[] testArray = LowPassFilter.filterValueSet(null, newArray, 0f);
        assertEquals(1, testArray.length);
        assertEquals(1, testArray[0], ACCURACY);

        // size of newArray and previousArray is not equal, throws exception
        float[] previousArray = new float[2];

        LowPassFilter.filterValueSet(previousArray, newArray, 0f);
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

        assertEquals(ALPHA_VALUE, filteredArray[0], ACCURACY);
        assertEquals(1 - ALPHA_VALUE, filteredArray[1], ACCURACY);
    }
}
