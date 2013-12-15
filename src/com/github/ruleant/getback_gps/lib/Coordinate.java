/**
 * 2D coordinate class, for converting between polar and Cartesian.
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

/**
 * 2D coordinate class, for converting between polar and Cartesian.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Coordinate {
    /**
     * Polar angle coordinate.
     */
    private double mAngle;

    /**
     * Polar radius coordinate.
     */
    private double mRadius;

    /**
     * Constructor.
     *
     * @param angle Angle coordinate
     * @param radius Radius coordinate
     */
    public Coordinate(final double angle, final double radius) {
        setPolarCoordinate(angle, radius);
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
     * Set coordinate in polar format.
     *
     * @param angle Angle coordinate
     * @param radius Radius coordinate
     */
    public final void setPolarCoordinate(final double angle,
                                         final double radius) {

    }

    /**
     * Set coordinate in Cartesian format.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public final void setCartesianCoordinate(final long x, final long y) {

    }

    /**
     * Get coordinate in polar format.
     *
     * @return array with angle and radius
     */
    public final double[] getPolarCoordinate() {
        return null;
    }

    /**
     * Get polar angle coordinate.
     *
     * @return angle coordinate (0-360°)
     */
    public final double getPolarAngle() {
        return 0;
    }

    /**
     * Get polar radius coordinate.
     *
     * @return radius coordinate
     */
    public final double getPolarRadius() {
        return 0;
    }

    /**
     * Get coordinate in cartesian format.
     *
     * @return array with X and Y coordinate
     */
    public final long[] getCartesianCoordinate() {
        return null;
    }

    /**
     * Get Cartesian X coordinate.
     *
     * @return X coordinate
     */
    public final long getCartesianX() {
        return 0;
    }

    /**
     * Get Cartesian Y coordinate.
     *
     * @return Y coordinate
     */
    public final long getCartesianY() {
        return 0;
    }
}
