/**
 * Coordinate class container.
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

import android.graphics.Path;

import java.util.ArrayList;

/**
 * Coordinates is a container that can contain
 * multiple instances of a Coordinate class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Coordinates {
    /**
     * Collection of Coordinate instances.
     */
    private ArrayList<Coordinate> mCoordinates;

    /**
     * Number of coordinates per line : x,y of start and end point.
     */
    private static final int NUM_COORD_LINE = 4;

    /**
     * Constructor.
     */
    public Coordinates() {
        mCoordinates = new ArrayList<Coordinate>();
    }

    /**
     * Add coordinate in polar format.
     *
     * @param radius Radius coordinate
     * @param angle Angle coordinate in degrees
     */
    public final void addCoordinate(final double radius, final double angle) {
        addCoordinate(new Coordinate(radius, angle));
    }

    /**
     * Add coordinate in Cartesian format.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public final void addCoordinate(final long x, final long y) {
        addCoordinate(new Coordinate(x, y));
    }

    /**
     * Add coordinate with a Coordinate instance.
     *
     * @param coordinate New Coordinate
     */
    public final void addCoordinate(final Coordinate coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException(
                    "Parameter coordinate should not be null");
        }

        mCoordinates.add(coordinate);
    }

    /**
     * Get size of the Coordinates collection.
     *
     * @return number of Coordinate instances
     */
    public final int getSize() {
        return mCoordinates.size();
    }

    /**
     * Return Coordinates collection in array format.
     *
     * @return array with Coordinate instances
     */
    public final Object[] toArray() {
        return mCoordinates.toArray();
    }

    /**
     * Return Coordinates as Path.
     *
     * @return coordinates as Path instance
     */
    public final Path toPath() {
        Path path = new Path();

        // TODO use conversion/iterator to create all points of the path
        /*path.moveTo(coordinateX, coordinateY);
        path.lineTo(coordinateX, coordinateY);
        path.close();*/

        return path;
    }

    /**
     * Return Coordinates as Canvas.DrawLines array.
     *
     * @return coordinates as array
     */
    public final float[] toLinesArray() {
        // calculate array length, based on number of coordinates
        // length = #points * 4 (=number of coordinates needed to draw
        // a line between 2 points)
        int arrayLength = getSize() * NUM_COORD_LINE;

        float[] array = new float[arrayLength];

        // TODO use conversion/iterator to create all points of the array

        return array;
    }
}
