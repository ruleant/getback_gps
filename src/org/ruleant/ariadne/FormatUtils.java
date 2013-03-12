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
 * Methods to convert values to formatted string
 *
 * @package org.ruleant.ariadne
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
class FormatUtils {
    /**
     * Formats a distance (in meter) to a string,
     * in meter or kilometer, depending on the size.
     *
     * @param meters distance in m
     * @return formatted distance with unit (m or km)
     */
    public static String formatDist(double meters) {
        String shortUnit = "m";
        String longUnit = "km";
        double scaleUnit = 1000.0;

        // conversion and formatting
        if (meters < scaleUnit) {
            return String.format("%1$d%2$s", (int) meters, shortUnit);
        } else if (meters < (scaleUnit * 10)) {
            return String.format("%1$,.1f%2$s", (meters / scaleUnit), longUnit);
        } else {
            return String.format("%1$,d%2$s", (int) (meters / scaleUnit), longUnit);
        }
    }
}