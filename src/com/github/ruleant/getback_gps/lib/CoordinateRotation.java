/**
 * Coordinate converter class rotating a set of coordinates.
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
package com.github.ruleant.getback_gps.lib;

/**
 * Coordinate converter class rotating a set of coordinates.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class CoordinateRotation  implements CoordinateConverterInterface {
    /**
     * Rotation angle in radians (0-2*PI).
     */
    private double mAngle;

    /**
     * Rotation center.
     */
    private Coordinate mCenter;

    /**
     * Maximum radius.
     */
    private double mMaxRadius;

    /**
     * Constructor.
     *
     * @param center Rotation center coordinate
     * @param angle Rotation angle to apply to coordinate (0-360°)
     * @param maxRadius Maximum value of the radius
     */
    public CoordinateRotation(final Coordinate center, final double angle,
                              final double maxRadius) {
        setRotationCenter(center);
        setRotationAngle(angle);
        setMaxRadius(maxRadius);
    }

    /**
     * Sets rotation angle.
     *
     * @param angle Rotation angle to apply to coordinate (0-360°)
     */
    public final void setRotationAngle(final double angle) {
        mAngle = Math.toRadians(FormatUtils.normalizeAngle(angle));
    }

    /**
     * Sets rotation center.
     *
     * @param center Rotation center coordinate
     */
    public final void setRotationCenter(final Coordinate center) {
        mCenter = center;
    }

    /**
     * Sets maximum radius.
     *
     * @param maxRadius Maximum value of the radius
     */
    public final void setMaxRadius(final double maxRadius) {
        mMaxRadius = maxRadius;
    }

    /**
     * Rotate coordinates with an angle, around a center.
     *
     * A couple of transformations are applied to convert the coordinates :
     * - to clockwise : a => -a
     * - rotate 90° counter-clockwise : -a => PI/2 - a
     * X = cos(PI/2 - a) = sin(a)
     * Y = sin(PI/2 - a) = cos(a)
     * - flip Y coordinate
     * Y = -cos(a)
     *
     * @param coordinate Unconverted coordinate
     * @return Converted coordinate
     */
    public final Coordinate getConvertedCoordinate(
            final Coordinate coordinate) {
        double angle = Math.toRadians(coordinate.getPolarAngle()) + mAngle;
        double radius = coordinate.getPolarRadius() * mMaxRadius;

        // Transform angle and convert to Cartesian
        return new Coordinate(
                mCenter.getCartesianX() + Math.round(Math.sin(angle) * radius),
                mCenter.getCartesianY() - Math.round(Math.cos(angle) * radius));
    }
}
