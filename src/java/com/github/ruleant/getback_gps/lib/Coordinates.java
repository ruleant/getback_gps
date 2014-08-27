/**
 * Coordinate class container.
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
 * @package com.github.ruleant.getback_gps.lib
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.Iterator;

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
    private final ArrayList<Coordinate> mCoordinates;

    /**
     * Iterator for Coordinates collection.
     */
    private Iterator<Coordinate> mCoordinateIterator;

    /**
     * Coordinate converter.
     */
    private CoordinateConverterInterface mCoordinateConverter;

    /**
     * Close line when exporting sets of coordinates.
     */
    private boolean mCloseLine = true;

    /**
     * Number of coordinates per line : x,y of start and end point.
     */
    public static final int NUM_COORD_LINE = 4;

    /**
     * Position in array of X coordinate of the start point.
     */
    public static final int POS_START_X = 0;

    /**
     * Position in array of Y coordinate of the start point.
     */
    public static final int POS_START_Y = 1;

    /**
     * Position in array of X coordinate of the end point.
     */
    public static final int POS_END_X = 2;

    /**
     * Position in array of Y coordinate of the end point.
     */
    public static final int POS_END_Y = 3;

    /**
     * Constructor.
     */
    public Coordinates() {
        mCoordinates = new ArrayList<Coordinate>();
    }

    /**
     * Constructor.
     *
     * @param converter CoordinateConverter class
     */
    public Coordinates(final CoordinateConverterInterface converter) {
        this();
        setCoordinateConverter(converter);
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
     * Set CoordinateConverter.
     *
     * @param converter CoordinateConverter class
     */
    public final void setCoordinateConverter(
            final CoordinateConverterInterface converter) {
        if (converter == null) {
            throw new IllegalArgumentException(
                    "Parameter converter should not be null");
        }

        mCoordinateConverter = converter;
    }

    /**
     * Close line (add a line from last to first point)
     * when exporting sets of coordinates.
     * By default, the line is closed.
     *
     * @param close set to true to close the line
     */
    public final void setCloseLine(final boolean close) {
        mCloseLine = close;
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

        // 2 points or more are required to draw a line
        if (getSize() <= 1) {
            return path;
        }

        long[] firstPoint = getFirstCoordinateCartesian();
        // skip point if it is null
        while (firstPoint == null) {
            firstPoint = getNextCoordinateCartesian();
        }

        path.moveTo(firstPoint[Coordinate.X], firstPoint[Coordinate.Y]);

        long[] currentPoint;
        while (mCoordinateIterator.hasNext()) {
            currentPoint = getNextCoordinateCartesian();

            // skip point if it is null
            if (currentPoint != null) {
                path.lineTo(currentPoint[Coordinate.X], currentPoint[Coordinate.Y]);
            }
        }

        if (mCloseLine) {
            path.close();
        }

        return path;
    }

    /**
     * Return Coordinates as Canvas.DrawLines array.
     *
     * @return coordinates as array
     */
    public final float[] toLinesArray() {
        int totalPoints = getSize();

        // 2 points or more are required to draw a line
        if (totalPoints <= 1) {
            return new float[0];
        }

        int arrayLength;

        // only 1 line between 2 points (no need to close),
        // else use class property mCloseLine
        boolean closeLine = totalPoints != 2 && mCloseLine;

        if (closeLine) {
            // calculate array length, based on number of coordinates
            // length = #points * 4 (=number of coordinates needed to draw
            // a line between 2 points)
            arrayLength = totalPoints * NUM_COORD_LINE;
        } else {
            // when not closing, one set of coordinates less is needed.
            arrayLength = (totalPoints - 1) * NUM_COORD_LINE;
        }

        float[] array = new float[arrayLength];

        long[] firstPoint = getFirstCoordinateCartesian();
        // skip point if it is null
        while (firstPoint == null) {
            firstPoint = getNextCoordinateCartesian();
        }

        long[] previousPoint = firstPoint;
        long[] currentPoint;
        int arrayPosition = 0;

        while (mCoordinateIterator.hasNext()) {
            currentPoint = getNextCoordinateCartesian();

            // skip point if it is null
            if (currentPoint == null) {
                continue;
            }

            // prevent overfilling array in case the
            // coordinates collection changes
            if (arrayPosition >= arrayLength) {
                return array;
            }

            // add next line
            array[arrayPosition + POS_START_X] = previousPoint[Coordinate.X];
            array[arrayPosition + POS_START_Y] = previousPoint[Coordinate.Y];
            array[arrayPosition + POS_END_X] = currentPoint[Coordinate.X];
            array[arrayPosition + POS_END_Y] = currentPoint[Coordinate.Y];

            arrayPosition += NUM_COORD_LINE;
            previousPoint = currentPoint;
        }

        // prevent overfilling array in case the
        // coordinates collection changes
        // or when line doesn't need to be closed
        if (!closeLine || arrayPosition >= arrayLength) {
            return array;
        }

        // close figure
        array[arrayPosition + POS_START_X] = previousPoint[Coordinate.X];
        array[arrayPosition + POS_START_Y] = previousPoint[Coordinate.Y];
        array[arrayPosition + POS_END_X] = firstPoint[Coordinate.X];
        array[arrayPosition + POS_END_Y] = firstPoint[Coordinate.Y];

        return array;
    }

    /**
     * Get first Coordinate and return it as Cartesian coordinate array.
     *
     * @return Cartesian coordinate array
     */
    private long[] getFirstCoordinateCartesian() {
        mCoordinateIterator = mCoordinates.iterator();
        return getNextCoordinateCartesian();
    }

    /**
     * Get next Coordinate and return it as Cartesian coordinate array.
     *
     * @return Cartesian coordinate array
     */
    private long[] getNextCoordinateCartesian() {
        if (mCoordinateIterator != null && mCoordinateIterator.hasNext()) {
            return convertCoordinate(mCoordinateIterator.next())
                    .getCartesianCoordinate();
        }

        return null;
    }

    /**
     * Converts a Coordinate using the defined CoordinateConverter class,
     * it returns an unconverted Coordinate if the CoordinateConverter class
     * is not set.
     *
     * @param coordinate Unconverted coordinate
     * @return Converted coordinate
     */
    private Coordinate convertCoordinate(final Coordinate coordinate) {
        // if CoordinateConverter is not set, return coordinate unconverted
        if (mCoordinateConverter == null) {
            return coordinate;
        }

        // convert Coordinate using defined CoordinateConverter
        return mCoordinateConverter.getConvertedCoordinate(coordinate);
    }
}
