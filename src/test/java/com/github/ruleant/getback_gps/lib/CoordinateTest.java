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
public class CoordinateTest {
    /**
     * Expected Exception.
     */
    @Rule public final ExpectedException thrown = ExpectedException.none();

    /**
     * Instance of the coordinate class.
     */
    private Coordinate coordinate;

    /**
     * Radius 20 * sqrt(2) units.
     */
    public static final double RADIUS_20SQRT2 = 28.284271247461902;

    /**
     * Radius 20 units.
     */
    public static final double RADIUS_20 = 20.0;

    /**
     * Radius 50 units.
     */
    public static final double RADIUS_50 = 50.0;

    /**
     * Angle 0°.
     */
    public static final double ANGLE_0 = 0.0;

    /**
     * Angle 45°.
     */
    public static final double ANGLE_45 = 45.0;

    /**
     * Angle 53,129°.
     */
    public static final double ANGLE_53P129 = 53.13010235415598;

    /**
     * Angle 90°.
     */
    public static final double ANGLE_90 = 90.0;

    /**
     * Angle 225° (inverse angle of 45°).
     */
    public static final double ANGLE_225 = 225.0;

    /**
     * Angle 360°.
     */
    public static final double ANGLE_360 = 360.0;

    /**
     * Unit 20 (for X and Y coordinates).
     */
    public static final long UNIT_20 = 20;

    /**
     * Unit 30 (for X and Y coordinates).
     */
    public static final long UNIT_30 = 30;

    /**
     * Unit 40 (for X and Y coordinates).
     */
    public static final long UNIT_40 = 40;

    /**
     * Polar accuracy.
     */
    public static final double POLAR_ACCURACY = 0.00000000000001;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public final void setUp() {
        coordinate = new Coordinate(0.0, 0.0);
    }

