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

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(0, coordinatesArray.length);
    }

    /**
     * Tests adding Coordinate with polar coordinates.
     */
    public final void testaddPolarCoordinate() {
        coordinates.addCoordinate(CoordinateTest.RADIUS_20, CoordinateTest.ANGLE_45);
        assertEquals(1, coordinates.getSize());

        Object[] coordinatesArray = coordinates.toArray();
        assertEquals(1, coordinatesArray.length);

        Coordinate coordinate = (Coordinate) coordinatesArray[0];
        assertEquals(CoordinateTest.ANGLE_45, coordinate.getPolarAngle());
        assertEquals(CoordinateTest.RADIUS_20, coordinate.getPolarRadius());
    }
}
