/**
 * Unit tests for CardinalDirection class
 *
 * Copyright (C) 2012-2015 Dieter Adriaenssens
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit tests for CardinalDirection class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class CardinalDirectionTest {
    /**
     * Expected Exception.
     */
    @Rule public final ExpectedException thrown = ExpectedException.none();

    /**
     * Instance of the CardinalDirection class.
     */
    private CardinalDirection object;

    /**
     * Valid coordinate value : 4°.
     */
    private static final double VALID_COORDINATE = 4.0;

    /**
     * Out of range coordinate value: 100°.
     */
    private static final double OUT_OF_RANGE = 400.0;

    /**
     * Accuracy.
     */
    private static final double ACCURACY = 0.001;

    /**
     * Exception message when value is out of range.
     */
    private static final String MESSAGE_VALUE_RANGE
            = "newValue is not in range 0.0 .. 360.0";

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public final void setUp() {
        object = new CardinalDirection(0.0);
    }

    /**
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        assertEquals(0.0, object.getValue(), ACCURACY);
    }

    /**
     * Tests value.
     */
    @Test
    public final void testValue() {
        object.setValue(VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, object.getValue(), ACCURACY);

        object.setValue(CardinalDirection.SEGMENT_NW_HIGH);
        assertEquals(
                CardinalDirection.SEGMENT_NW_HIGH, object.getValue(),
                ACCURACY);
    }

    /**
     * Tests out of range value, bigger than highest allowed value.
     */
    @Test
    public final void testOutOfRangeValueBigger() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        object.setValue(OUT_OF_RANGE);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        object.setValue(-1 * OUT_OF_RANGE);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests getSegment.
     */
    @Test
    public final void testGetSegment() {
        assertEquals(CardinalDirection.SEGMENT_NORTHEAST, object.getSegment());

        object.setValue(VALID_COORDINATE);
        assertEquals(CardinalDirection.SEGMENT_NORTHEAST, object.getSegment());

        object.setValue(CardinalDirection.SEGMENT_NE_HIGH);
        assertEquals(CardinalDirection.SEGMENT_NORTHEAST, object.getSegment());

        object.setValue(VALID_COORDINATE + CardinalDirection.SEGMENT_SE_LOW);
        assertEquals(CardinalDirection.SEGMENT_SOUTHEAST, object.getSegment());

        object.setValue(CardinalDirection.SEGMENT_SE_HIGH);
        assertEquals(CardinalDirection.SEGMENT_SOUTHEAST, object.getSegment());

        object.setValue(VALID_COORDINATE + CardinalDirection.SEGMENT_SW_LOW);
        assertEquals(CardinalDirection.SEGMENT_SOUTHWEST, object.getSegment());

        object.setValue(CardinalDirection.SEGMENT_SW_HIGH);
        assertEquals(CardinalDirection.SEGMENT_SOUTHWEST, object.getSegment());

        object.setValue(VALID_COORDINATE + CardinalDirection.SEGMENT_NW_LOW);
        assertEquals(CardinalDirection.SEGMENT_NORTHWEST, object.getSegment());

        object.setValue(CardinalDirection.SEGMENT_NW_HIGH);
        assertEquals(CardinalDirection.SEGMENT_NORTHWEST, object.getSegment());
    }

    /**
     * Tests getSegmentUnit.
     */
    @Test
    public final void testGetSegmentUnit() {
        assertEquals(CardinalDirection.SEGMENT_NE_UNIT,
                object.getSegmentUnit());

        object.setValue(VALID_COORDINATE);
        assertEquals(CardinalDirection.SEGMENT_NE_UNIT,
                object.getSegmentUnit());

        object.setValue(VALID_COORDINATE + CardinalDirection.SEGMENT_SE_LOW);
        assertEquals(CardinalDirection.SEGMENT_SE_UNIT,
                object.getSegmentUnit());

        object.setValue(VALID_COORDINATE + CardinalDirection.SEGMENT_SW_LOW);
        assertEquals(CardinalDirection.SEGMENT_SW_UNIT,
                object.getSegmentUnit());

        object.setValue(VALID_COORDINATE + CardinalDirection.SEGMENT_NW_LOW);
        assertEquals(CardinalDirection.SEGMENT_NW_UNIT,
                object.getSegmentUnit());
    }

    /**
     * Tests getConvertedValue.
     */
    @Test
    public final void testGetConvertedValue() {
        assertEquals(0.0, object.getConvertedValue(), ACCURACY);

        object.setValue(VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, object.getConvertedValue(), ACCURACY);

        object.setValue(CardinalDirection.SEGMENT_NW_HIGH);
        assertEquals(CardinalDirection.SEGMENT_NW_HIGH,
                object.getConvertedValue(),
                ACCURACY);
    }
}
