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

import com.github.ruleant.getback_gps.lib.FormatUtils;

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
    private static final float ALPHA_VALUE = 0.5f;

    /**
     * Accuracy.
     */
    private static final double ACCURACY = 0.0001;

    /**
     * Value reached after 1 cycle with alpha = 0.5 : 50 %.
     */
    private static final float CYCLE1 = 0.5f;

    /**
     * Value reached after 2 cycles with alpha = 0.5 : 75 %.
     */
    private static final float CYCLE2 = 0.75f;

    /**
     * Value reached after 3 cycles with alpha = 0.5 : 87.5 %.
     */
    private static final float CYCLE3 = 0.875f;

     /**
     * Value reached after 4 cycles with alpha = 0.5 : 93.75 %.
     */
    private static final float CYCLE4 = 0.9375f;

    /**
     * Value reached after 5 cycles with alpha = 0.5 : 96.875 %.
     */
    private static final float CYCLE5 = 0.96875f;

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

    /**
     * Tests getAverageValue() method, with a positive step.
     */
    @Test
    public final void testAverageValuePositiveStep() {
	// initial value = 10
	// applied step = 50
	testAverageValueAfterStep(10, 50);
    }

    /**
     * Tests getAverageValue() method, with a negative step.
     */
    @Test
    public final void testAverageValueNegativeStep() {
	// initial value = 100
	// applied step = -50
	testAverageValueAfterStep(100, -50);
    }

    /**
     * Tests getAverageValue() in 5 cycles after a step is applied.
     */
    private final void testAverageValueAfterStep(final float initial_value,
            final float step_value) {
        // check initial state.
        assertEquals(
                FormatUtils.normalizeAngle(initial_value),
                CircularAverage.getAverageValue(initial_value, initial_value,
                      ALPHA_VALUE),
                ACCURACY);

        // with an alpha value of .5, the new value should be >95%
        // of the initial value after 5 cycles.
        // cycle 1 : 50%
        assertEquals(
                FormatUtils.normalizeAngle(initial_value + CYCLE1 * step_value),
                CircularAverage.getAverageValue(initial_value,
                        initial_value + step_value, ALPHA_VALUE),
                ACCURACY);
	// cycle 2 : 75%
        assertEquals(
                FormatUtils.normalizeAngle(initial_value + CYCLE2 * step_value),
                CircularAverage.getAverageValue(
                        initial_value + CYCLE1 * step_value,
                        initial_value + step_value, ALPHA_VALUE),
                ACCURACY);
	// cycle 3 : 87.5%
        assertEquals(
                FormatUtils.normalizeAngle(initial_value + CYCLE3 * step_value),
                CircularAverage.getAverageValue(
                        initial_value + CYCLE2 * step_value,
                        initial_value + step_value, ALPHA_VALUE),
                ACCURACY);
	// cycle 4 : 93.75%
        assertEquals(
                FormatUtils.normalizeAngle(initial_value + CYCLE4 * step_value),
                CircularAverage.getAverageValue(
                        initial_value + CYCLE3 * step_value,
                        initial_value + step_value, ALPHA_VALUE),
                ACCURACY);
	// cycle 5 : 96.875%
        assertEquals(
                FormatUtils.normalizeAngle(initial_value + CYCLE5 * step_value),
                CircularAverage.getAverageValue(
                        initial_value + CYCLE4 * step_value,
                        initial_value + step_value, ALPHA_VALUE),
                ACCURACY);
    }
}
