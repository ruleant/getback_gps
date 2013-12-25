/**
 * Unit tests for Coordinates class
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
import com.github.ruleant.getback_gps.lib.Coordinates;

import junit.framework.TestCase;

/**
 * Unit tests for Coordinates class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class CoordinatesTest extends TestCase {
    /**
     * Instance of the coordinate class.
     */
    private Coordinates coordinates;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        coordinates = new Coordinates();
    }

    /**
     * Tests empty collection.
     */
    public final void testEmpty() {
        assertEquals(0, coordinates.getSize());
        assertEquals(0, coordinates.toArray().length);
        assertEquals(0, coordinates.toLinesArray().length);
    }

    /**
     * Tests adding Coordinate with polar coordinates.
     */
    public final void testAddPolarCoordinate() {
        coordinates.addCoordinate(CoordinateTest.RADIUS_20,
                                    CoordinateTest.ANGLE_45);
        assertEquals(1, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(1, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.ANGLE_45, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests adding Coordinate with Cartesian coordinates.
     */
    public final void testAddCartesianCoordinate() {
        coordinates.addCoordinate(CoordinateTest.UNIT_20,
                                    CoordinateTest.UNIT_30);
        assertEquals(1, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(1, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.UNIT_20, coordinate.getCartesianX());
        assertEquals(CoordinateTest.UNIT_30, coordinate.getCartesianY());
    }

    /**
     * Tests adding Coordinate with Coordinate instance.
     */
    public final void testAddCoordinate() {
        coordinates.addCoordinate(new Coordinate(CoordinateTest.RADIUS_20,
                                    CoordinateTest.ANGLE_45));
        assertEquals(1, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(1, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.ANGLE_45, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_20, coordinate.getPolarRadius());
    }

    /**
     * Tests null value for new coordinate in setCoordinate.
     */
    public final void testSetCoordinateNull() {
        try {
            coordinates.addCoordinate(null);
            fail("should have thrown exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Parameter coordinate should not be null",
                    e.getMessage());
        }
    }

    /**
     * Tests adding multiple Coordinates.
     */
    public final void testAddMultipleCoordinates() {
        coordinates.addCoordinate(CoordinateTest.RADIUS_20,
                                    CoordinateTest.ANGLE_45);
        assertEquals(1, coordinates.getSize());

        coordinates.addCoordinate(CoordinateTest.RADIUS_50,
                                    CoordinateTest.ANGLE_90);

        assertEquals(2, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(2, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.ANGLE_45, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_20, coordinate.getPolarRadius());

        coordinate = (Coordinate) coordinatesArray[1];
        assertEquals(CoordinateTest.ANGLE_90, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_50, coordinate.getPolarRadius());
    }

    /**
     * Tests toLinesArray, one line.
     */
    public final void testToLinesArray() {
        coordinates.addCoordinate(0, CoordinateTest.UNIT_20);
        // array should be empty in only 1 point is added,
        // two lines are needed to draw a line
        assertEquals(0, coordinates.toLinesArray().length);

        coordinates.addCoordinate(CoordinateTest.UNIT_30, CoordinateTest.UNIT_40);
        float[] coordinatesArray = coordinates.toLinesArray();
        assertEquals(Coordinates.NUM_COORD_LINE, coordinatesArray.length);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_X]);
        assertEquals((float) CoordinateTest.UNIT_20,
                coordinatesArray[Coordinates.POS_START_Y]);
        assertEquals((float) CoordinateTest.UNIT_30,
                coordinatesArray[Coordinates.POS_END_X]);
        assertEquals((float) CoordinateTest.UNIT_40,
                coordinatesArray[Coordinates.POS_END_Y]);
    }

    /**
     * Tests toLinesArray, multiple lines.
     */
    public final void testToLinesArrayMulti() {
        coordinates.addCoordinate(0, 0);
        coordinates.addCoordinate(0, CoordinateTest.UNIT_20);
        coordinates.addCoordinate(CoordinateTest.UNIT_30,
                CoordinateTest.UNIT_40);
        float[] coordinatesArray = coordinates.toLinesArray();
        assertEquals(3 * Coordinates.NUM_COORD_LINE, coordinatesArray.length);
        // first line
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_X]);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_START_Y]);
        assertEquals((float) 0, coordinatesArray[Coordinates.POS_END_X]);
        assertEquals((float) CoordinateTest.UNIT_20,
                coordinatesArray[Coordinates.POS_END_Y]);

        // second line
        assertEquals((float) 0,
                coordinatesArray[Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_START_X]);
        assertEquals((float) CoordinateTest.UNIT_20,
                coordinatesArray[Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_START_Y]);
        assertEquals((float) CoordinateTest.UNIT_30,
                coordinatesArray[Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_END_X]);
        assertEquals((float) CoordinateTest.UNIT_40,
                coordinatesArray[Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_END_Y]);

        // closing line
        assertEquals((float) CoordinateTest.UNIT_30,
                coordinatesArray[2 * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_START_X]);
        assertEquals((float) CoordinateTest.UNIT_40,
                coordinatesArray[2 * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_START_Y]);
        assertEquals((float) 0,
                coordinatesArray[2 * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_END_X]);
        assertEquals((float) 0,
                coordinatesArray[2 * Coordinates.NUM_COORD_LINE
                        + Coordinates.POS_END_Y]);
    }
}
