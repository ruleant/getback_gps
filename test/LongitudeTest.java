/**
 * Unit tests for Longitude class
 *
 * Copyright (C) 2012-2013 Dieter Adriaenssens
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

import junit.framework.TestCase;

/**
 * Unit tests for Longitude class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LongitudeTest extends TestCase {
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
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        longitude = new Longitude(0.0);
    }

    /**
     * Tests empty value.
     */
    public final void testNoValue() {
        assertEquals(0.0, longitude.getValue());
    }

    /**
     * Tests value.
     */
    public final void testValue() {
        longitude.setValue(VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, longitude.getValue());

        longitude.setValue(Longitude.SEGMENT_EAST_HIGH);
        assertEquals(Longitude.SEGMENT_EAST_HIGH, longitude.getValue());
    }

    /**
     * Tests negative value.
     */
    public final void testNegValue() {
        longitude.setValue(-1.0 * VALID_COORDINATE);
        assertEquals(-1.0 * VALID_COORDINATE, longitude.getValue());

        longitude.setValue(Longitude.SEGMENT_WEST_LOW);
        assertEquals(Longitude.SEGMENT_WEST_LOW, longitude.getValue());
    }

    /**
     * Tests out of range value.
     */
    public final void testOutOfRangeValue() {
        try {
            longitude.setValue(OUT_OF_RANGE);
            fail("should have thrown exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "newValue is not in range -180.0 .. 180.0",
                    e.getMessage());
        }

        assertEquals(0.0, longitude.getValue());

        try {
            longitude.setValue(-1 * OUT_OF_RANGE);
            fail("should have thrown exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "newValue is not in range -180.0 .. 180.0",
                    e.getMessage());
        }

        assertEquals(0.0, longitude.getValue());
    }

    /**
     * Tests getSegment.
     */
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
    public final void testGetConvertedValue() {
        assertEquals(0.0, longitude.getConvertedValue());

        longitude.setValue(VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, longitude.getConvertedValue());

        longitude.setValue(Longitude.SEGMENT_EAST_HIGH);
        assertEquals(
                Longitude.SEGMENT_EAST_HIGH,
                longitude.getConvertedValue());

        longitude.setValue(-1 * VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, longitude.getConvertedValue());

        longitude.setValue(Longitude.SEGMENT_WEST_LOW);
        assertEquals(
                Math.abs(Longitude.SEGMENT_WEST_LOW),
                longitude.getConvertedValue());
    }
}
