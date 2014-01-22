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
}