    /**
     * Tests empty value.
     */
    @Test
    public final void testNoValue() {
        assertEquals(0.0, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(0.0, coordinate.getPolarRadius(), POLAR_ACCURACY);
        assertEquals(0, coordinate.getCartesianX());
        assertEquals(0, coordinate.getCartesianY());

        double[] coordinatePolarArray = coordinate.getPolarCoordinate();
        assertEquals(
                0.0,
                coordinatePolarArray[Coordinate.RADIUS],
                POLAR_ACCURACY);
        assertEquals(
                ANGLE_0,
                coordinatePolarArray[Coordinate.ANGLE],
                POLAR_ACCURACY);

        long[] coordinateCartesianArray = coordinate.getCartesianCoordinate();
        assertEquals(0, coordinateCartesianArray[Coordinate.X]);
        assertEquals(0, coordinateCartesianArray[Coordinate.Y]);
    }

    /**
     * Tests setPolarCoordinate.
     */
    @Test
    public final void testSetPolarCoordinate() {
        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_45);
        assertEquals(ANGLE_45, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);

        // test zero radius
        coordinate.setPolarCoordinate(0.0, ANGLE_45);
        assertEquals(ANGLE_0, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(0.0, coordinate.getPolarRadius(), POLAR_ACCURACY);

        // test negative radius
        coordinate.setPolarCoordinate(RADIUS_20 * -1, ANGLE_45);
        assertEquals(ANGLE_225, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);

        // test 360° angle
        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_360);
        assertEquals(ANGLE_0, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);

        // test 45° + 1 turn angle (=405°)
        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_360 + ANGLE_45);
        assertEquals(ANGLE_45, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);
    }

    /**
     * Tests setCartesianCoordinate.
     */
    @Test
    public final void testSetCartesianCoordinate() {
        // test zero radius
        coordinate.setCartesianCoordinate(0, 0);
        assertEquals(ANGLE_0, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(0.0, coordinate.getPolarRadius(), POLAR_ACCURACY);

        coordinate.setCartesianCoordinate(UNIT_20, 0);
        assertEquals(ANGLE_0, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);

        coordinate.setCartesianCoordinate(0, UNIT_20);
        assertEquals(ANGLE_90, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);

        coordinate.setCartesianCoordinate(UNIT_20, UNIT_20);
        assertEquals(ANGLE_45, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(
                RADIUS_20SQRT2,
                coordinate.getPolarRadius(),
                POLAR_ACCURACY);

        coordinate.setCartesianCoordinate(UNIT_30, UNIT_40);
        assertEquals(ANGLE_53P129, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_50, coordinate.getPolarRadius(), POLAR_ACCURACY);

        coordinate.setCartesianCoordinate(-1 * UNIT_20, UNIT_20);
        assertEquals(
                ANGLE_90 + ANGLE_45,
                coordinate.getPolarAngle(),
                POLAR_ACCURACY);
        assertEquals(
                RADIUS_20SQRT2,
                coordinate.getPolarRadius(),
                POLAR_ACCURACY);

        coordinate.setCartesianCoordinate(-1 * UNIT_20, -1 * UNIT_20);
        assertEquals(ANGLE_225, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(
                RADIUS_20SQRT2,
                coordinate.getPolarRadius(),
                POLAR_ACCURACY);

        coordinate.setCartesianCoordinate(UNIT_20, -1 * UNIT_20);
        assertEquals(
                ANGLE_360 - ANGLE_45,
                coordinate.getPolarAngle(),
                POLAR_ACCURACY);
        assertEquals(
                RADIUS_20SQRT2,
                coordinate.getPolarRadius(),
                POLAR_ACCURACY);
    }

    /**
     * Tests setCoordinate.
     */
    @Test
    public final void testSetCoordinate() {
        Coordinate newCoordinate = new Coordinate(RADIUS_20, ANGLE_45);
        assertEquals(ANGLE_45, newCoordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(
                RADIUS_20,
                newCoordinate.getPolarRadius(),
                POLAR_ACCURACY);

        coordinate.setCoordinate(newCoordinate);
        assertEquals(ANGLE_45, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);
    }

    /**
     * Tests null value for new coordinate in setCoordinate.
     */
    @Test
    public final void testSetCoordinateNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter coordinate should not be null");

        coordinate.setCoordinate(null);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests polar Coordinate constructor.
     */
    @Test
    public final void testPolarConstructor() {
        coordinate = new Coordinate(RADIUS_20, ANGLE_45);
        assertEquals(ANGLE_45, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);
    }

    /**
     * Tests Cartesian Coordinate constructor.
     */
    @Test
    public final void testCartesianConstructor() {
        coordinate = new Coordinate(UNIT_30, UNIT_40);
        assertEquals(ANGLE_53P129, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_50, coordinate.getPolarRadius(), POLAR_ACCURACY);
    }

    /**
     * Tests Coordinate constructor.
     */
    @Test
    public final void testCoordinateConstructor() {
        coordinate = new Coordinate(new Coordinate(RADIUS_20, ANGLE_45));
        assertEquals(ANGLE_45, coordinate.getPolarAngle(), POLAR_ACCURACY);
        assertEquals(RADIUS_20, coordinate.getPolarRadius(), POLAR_ACCURACY);
    }

    /**
     * Tests getCartesianX/Y.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
    public final void testGetPolarCoordinateArray() {
        double[] coordinateArray;

        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_0);
        coordinateArray = coordinate.getPolarCoordinate();
        assertEquals(
                RADIUS_20,
                coordinateArray[Coordinate.RADIUS],
                POLAR_ACCURACY);
        assertEquals(
                ANGLE_0,
                coordinateArray[Coordinate.ANGLE],
                POLAR_ACCURACY);

        coordinate.setPolarCoordinate(RADIUS_20, ANGLE_90);
        coordinateArray = coordinate.getPolarCoordinate();
        assertEquals(
                RADIUS_20,
                coordinateArray[Coordinate.RADIUS],
                POLAR_ACCURACY);
        assertEquals(
                ANGLE_90,
                coordinateArray[Coordinate.ANGLE],
                POLAR_ACCURACY);

        coordinate.setPolarCoordinate(RADIUS_50, ANGLE_53P129);
        coordinateArray = coordinate.getPolarCoordinate();
        assertEquals(
                RADIUS_50,
                coordinateArray[Coordinate.RADIUS],
                POLAR_ACCURACY);
        assertEquals(
                ANGLE_53P129,
                coordinateArray[Coordinate.ANGLE],
                POLAR_ACCURACY);
    }
}
