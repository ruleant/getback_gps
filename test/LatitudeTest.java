/**
 * Unit tests for Latitude class
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
 * Unit tests for Latitude class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LatitudeTest extends TestCase {
    /**
     * Instance of the latitude class.
     */
    private Latitude latitude;

    /**
     * Valid coordinate value : 4°.
     */
    private static final double VALID_COORDINATE = 4.0;

    /**
     * Out of range coordinate value: 100°.
     */
    private static final double OUT_OF_RANGE = 100.0;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        latitude = new Latitude(0.0);
    }

    /**
     * Tests empty value.
     */
    public final void testNoValue() {
        assertEquals(0.0, latitude.getValue());
    }

    /**
     * Tests value.
     */
    public final void testValue() {
        latitude.setValue(VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, latitude.getValue());

        latitude.setValue(Latitude.SEGMENT_NORTH_HIGH);
        assertEquals(Latitude.SEGMENT_NORTH_HIGH, latitude.getValue());
    }

    /**
     * Tests negative value.
     */
    public final void testNegValue() {
        latitude.setValue(-1.0 * VALID_COORDINATE);
        assertEquals(-1.0 * VALID_COORDINATE, latitude.getValue());

        latitude.setValue(Latitude.SEGMENT_SOUTH_LOW);
        assertEquals(Latitude.SEGMENT_SOUTH_LOW, latitude.getValue());
    }

    /**
     * Tests out of range value.
     */
    public final void testOutOfRangeValue() {
        try {
            latitude.setValue(OUT_OF_RANGE);
            fail("should have thrown exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "newValue is not in range -90.0 .. 90.0",
                    e.getMessage());
        }

        assertEquals(0.0, latitude.getValue());

        try {
            latitude.setValue(-1 * OUT_OF_RANGE);
            fail("should have thrown exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "newValue is not in range -90.0 .. 90.0",
                    e.getMessage());
        }

        assertEquals(0.0, latitude.getValue());
    }

    /**
     * Tests getSegment.
     */
    public final void testGetSegment() {
        assertEquals(Latitude.SEGMENT_NORTH, latitude.getSegment());

        latitude.setValue(VALID_COORDINATE);
        assertEquals(Latitude.SEGMENT_NORTH, latitude.getSegment());

        latitude.setValue(Latitude.SEGMENT_NORTH_HIGH);
        assertEquals(Latitude.SEGMENT_NORTH, latitude.getSegment());

        latitude.setValue(-1 * VALID_COORDINATE);
        assertEquals(Latitude.SEGMENT_SOUTH, latitude.getSegment());

        latitude.setValue(Latitude.SEGMENT_SOUTH_LOW);
        assertEquals(Latitude.SEGMENT_SOUTH, latitude.getSegment());
    }

    /**
     * Tests getSegmentUnit.
     */
    public final void testGetSegmentUnit() {
        assertEquals(Latitude.SEGMENT_NORTH_UNIT, latitude.getSegmentUnit());

        latitude.setValue(VALID_COORDINATE);
        assertEquals(Latitude.SEGMENT_NORTH_UNIT, latitude.getSegmentUnit());

        latitude.setValue(Latitude.SEGMENT_NORTH_HIGH);
        assertEquals(Latitude.SEGMENT_NORTH_UNIT, latitude.getSegmentUnit());

        latitude.setValue(-1 * VALID_COORDINATE);
        assertEquals(Latitude.SEGMENT_SOUTH_UNIT, latitude.getSegmentUnit());

        latitude.setValue(Latitude.SEGMENT_SOUTH_LOW);
        assertEquals(Latitude.SEGMENT_SOUTH_UNIT, latitude.getSegmentUnit());
    }

    /**
     * Tests getConvertedValue.
     */
    public final void testGetConvertedValue() {
        assertEquals(0.0, latitude.getConvertedValue());

        latitude.setValue(VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, latitude.getConvertedValue());

        latitude.setValue(Latitude.SEGMENT_NORTH_HIGH);
        assertEquals(Latitude.SEGMENT_NORTH_HIGH, latitude.getConvertedValue());

        latitude.setValue(-1 * VALID_COORDINATE);
        assertEquals(VALID_COORDINATE, latitude.getConvertedValue());

        latitude.setValue(Latitude.SEGMENT_SOUTH_LOW);
        assertEquals(
                Math.abs(Latitude.SEGMENT_SOUTH_LOW),
                latitude.getConvertedValue());
    }
}
