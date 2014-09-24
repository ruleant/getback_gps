/**
 * Implementation of a averaging a value with circular limits.
 * When a value reaches the end of the range, it can grow bigger to
 * a value that starts in the lower end of the range,
 * fe. 360° = 0°, 370° = 10°, 350° = -10°.
 *
 * Copyright (C) 2014 Dieter Adriaenssens
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
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

/**
 * Method to calculate the average value of a circular range.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class CircularAverage {
    /**
     * Minimal angle value = 0°.
     */
    private static final float CROSS_WINDOW = 180;

    /**
     * Hidden constructor, to prevent instantiating.
     */
    protected CircularAverage() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }

    /**
     * Calculates the average value of a circular range.
     *
     * @param previousValue previous value
     * @param newValue      new value
     * @param alpha         Alpha value of low pass filter (valid range : 0-1)
     * @return average value
     */
    public static float getAverageValue(
            final float previousValue, final float newValue,
            final float alpha) {
        // alpha value range is checked in LowPassFilter

        float lPreviousValue = previousValue;
        float lNewValue = newValue;

        // increase new value with 360° in case maximum is crossed.
        if (newValue > FormatUtils.CIRCLE_ZERO && newValue < CROSS_WINDOW
                && previousValue > (FormatUtils.CIRCLE_FULL - CROSS_WINDOW)
                && previousValue < FormatUtils.CIRCLE_FULL
                && Math.abs(newValue + FormatUtils.CIRCLE_FULL - previousValue)
                    < CROSS_WINDOW) {
            lNewValue += FormatUtils.CIRCLE_FULL;
            // increase previous value with 360° in case minimum is crossed.
        } else if (newValue > (FormatUtils.CIRCLE_FULL - CROSS_WINDOW)
                && newValue < FormatUtils.CIRCLE_FULL
                && previousValue > FormatUtils.CIRCLE_ZERO
                && previousValue < CROSS_WINDOW
                && Math.abs(previousValue + FormatUtils.CIRCLE_FULL - newValue)
                    < CROSS_WINDOW) {
            lPreviousValue += FormatUtils.CIRCLE_FULL;
        }

        return (float) FormatUtils.normalizeAngle(
                LowPassFilter.filterValue(lPreviousValue, lNewValue, alpha));
    }
}
