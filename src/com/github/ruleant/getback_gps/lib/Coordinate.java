/**
 * 2D coordinate class, for converting between polar and Cartesian.
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

/**
 * 2D coordinate class, for converting between polar and Cartesian.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Coordinate {
    /**
     * Polar angle coordinate in degrees.
     */
    private double mAngle;

    /**
     * Polar radius coordinate.
     */
    private double mRadius;

    /**
     * X coordinate.
     */
    public static final int X = 0;

    /**
     * Y coordinate.
     */
    public static final int Y = 1;

    /**
     * Polar radius coordinate.
     */
    public static final int RADIUS = 0;

    /**
     * Polar angle coordinate.
     */
    public static final int ANGLE = 1;

    /**
     * Constructor.
     *
     * @param radius Radius coordinate
     * @param angle Angle coordinate in degrees
     */
    public Coordinate(final double radius, final double angle) {
        setPolarCoordinate(radius, angle);
    }

    /**
     * Constructor.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Coordinate(final long x, final long y) {
        setCartesianCoordinate(x, y);
    }

    /**
     * Constructor.
     *
     * @param coordinate New Coordinate
     */
    public Coordinate(final Coordinate coordinate) {
        setCoordinate(coordinate);
    }

    /**
     * Set coordinate in polar format.
     *
     * @param radius Radius coordinate
     * @param angle Angle coordinate in degrees
     */
    public final void setPolarCoordinate(final double radius,
                                         final double angle) {
        if (radius >= 0) {
            // if radius is a positive number
            mAngle = FormatUtils.normalizeAngle(angle);
            mRadius = radius;
        } else {
            // if radius is negative number
            // invert angle
            mAngle = FormatUtils.inverseAngle(angle);
            // use positive value for radius
            mRadius = Math.abs(radius);
        }
    }

    /**
     * Set coordinate in Cartesian format.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public final void setCartesianCoordinate(final long x, final long y) {
        // calculate radius
        mRadius = Math.sqrt(x * x + y * y);

        // calculate angle, atan2() returns the polar angle
        mAngle = FormatUtils.normalizeAngle(Math.toDegrees(Math.atan2(y, x)));
    }

    /**
     * Set coordinate with a Coordinate instance.
     *
     * @param coordinate New Coordinate
     */
    public final void setCoordinate(final Coordinate coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException(
                    "Parameter coordinate should not be null");
        }

        setPolarCoordinate(coordinate.getPolarRadius(),
                coordinate.getPolarAngle());
    }

    /**
     * Get coordinate in polar format.
     *
     * @return array with radius and angle in degrees
     */
    public final double[] getPolarCoordinate() {
        double[] coordinate = new double[2];

        coordinate[RADIUS] = getPolarRadius();
        coordinate[ANGLE] = getPolarAngle();

        return coordinate;
    }

    /**
     * Get polar angle coordinate.
     *
     * @return angle coordinate in degrees (0-360°)
     */
    public final double getPolarAngle() {
        if (mRadius > 0.0) {
            return mAngle;
        } else {
            return 0.0;
        }
    }

    /**
     * Get polar radius coordinate.
     *
     * @return radius coordinate
     */
    public final double getPolarRadius() {
        return mRadius;
    }

    /**
     * Get coordinate in Cartesian format.
     *
     * @return array with X and Y coordinate
     */
    public final long[] getCartesianCoordinate() {
        long[] coordinate = new long[2];

        coordinate[X] = getCartesianX();
        coordinate[Y] = getCartesianY();

        return coordinate;
    }

    /**
     * Get Cartesian X coordinate.
     *
     * @return X coordinate
     */
    public final long getCartesianX() {
        return Math.round(Math.cos(Math.toRadians(mAngle)) * mRadius);
    }

    /**
     * Get Cartesian Y coordinate.
     *
     * @return Y coordinate
     */
    public final long getCartesianY() {
        return Math.round(Math.sin(Math.toRadians(mAngle)) * mRadius);
    }
}
