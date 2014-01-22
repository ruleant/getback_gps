/**
 * Unit tests for Coordinate class
 *
 * Copyright (C) 2012-2014 Dieter Adriaenssens
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

import com.github.ruleant.getback_gps.lib.Coordinate;
import com.github.ruleant.getback_gps.lib.CoordinateRotation;

import junit.framework.TestCase;

/**
 * Unit tests for Coordinate class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class CoordinateRotationTest extends TestCase {
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
    private Coordinate coordinate1;

    /**
     * Center X coordinate.
     */
    private static final long CENTER_X = 100;

    /**
     * Center Y coordinate.
     */
    private static final long CENTER_Y = 150;

    /**
     * Unit 30 (for X and Y coordinates).
     */
    public static final long UNIT_30 = 30;

    /**
     * Unit 40 (for X and Y coordinates).
     */
    public static final long UNIT_40 = 40;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        rotationCenter = new Coordinate(0, 0);

        converter = new CoordinateRotation(rotationCenter, 0, 1);

        coordinate1 = new Coordinate(0, 0);
    }

    /**
     * Tests empty value.
     */
    public final void testNoValue() {
        Coordinate converted = converter.getConvertedCoordinate(coordinate1);
        assertEquals(0, converted.getCartesianX());
        assertEquals(0, converted.getCartesianY());
    }

    /**
     * Tests center coordinate.
     */
    public final void testCenterCoordinate() {
        rotationCenter.setCartesianCoordinate(CENTER_X, CENTER_Y);

        Coordinate converted = converter.getConvertedCoordinate(coordinate1);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());
    }

    /**
     * Tests coordinate conversion without rotation,
     * letting the coordinates point to all quadrants.
     */
    public final void testNoRotation() {
        rotationCenter.setCartesianCoordinate(CENTER_X, CENTER_Y);

        // pointing right, after conversion pointing up
        coordinate1.setCartesianCoordinate(UNIT_30, 0);
        Coordinate converted = converter.getConvertedCoordinate(coordinate1);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y - UNIT_30, converted.getCartesianY());

        coordinate1.setCartesianCoordinate(UNIT_30, UNIT_40);
        converted = converter.getConvertedCoordinate(coordinate1);
        assertEquals(CENTER_X + UNIT_40, converted.getCartesianX());
        assertEquals(CENTER_Y - UNIT_30, converted.getCartesianY());

        // pointing up, after conversion pointing right
        coordinate1.setCartesianCoordinate(0, UNIT_30);
        converted = converter.getConvertedCoordinate(coordinate1);
        assertEquals(CENTER_X + UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());

        // pointing left, after conversion pointing down
        coordinate1.setCartesianCoordinate(-1 * UNIT_30, 0);
        converted = converter.getConvertedCoordinate(coordinate1);
        assertEquals(CENTER_X, converted.getCartesianX());
        assertEquals(CENTER_Y + UNIT_30, converted.getCartesianY());

        // pointing down, after conversion pointing left
        coordinate1.setCartesianCoordinate(0, -1 * UNIT_30);
        converted = converter.getConvertedCoordinate(coordinate1);
        assertEquals(CENTER_X - UNIT_30, converted.getCartesianX());
        assertEquals(CENTER_Y, converted.getCartesianY());
    }
}
