/**
 * Unit tests for CircularAverage class
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

/**
 * Unit tests for CircularAverage class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class CircularAverageTest {
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
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        assertEquals(0.0f, CircularAverage.getAverageValue(0, 0, 0), ACCURACY);
    }

    /**
     * Tests range of alpha parameter.
     */
    @Test
    public final void testAlphaParameterRange() {
        // valid range for parameter alpha
        assertEquals(0.0f, CircularAverage.getAverageValue(0, 0, 0), ACCURACY);
        assertEquals(
                0.0f,
                CircularAverage.getAverageValue(0, 0, ALPHA_VALUE),
                ACCURACY);
        assertEquals(0.0f, CircularAverage.getAverageValue(0, 0, 1), ACCURACY);
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        // invalid range for parameter alpha
        CircularAverage.getAverageValue(0, 0, -1);
    }

    /**
      * Tests out of range value, bigger than highest allowed value.
      */
    @Test
    public final void testOutOfRangeValueBigger() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        CircularAverage.getAverageValue(0, 0, 2);
    }

}
