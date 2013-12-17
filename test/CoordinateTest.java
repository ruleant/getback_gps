/**
 * Unit tests for Coordinate class
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

import com.github.ruleant.getback_gps.lib.Coordinate;

import junit.framework.TestCase;

/**
 * Unit tests for Coordinate class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class CoordinateTest extends TestCase {
    /**
     * Instance of the coordinate class.
     */
    private Coordinate coordinate;

    /**
     * Radius 20 units.
     */
    private static final double RADIUS_20 = 20.0;

    /**
     * Angle 0°.
     */
    private static final double ANGLE_0 = 0.0;

    /**
     * Angle 45°.
     */
    private static final double ANGLE_45 = 45.0;

    /**
     * Angle 225° (inverse angle of 45°).
     */
    private static final double ANGLE_225 = 225.0;

    /**
     * Angle 360°.
     */
    private static final double ANGLE_360 = 360.0;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        coordinate = new Coordinate(0.0, 0.0);
    }

    /**
     * Tests empty value.
     */
    public final void testNoValue() {
        assertEquals(0.0, coordinate.getPolarAngle());
        assertEquals(0.0, coordinate.getPolarRadius());
        assertEquals(0, coordinate.getCartesianX());
        assertEquals(0, coordinate.getCartesianY());
    }

    /**
     * Tests polar Coordinate constructor.
     */
    public final void testPolarConstructor() {
        coordinate = new Coordinate(ANGLE_45, RADIUS_20);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests Coordinate constructor.
     */
    public final void testCoordinateConstructor() {
        Coordinate newCoordinate = new Coordinate(ANGLE_45, RADIUS_20);
        coordinate = new Coordinate(newCoordinate);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests setPolarCoordinate.
     */
    public final void testSetPolarCoordinate() {
        coordinate.setPolarCoordinate(ANGLE_45, RADIUS_20);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        // test zero radius
        coordinate.setPolarCoordinate(ANGLE_45, 0.0);
        assertEquals(ANGLE_0, coordinate.getPolarAngle());
        assertEquals(0.0, coordinate.getPolarRadius());

        // test negative radius
        coordinate.setPolarCoordinate(ANGLE_45, RADIUS_20 * -1);
        assertEquals(ANGLE_225, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        // test 360° angle
        coordinate.setPolarCoordinate(ANGLE_360, RADIUS_20);
        assertEquals(ANGLE_0, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        // test 45° + 1 turn angle (=405°)
        coordinate.setPolarCoordinate(ANGLE_360 + ANGLE_45, RADIUS_20);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests setCoordinate.
     */
    public final void testSetCoordinate() {
        Coordinate newCoordinate = new Coordinate(ANGLE_45, RADIUS_20);
        coordinate.setCoordinate(newCoordinate);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests null value for new coordinate in setCoordinate.
     */
    public final void testSetCoordinateNull() {
        try {
            coordinate.setCoordinate(null);
            fail("should have thrown exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Parameter coordinate should not be null",
                    e.getMessage());
        }
    }
}
