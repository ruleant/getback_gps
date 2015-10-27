/**
 * Unit tests for Coordinate class
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
 * Unit tests for Coordinate class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class CoordinateRotationTest {
    /**
     * Expected Exception.
     */
    @Rule public final ExpectedException thrown = ExpectedException.none();

    /**
     * Instance of the coordinate class.
     */
    private CoordinateRotation converter;

    /**
     * Rotation center (stub).
     */
    private Coordinate rotationCenter;

    /**
     * Test coordinate (stub).
     */
    private Coordinate testCoordinate;

    /**
     * Center X coordinate.
     */
    private static final long CENTER_X = 100;

    /**
     * Center Y coordinate.
     */
    private static final long CENTER_Y = 150;

    /**
     * Scale to 50%.
     */
    public static final double SCALE_HALF = 0.5;

    /**
     * Unit 30 (for X and Y coordinates).
     */
    public static final long UNIT_30 = 30;

    /**
     * Unit 40 (for X and Y coordinates).
     */
    public static final long UNIT_40 = 40;

    /**
     * Angle 90°.
     */
    public static final double ANGLE_90 = 90.0;

    /**
     * Angle 180°.
     */
    public static final double ANGLE_180 = 180.0;

    /**
     * Angle 270°.
     */
    public static final double ANGLE_270 = 270.0;

    /**
     * Angle 360°.
     */
    public static final double ANGLE_360 = 360.0;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public final void setUp() {
        rotationCenter = new Coordinate(0, 0);

        converter = new CoordinateRotation(rotationCenter, 0, 1);

        testCoordinate = new Coordinate(0, 0);
    }

    /**
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        Coordinate converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(0, converted.getCartesianX());
        assertEquals(0, converted.getCartesianY());
    }

    /**
     * Tests setting empty center coordinate.
     */
    @Test
    public final void testNoCenter() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter center should not be null");

        converter.setRotationCenter(null);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests center coordinate.
     */
    @Test
    public final void testCenterCoordinate() {
        rotationCenter.setCartesianCoordinate(CENTER_X, CENTER_Y);

        Coordinate converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());
    }

    /**
     * Tests coordinate conversion without rotation,
     * letting the coordinates point to all quadrants.
     */
    @Test
    public final void testConversionNoRotation() {
        rotationCenter.setCartesianCoordinate(CENTER_X, CENTER_Y);

        // pointing right, after conversion pointing up
        testCoordinate.setCartesianCoordinate(UNIT_30, 0);
        Coordinate converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y - UNIT_30, converted.getCartesianY());

        testCoordinate.setCartesianCoordinate(UNIT_30, UNIT_40);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X + UNIT_40, converted.getCartesianX());
        assertEquals(CENTER_Y - UNIT_30, converted.getCartesianY());

        // pointing up, after conversion pointing right
        testCoordinate.setCartesianCoordinate(0, UNIT_30);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X + UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());

        // pointing left, after conversion pointing down
        testCoordinate.setCartesianCoordinate(-1 * UNIT_30, 0);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y + UNIT_30, converted.getCartesianY());

        // pointing down, after conversion pointing left
        testCoordinate.setCartesianCoordinate(0, -1 * UNIT_30);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X - UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());
    }

    /**
     * Tests coordinate conversion with rotation,
     * letting the coordinate point to all quadrants.
     */
    @Test
    public final void testConversionWithRotation() {
        rotationCenter.setCartesianCoordinate(CENTER_X, CENTER_Y);

        // pointing right, after conversion pointing up
        testCoordinate.setCartesianCoordinate(UNIT_30, 0);
        Coordinate converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y - UNIT_30, converted.getCartesianY());

        // after 90° rotation pointing right
        converter.setRotationAngle(ANGLE_90);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X + UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());

        // after -90° (= 270°) rotation pointing left
        converter.setRotationAngle(-1 * ANGLE_90);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X - UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());

        // after 180° rotation pointing down
        converter.setRotationAngle(ANGLE_180);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y + UNIT_30, converted.getCartesianY());

        // after 270° rotation pointing left
        converter.setRotationAngle(ANGLE_270);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X - UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());

        // after 360° rotation pointing up
        converter.setRotationAngle(ANGLE_360);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y - UNIT_30, converted.getCartesianY());
    }

    /**
     * Tests coordinate conversion scaling the radius.
     */
    @Test
    public final void testConversionScaleRadius() {
        rotationCenter.setCartesianCoordinate(CENTER_X, CENTER_Y);
        converter.setScaleRadius(2);

        // pointing right, after conversion pointing up
        testCoordinate.setCartesianCoordinate(UNIT_30, 0);
        Coordinate converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y - (2 * UNIT_30), converted.getCartesianY());

        testCoordinate.setCartesianCoordinate(UNIT_30, UNIT_40);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X + (2 * UNIT_40), converted.getCartesianX());
        assertEquals(CENTER_Y - (2 * UNIT_30), converted.getCartesianY());

        converter.setScaleRadius(SCALE_HALF);

        // pointing right, after conversion pointing up
        testCoordinate.setCartesianCoordinate(UNIT_30, 0);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y - (UNIT_30 / 2), converted.getCartesianY());

        testCoordinate.setCartesianCoordinate(UNIT_30, UNIT_40);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X + (UNIT_40 / 2), converted.getCartesianX());
        assertEquals(CENTER_Y - (UNIT_30 / 2), converted.getCartesianY());

        converter.setScaleRadius(-1);

        // pointing right, after conversion pointing down
        testCoordinate.setCartesianCoordinate(UNIT_30, 0);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y + UNIT_30, converted.getCartesianY());

        // pointing up, after conversion pointing left
        testCoordinate.setCartesianCoordinate(0, UNIT_30);
        converted = converter.getConvertedCoordinate(testCoordinate);
        assertEquals(CENTER_X - UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());
    }
}
