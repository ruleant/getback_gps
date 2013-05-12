/**
 * Class to format distances, speeds and numbers.
 *
 * Copyright (C) 2010 Peer internet solutions
 * Copyright (C) 2013 Dieter Adriaenssens
 *
 * This file is based on a class that is part of mixare.
 * The original source can be found on :
 * http://www.java2s.com/Code/Android/Date-Type/FormatDistance.htm
 * See commit logs for changes.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *
 * @package org.ruleant.ariadne
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

/**
 * Methods to convert values to formatted string.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class FormatUtils {
    /**
     * Conversion rate from m/s to km/h.
     */
    public static final double SPEED_CONV_MPS_KPH = 3.6; // 3600s/1000m

    /**
     * Conversion rate from kilometer to meter.
     */
    public static final double CONV_KM_M = 1000.0;

    /**
     * 1 decimal difference.
     */
    private static final double ONE_DEC = 10.0;

    /**
     * Minimal angle value = 0°.
     */
    private static final float MIN_ANGLE = 0;

    /**
     * Maximal angle value = 360°.
     */
    private static final float MAX_ANGLE = 360;

    /**
     * Hidden constructor.
     */
    protected FormatUtils() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }
    /**
     * Formats a distance (in meter) to a string,
     * in meter or kilometer, depending on the size.
     * The number format is localized.
     *
     * @param distance distance in m
     * @return formatted distance with unit (m or km)
     */
    public static final String formatDist(double distance) {
        String shortUnit = "m";
        String longUnit = "km";
        double scaleUnit = CONV_KM_M;

        // distance shouldn't be negative
        distance = Math.abs(distance);

        // conversion and formatting
        if (Math.round(distance) < scaleUnit) {
            // display as short unit, as integer
            return String.format("%1$d%2$s", Math.round(distance), shortUnit);
        } else {
            double scaledDistance = (distance / scaleUnit);
            // round to one decimal and check if it is
            // smaller than a 1 decimal difference
            if ((Math.round(scaledDistance * ONE_DEC) / ONE_DEC) < ONE_DEC) {
                // display as long unit, with 1 decimal
                return String.format(
                    "%1$,.1f%2$s", scaledDistance, longUnit);
            } else {
                // display as long unit, as integer
                return String.format(
                    "%1$,d%2$s", Math.round(scaledDistance), longUnit);
            }
        }
    }

    /**
     * Formats a distance (in meter per second (m/s)) to a string,
     * in kilometer per hour (km/h).
     * The number format is localized.
     *
     * @param speed speed in m/s
     * @return formatted speed with unit (km/h)
     */
    public static final String formatSpeed(double speed) {
        // TODO use translated string
        String unit = "km/h";

        // speed shouldn't be negative
        speed = Math.abs(speed);

        // conversion
        double convertedSpeed = speed * SPEED_CONV_MPS_KPH;

        // formatting
        if (convertedSpeed < ONE_DEC) {
            // display with 1 decimal
            return String.format(
                "%1$,.1f%2$s", convertedSpeed, unit);
        } else {
            // display as integer
            return String.format(
                "%1$,d%2$s", Math.round(convertedSpeed), unit);
        }
    }

    /**
     * Formats an angle (in °) to a string.
     * The number format is localized.
     *
     * @param angle Angle in °
     * @return formatted angle with unit (°)
     */
    public static final String formatAngle(double angle) {
        String unit = "°";

        // conversion
        double normalizedAngle = normalizeAngle(angle);

        // formatting
        return String.format("%1$.2f%2$s", normalizedAngle, unit);
    }

    /**
     * Normalize an angle to be in the range 0°-360°.
     *
     * @param angle Angle in degrees
     * @return Normalized angle in range 0°-360°
     * @todo low refactor to also work if range would be -180°-180°
     */
    public static final double normalizeAngle(double angle) {
        float range = MAX_ANGLE - MIN_ANGLE;

        // returned value should be between 0° and 360°
        if (angle < MIN_ANGLE) {
            angle += range * Math.ceil(Math.abs(angle / range));
        } else if (angle >= MAX_ANGLE) {
            angle -= range * Math.floor(angle / range);
        }

        return angle;
    }
}
