/**
 * Unit tests for Longitude class
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
 * Unit tests for Longitude class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class LongitudeTest {
    /**
     * Expected Exception.
     */
    @Rule public final ExpectedException thrown = ExpectedException.none();

    /**
     * Instance of the longitude class.
     */
    private Longitude longitude;

    /**
     * Valid coordinate value : 4°.
     */
    private static final double VALID_COORDINATE = 4.0;

    /**
     * Out of range coordinate value: 190°.
     */
    private static final double OUT_OF_RANGE = 190.0;

    /**
     * Accuracy.
     */
    private static final double ACCURACY = 0.00001;

    /**
     * Exception message when value is out of range.
     */
    private static final String MESSAGE_VALUE_RANGE
            = "newValue is not in range -180.0 .. 180.0";

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public final void setUp() {
        longitude = new Longitude(0.0);
    }

    /**
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        assertEquals(0.0, longitude.getValue(), ACCURACY);
    }

    /**
     * Tests value.
     */
    @Test
    public final void testValue() {
        longitude.setValue(VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, longitude.getValue(), ACCURACY);

        longitude.setValue(Longitude.SEGMENT_EAST_HIGH);
        assertEquals(
                Longitude.SEGMENT_EAST_HIGH,
                longitude.getValue(),
                ACCURACY);
    }

    /**
     * Tests negative value.
     */
    @Test
    public final void testNegValue() {
        longitude.setValue(-1.0 * VALID_COORDINATE);
        assertEquals(-1.0 * VALID_COORDINATE, longitude.getValue(), ACCURACY);

        longitude.setValue(Longitude.SEGMENT_WEST_LOW);
        assertEquals(
                Longitude.SEGMENT_WEST_LOW,
                longitude.getValue(),
                ACCURACY);
    }

    /**
     * Tests out of range value, bigger than highest allowed value.
     */
    @Test
    public final void testOutOfRangeValueBigger() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        longitude.setValue(OUT_OF_RANGE);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests out of range value, smaller than lowest allowed value.
     */
    @Test
    public final void testOutOfRangeValueSmaller() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(MESSAGE_VALUE_RANGE);

        longitude.setValue(-1 * OUT_OF_RANGE);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests getSegment.
     */
    @Test
    public final void testGetSegment() {
        assertEquals(Longitude.SEGMENT_EAST, longitude.getSegment());

        longitude.setValue(VALID_COORDINATE);
        assertEquals(Longitude.SEGMENT_EAST, longitude.getSegment());

        longitude.setValue(Longitude.SEGMENT_EAST_HIGH);
        assertEquals(Longitude.SEGMENT_EAST, longitude.getSegment());

        longitude.setValue(-1 * VALID_COORDINATE);
        assertEquals(Longitude.SEGMENT_WEST, longitude.getSegment());

        longitude.setValue(Longitude.SEGMENT_WEST_LOW);
        assertEquals(Longitude.SEGMENT_WEST, longitude.getSegment());
    }

    /**
     * Tests getSegmentUnit.
     */
    @Test
    public final void testGetSegmentUnit() {
        assertEquals(Longitude.SEGMENT_EAST_UNIT, longitude.getSegmentUnit());

        longitude.setValue(VALID_COORDINATE);
        assertEquals(Longitude.SEGMENT_EAST_UNIT, longitude.getSegmentUnit());

        longitude.setValue(Longitude.SEGMENT_EAST_HIGH);
        assertEquals(Longitude.SEGMENT_EAST_UNIT, longitude.getSegmentUnit());

        longitude.setValue(-1 * VALID_COORDINATE);
        assertEquals(Longitude.SEGMENT_WEST_UNIT, longitude.getSegmentUnit());

        longitude.setValue(Longitude.SEGMENT_WEST_LOW);
        assertEquals(Longitude.SEGMENT_WEST_UNIT, longitude.getSegmentUnit());
    }

    /**
     * Tests getConvertedValue.
     */
    @Test
    public final void testGetConvertedValue() {
        assertEquals(0.0, longitude.getConvertedValue(), ACCURACY);

        longitude.setValue(VALID_COORDINATE);
        assertEquals(
                VALID_COORDINATE,
                longitude.getConvertedValue(),
                ACCURACY);

        longitude.setValue(Longitude.SEGMENT_EAST_HIGH);
        assertEquals(
                Longitude.SEGMENT_EAST_HIGH,
                longitude.getConvertedValue(),
                ACCURACY);

        longitude.setValue(-1 * VALID_COORDINATE);
        assertEquals(
                VALID_COORDINATE,
                longitude.getConvertedValue(),
                ACCURACY);

        longitude.setValue(Longitude.SEGMENT_WEST_LOW);
        assertEquals(
                Math.abs(Longitude.SEGMENT_WEST_LOW),
                longitude.getConvertedValue(),
                ACCURACY);
    }
}
