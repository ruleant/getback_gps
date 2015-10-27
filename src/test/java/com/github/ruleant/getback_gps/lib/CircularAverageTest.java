/**
 * Unit tests for CircularAverage class
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
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
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
 * Unit tests for CircularAverage class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class CircularAverageTest {
    /**
     * Value for alpha parameter.
     */
    private static final float ALPHA_VALUE = 0.5f;
    /**
     * Accuracy.
     */
    private static final double ACCURACY = 0.0001;

    /**
     * Angle first quadrant.
     */
    private static final float ANGLE_Q1 = 40;

    /**
     * Angle second quadrant.
     */
    private static final float ANGLE_Q2 = 130;

    /**
     * Angle third quadrant.
     */
    private static final float ANGLE_Q3 = 220;

    /**
     * Angle fourth quadrant.
     */
    private static final float ANGLE_Q4 = 310;

    /**
     * Add step value 30.
     */
    private static final float STEP_30 = 30;

    /**
     * Add step value 100.
     */
    private static final float STEP_100 = 100;

    /**
     * Add step value 170.
     */
    private static final float STEP_170 = 170;

    /**
     * Add step value 180.
     */
    private static final float STEP_180 = 180;

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
     * Expected Exception.
     */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

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
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests out of range value, bigger than highest allowed value.
     */
    @Test
    public final void testOutOfRangeValueBigger() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        CircularAverage.getAverageValue(0, 0, 2);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests getAverageValue() method, with a positive step.
     */
    @Test
    public final void testAverageValuePositiveStep() {
        // initial value in first quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q1, STEP_30);
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q1, STEP_100);
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q1, STEP_170);
        // step to inverse angle
        testAverageValueAfterStep(ANGLE_Q1, STEP_180);

        // initial value in second quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q2, STEP_30);
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q2, STEP_100);
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q2, STEP_170);
        // step to inverse angle
        testAverageValueAfterStep(ANGLE_Q2, STEP_180);

        // initial value in third quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q3, STEP_30);
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q3, STEP_100);
        // bigger steps in testAverageValuePositiveStepCrossMax()

        // initial value in fourth quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q4, STEP_30);
        // bigger steps in testAverageValuePositiveStepCrossMax()
    }

    /**
     * Tests getAverageValue() method, with a positive step,
     * crossing maximum value, < 360° -> > 0°.
     */
    @Test
    public final void testAverageValuePositiveStepCrossMax() {
        // initial value in third quadrant
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q3, STEP_170);
        // bigger steps in testAverageValuePositiveStepCrossMax()

        // initial value in fourth quadrant
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q4, STEP_100);
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q4, STEP_170);
    }

    /**
     * Tests getAverageValue() method, with a negative step.
     */
    @Test
    public final void testAverageValueNegativeStep() {
        // initial value in fourth quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q4, -1 * STEP_30);
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q4, -1 * STEP_100);
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q4, -1 * STEP_170);
        // step to inverse angle
        testAverageValueAfterStep(ANGLE_Q4, -1 * STEP_180);

        // initial value in third quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q3, -1 * STEP_30);
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q3, -1 * STEP_100);
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q3, -1 * STEP_170);
        // step to inverse angle
        testAverageValueAfterStep(ANGLE_Q3, -1 * STEP_180);

        // initial value in second quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q2, -1 * STEP_30);
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q2, -1 * STEP_100);
        // bigger steps in testAverageValueNegativeStepCrossMin()

        // initial value in first quadrant
        // step to same quadrant
        testAverageValueAfterStep(ANGLE_Q1, -1 * STEP_30);
        // bigger steps in testAverageValueNegativeStepCrossMin()
    }

    /**
     * Tests getAverageValue() method, with a positive step,
     * crossing minimum value, > 0° -> < 360°.
     */
    @Test
    public final void testAverageValueNegativeStepCrossMin() {
        // initial value in first quadrant
        // step to next quadrant
        testAverageValueAfterStep(ANGLE_Q1, -1 * STEP_100);
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q1, -1 * STEP_170);

        // initial value in second quadrant
        // step to opposite quadrant
        testAverageValueAfterStep(ANGLE_Q2, -1 * STEP_170);
    }

    /**
     * Tests apply 180° degree step.
     */
    @Test
    public final void testAverageValue180Step() {
        // value will move clockwise
        testAverageValueAfterStep(FormatUtils.CIRCLE_ZERO, STEP_180);
        testAverageValueAfterStep(FormatUtils.CIRCLE_1Q, STEP_180);
        // value will move counter-clockwise
        testAverageValueAfterStep(FormatUtils.CIRCLE_HALF, -1 * STEP_180);
        testAverageValueAfterStep(FormatUtils.CIRCLE_3Q, -1 * STEP_180);
    }

    /**
     * Tests getAverageValue() in 5 cycles after a step is applied.
     *
     * @param initialValue Initial value
     * @param stepValue    Step to apply to initial value
     */
    private void testAverageValueAfterStep(final float initialValue,
                                                 final float stepValue) {
        // check initial state.
        assertEquals(
                FormatUtils.normalizeAngle(initialValue),
                CircularAverage.getAverageValue(initialValue, initialValue,
                        ALPHA_VALUE),
                ACCURACY
        );

        // with an alpha value of .5, the new value should be >95%
        // of the set point value after 5 cycles.
        float setPoint = (float) FormatUtils.normalizeAngle(
                initialValue + stepValue);
        // cycle 1 : 50%
        float expectedCycle1 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE1 * stepValue);
        assertEquals(
                expectedCycle1,
                CircularAverage.getAverageValue(initialValue,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 2 : 75%
        float expectedCycle2 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE2 * stepValue);
        assertEquals(
                expectedCycle2,
                CircularAverage.getAverageValue(expectedCycle1,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 3 : 87.5%
        float expectedCycle3 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE3 * stepValue);
        assertEquals(
                expectedCycle3,
                CircularAverage.getAverageValue(expectedCycle2,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 4 : 93.75%
        float expectedCycle4 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE4 * stepValue);
        assertEquals(
                expectedCycle4,
                CircularAverage.getAverageValue(expectedCycle3,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
        // cycle 5 : 96.875%
        float expectedCycle5 = (float) FormatUtils.normalizeAngle(
                initialValue + CYCLE5 * stepValue);
        assertEquals(
                expectedCycle5,
                CircularAverage.getAverageValue(expectedCycle4,
                        setPoint, ALPHA_VALUE),
                ACCURACY
        );
    }
}
