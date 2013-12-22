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
     * Radius 20 * sqrt(2) units.
     */
    private static final double RADIUS_20SQRT2 = 28.284271247461902;

    /**
     * Radius 20 units.
     */
    private static final double RADIUS_20 = 20.0;

    /**
     * Radius 50 units.
     */
    private static final double RADIUS_50 = 50.0;

    /**
     * Angle 0°.
     */
    private static final double ANGLE_0 = 0.0;

    /**
     * Angle 45°.
     */
    private static final double ANGLE_45 = 45.0;

    /**
     * Angle 53,129°.
     */
    private static final double ANGLE_53P129 = 53.13010235415598;

    /**
     * Angle 90°.
     */
    private static final double ANGLE_90 = 90.0;

    /**
     * Angle 225° (inverse angle of 45°).
     */
    private static final double ANGLE_225 = 225.0;

    /**
     * Angle 360°.
     */
    private static final double ANGLE_360 = 360.0;

    /**
     * Unit 20 (for X and Y coordinates).
     */
    private static final long UNIT_20 = 20;

    /**
     * Unit 30 (for X and Y coordinates).
     */
    private static final long UNIT_30 = 30;

    /**
     * Unit 40 (for X and Y coordinates).
     */
    private static final long UNIT_40 = 40;

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

        double[] coordinatePolarArray = coordinate.getPolarCoordinate();
        assertEquals(0.0, coordinatePolarArray[Coordinate.RADIUS]);
        assertEquals(ANGLE_0, coordinatePolarArray[Coordinate.ANGLE]);

        long[] coordinateCartesianArray = coordinate.getCartesianCoordinate();
        assertEquals(0, coordinateCartesianArray[Coordinate.X]);
        assertEquals(0, coordinateCartesianArray[Coordinate.Y]);
    }

    /**
     * Tests setPolarCoordinate.
     */
    public final void testSetPolarCoordinate() {
        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_45);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        // test zero radius
        coordinate.setPolarCoordinate(0.0, ANGLE_45);
        assertEquals(ANGLE_0, coordinate.getPolarAngle());
        assertEquals(0.0, coordinate.getPolarRadius());

        // test negative radius
        coordinate.setPolarCoordinate(RADIUS_20 * -1, ANGLE_45);
        assertEquals(ANGLE_225, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        // test 360° angle
        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_360);
        assertEquals(ANGLE_0, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        // test 45° + 1 turn angle (=405°)
        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_360 + ANGLE_45);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests setCartesianCoordinate.
     */
    public final void testSetCartesianCoordinate() {
        // test zero radius
        coordinate.setCartesianCoordinate(0, 0);
        assertEquals(ANGLE_0, coordinate.getPolarAngle());
        assertEquals(0.0, coordinate.getPolarRadius());

        coordinate.setCartesianCoordinate(UNIT_20, 0);
        assertEquals(ANGLE_0, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        coordinate.setCartesianCoordinate(0, UNIT_20);
        assertEquals(ANGLE_90, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());

        coordinate.setCartesianCoordinate(UNIT_20, UNIT_20);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20SQRT2, coordinate.getPolarRadius());

        coordinate.setCartesianCoordinate(UNIT_30, UNIT_40);
        assertEquals(ANGLE_53P129, coordinate.getPolarAngle());
        assertEquals(RADIUS_50, coordinate.getPolarRadius());

        coordinate.setCartesianCoordinate(-1 * UNIT_20, UNIT_20);
        assertEquals(ANGLE_90 + ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20SQRT2, coordinate.getPolarRadius());

        coordinate.setCartesianCoordinate(-1 * UNIT_20, -1 * UNIT_20);
        assertEquals(ANGLE_225, coordinate.getPolarAngle());
        assertEquals(RADIUS_20SQRT2, coordinate.getPolarRadius());

        coordinate.setCartesianCoordinate(UNIT_20, -1 * UNIT_20);
        assertEquals(ANGLE_360 - ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20SQRT2, coordinate.getPolarRadius());
    }

    /**
     * Tests setCoordinate.
     */
    public final void testSetCoordinate() {
        Coordinate newCoordinate = new Coordinate(RADIUS_20, ANGLE_45);
        assertEquals(ANGLE_45, newCoordinate.getPolarAngle());
        assertEquals(RADIUS_20, newCoordinate.getPolarRadius());

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

    /**
     * Tests polar Coordinate constructor.
     */
    public final void testPolarConstructor() {
        coordinate = new Coordinate(RADIUS_20, ANGLE_45);
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests Cartesian Coordinate constructor.
     */
    public final void testCartesianConstructor() {
        coordinate = new Coordinate(UNIT_30, UNIT_40);
        assertEquals(ANGLE_53P129, coordinate.getPolarAngle());
        assertEquals(RADIUS_50, coordinate.getPolarRadius());
    }

    /**
     * Tests Coordinate constructor.
     */
    public final void testCoordinateConstructor() {
        coordinate = new Coordinate(new Coordinate(RADIUS_20, ANGLE_45));
        assertEquals(ANGLE_45, coordinate.getPolarAngle());
        assertEquals(RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests getCartesianX/Y.
     */
    public final void testGetCartesianCoordinate() {
        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_0);
        assertEquals(UNIT_20, coordinate.getCartesianX());
        assertEquals(0, coordinate.getCartesianY());

        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_90);
        assertEquals(0, coordinate.getCartesianX());
        assertEquals(UNIT_20, coordinate.getCartesianY());

        coordinate.setPolarCoordinate(RADIUS_50, ANGLE_53P129);
        assertEquals(UNIT_30, coordinate.getCartesianX());
        assertEquals(UNIT_40, coordinate.getCartesianY());

        coordinate.setPolarCoordinate(RADIUS_20SQRT2, ANGLE_90 + ANGLE_45);
        assertEquals(-1 * UNIT_20, coordinate.getCartesianX());
        assertEquals(UNIT_20, coordinate.getCartesianY());

        coordinate.setPolarCoordinate(RADIUS_20SQRT2, ANGLE_225);
        assertEquals(-1 * UNIT_20, coordinate.getCartesianX());
        assertEquals(-1 * UNIT_20, coordinate.getCartesianY());

        coordinate.setPolarCoordinate(RADIUS_20SQRT2, ANGLE_360 - ANGLE_45);
        assertEquals(UNIT_20, coordinate.getCartesianX());
        assertEquals(-1 * UNIT_20, coordinate.getCartesianY());
    }

    /**
     * Tests Cartesian to Cartesian conversion.
     */
    public final void testCartesianConversion() {
        coordinate.setCartesianCoordinate(UNIT_20, 0);
        assertEquals(UNIT_20, coordinate.getCartesianX());
        assertEquals(0, coordinate.getCartesianY());

        coordinate.setCartesianCoordinate(0, UNIT_20);
        assertEquals(0, coordinate.getCartesianX());
        assertEquals(UNIT_20, coordinate.getCartesianY());

        coordinate.setCartesianCoordinate(UNIT_40, UNIT_20);
        assertEquals(UNIT_40, coordinate.getCartesianX());
        assertEquals(UNIT_20, coordinate.getCartesianY());

        coordinate.setCartesianCoordinate(UNIT_20, UNIT_40);
        assertEquals(UNIT_20, coordinate.getCartesianX());
        assertEquals(UNIT_40, coordinate.getCartesianY());

        coordinate.setCartesianCoordinate(1, 1);
        assertEquals(1, coordinate.getCartesianX());
        assertEquals(1, coordinate.getCartesianY());

        coordinate.setCartesianCoordinate(1, -1);
        assertEquals(1, coordinate.getCartesianX());
        assertEquals(-1, coordinate.getCartesianY());

        coordinate.setCartesianCoordinate(-1, -1);
        assertEquals(-1, coordinate.getCartesianX());
        assertEquals(-1, coordinate.getCartesianY());

        coordinate.setCartesianCoordinate(-1, 1);
        assertEquals(-1, coordinate.getCartesianX());
        assertEquals(1, coordinate.getCartesianY());
    }

    /**
     * Tests getCartesianCoordinate as array.
     */
    public final void testGetCartesianCoordinateArray() {
        long[] coordinateArray;

        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_0);
        coordinateArray = coordinate.getCartesianCoordinate();
        assertEquals(UNIT_20, coordinateArray[Coordinate.X]);
        assertEquals(0, coordinateArray[Coordinate.Y]);

        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_90);
        coordinateArray = coordinate.getCartesianCoordinate();
        assertEquals(0, coordinateArray[Coordinate.X]);
        assertEquals(UNIT_20, coordinateArray[Coordinate.Y]);

        coordinate.setPolarCoordinate(RADIUS_50, ANGLE_53P129);
        coordinateArray = coordinate.getCartesianCoordinate();
        assertEquals(UNIT_30, coordinateArray[Coordinate.X]);
        assertEquals(UNIT_40, coordinateArray[Coordinate.Y]);
    }

    /**
     * Tests getPolarCoordinate as array.
     */
    public final void testGetPolarCoordinateArray() {
        double[] coordinateArray;

        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_0);
        coordinateArray = coordinate.getPolarCoordinate();
        assertEquals(RADIUS_20, coordinateArray[Coordinate.RADIUS]);
        assertEquals(ANGLE_0, coordinateArray[Coordinate.ANGLE]);

        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_90);
        coordinateArray = coordinate.getPolarCoordinate();
        assertEquals(RADIUS_20, coordinateArray[Coordinate.RADIUS]);
        assertEquals(ANGLE_90, coordinateArray[Coordinate.ANGLE]);

        coordinate.setPolarCoordinate(RADIUS_50, ANGLE_53P129);
        coordinateArray = coordinate.getPolarCoordinate();
        assertEquals(RADIUS_50, coordinateArray[Coordinate.RADIUS]);
        assertEquals(ANGLE_53P129, coordinateArray[Coordinate.ANGLE]);
    }
}
