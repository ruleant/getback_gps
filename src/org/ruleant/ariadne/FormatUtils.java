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
    public final static String formatDist(double distance) {
        String shortUnit = "m";
        String longUnit = "km";
        double scaleUnit = 1000.0;

        // distance shouldn't be negative
        distance = Math.abs(distance);

        // conversion and formatting
        if (distance < scaleUnit) {
            // display as short unit, as integer
            return String.format("%1$d%2$s", (int) distance, shortUnit);
        } else if (distance < (scaleUnit * 10)) {
            // display as long unit, with 1 decimal
            return String.format(
                "%1$,.1f%2$s", (distance / scaleUnit), longUnit);
        } else {
            // display as long unit, as integer
            return String.format(
                "%1$,d%2$s", (int) (distance / scaleUnit), longUnit);
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
    public final static String formatSpeed(double speed) {
        // TODO use translated string
        String unit = "km/h";
        double conversionRate = 3.6; // 3600s/1000m

        // speed shouldn't be negative
        speed = Math.abs(speed);

        // conversion
        double convertedSpeed = speed * conversionRate;

        // formatting
        if (convertedSpeed < 10.0) {
            // display with 1 decimal
            return String.format(
                "%1$,.1f%2$s", convertedSpeed, unit);
        } else {
            // display as integer
            return String.format(
                "%1$,d%2$s", (int) convertedSpeed, unit);
        }
    }
}
