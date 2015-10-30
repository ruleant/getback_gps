/**
 * Class to format distances, speeds and numbers.
 *
 * Copyright (C) 2010 Peer internet solutions
 * Copyright (C) 2013-2015 Dieter Adriaenssens
 *
 * Method formatDist() in this file is based on method formatDist
 * in class MixUtils that is part of mixare.
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
 * @package com.github.ruleant.getback_gps.lib
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

import com.github.ruleant.getback_gps.R;

import java.util.Locale;

/**
 * Methods to convert values to formatted string.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class FormatUtils {
    /**
     * Speed unit : km/h.
     */
    public static final String SPEED_KPH = "km/h";

    /**
     * Conversion rate from m/s to km/h.
     */
    private static final double SPEED_CONV_MPS_KPH = 3.6; // 3600s/1000m

    /**
     * Conversion rate from kilometer to meter.
     */
    private static final double CONV_KM_M = 1000.0;

    /**
     * 1 decimal difference.
     */
    private static final double ONE_DEC = 10.0;

    /**
     * Minimal angle value = 0°.
     */
    public static final float CIRCLE_ZERO = 0;

    /**
     * First quarter angle value = 1/2 PI = 90°.
     */
    public static final float CIRCLE_1Q = 90;

    /**
     * Halfway angle value = PI = 180°.
     */
    public static final float CIRCLE_HALF = 180;

    /**
     * Third quarter angle value = 3/2 PI = 270°.
     */
    public static final float CIRCLE_3Q = 270;

    /**
     * Maximal angle value = 360°.
     */
    public static final float CIRCLE_FULL = 360;



    /**
     * Hidden constructor, to prevent instantiating.
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
    public static String formatDist(final double distance) {
        return formatDist(distance, null);
    }

    /**
     * Formats a distance (in meter) to a string,
     * in meter or kilometer, depending on the size.
     * The number format is localized.
     *
     * @param distance distance in m
     * @param context App context
     * @return formatted distance with unit (m or km)
     */
    public static String formatDist(final double distance, final Context context) {
        String shortUnit = "m";
        String longUnit = "km";
        double scaleUnit = CONV_KM_M;

        // if context is defined, use android string
        if (context != null) {
            shortUnit = context.getResources().getString(R.string.distance_m);
            longUnit = context.getResources().getString(R.string.distance_km);
        }

        // distance shouldn't be negative
        double distanceAbs = Math.abs(distance);

        // conversion and formatting
        if (Math.round(distanceAbs) < scaleUnit) {
            // display as short unit, as integer
            return String.format(Locale.getDefault(), "%1$d%2$s",
                    Math.round(distanceAbs), shortUnit);
        } else {
            double scaledDistance = distanceAbs / scaleUnit;
            // round to one decimal and check if it is
            // smaller than a 1 decimal difference
            if ((Math.round(scaledDistance * ONE_DEC) / ONE_DEC) < ONE_DEC) {
                // display as long unit, with 1 decimal
                return String.format(
                        Locale.getDefault(), "%1$,.1f%2$s",
                        scaledDistance, longUnit);
            } else {
                // display as long unit, as integer
                return String.format(
                        Locale.getDefault(), "%1$,d%2$s",
                        Math.round(scaledDistance), longUnit);
            }
        }
    }

    /**
     * Formats a height (in meter) to a string, in meter.
     * The number format is localized.
     *
     * @param height height in m
     * @return formatted height with unit (m)
     */
    public static String formatHeight(final double height) {
        return formatHeight(height, null);
    }

    /**
     * Formats a height (in meter) to a string, in meter.
     * The number format is localized.
     *
     * @param height height in m
     * @return formatted height with unit (m)
     */
    public static String formatHeight(final double height, final Context context) {
        String unit = "m";

        // if context is defined, use android string
        if (context != null) {
            unit = context.getResources().getString(R.string.distance_m);
        }

        return String.format(
                Locale.getDefault(), "%1$,d%2$s",
                Math.round(height), unit);
    }

    /**
     * Formats a speed (in meter per second (m/s)) to a string,
     * in kilometer per hour (km/h).
     * The number format is localized.
     *
     * @param speed speed in m/s
     * @return formatted speed with unit (km/h)
     */
    public static String formatSpeed(final double speed) {
        return formatSpeed(speed, null);
    }

    /**
     * Formats a distance (in meter per second (m/s)) to a string,
     * in kilometer per hour (km/h).
     * The number format is localized and speed unit is translatable.
     *
     * @param speed speed in m/s
     * @param context App context.
     * @return formatted speed with unit (km/h)
     */
    public static String formatSpeed(final double speed,
                                     final Context context) {
        String unit = SPEED_KPH;

        // if context is defined, use android string
        if (context != null) {
            unit = context.getResources().getString(R.string.speed_kph);
        }

        // speed shouldn't be negative, conversion to kph
        double convertedSpeed = Math.abs(speed) * SPEED_CONV_MPS_KPH;

        // formatting
        if (convertedSpeed < ONE_DEC) {
            // display with 1 decimal
            return String.format(
                    Locale.getDefault(), "%1$,.1f%2$s", convertedSpeed, unit);
        } else {
            // display as integer
            return String.format(
                    Locale.getDefault(), "%1$,d%2$s",
                    Math.round(convertedSpeed), unit);
        }
    }

    /**
     * Formats an angle (in °) to a string.
     * The number format is localized.
     *
     * @param angle Angle in °
     * @param precision number of decimals
     * @return formatted angle with unit (°)
     */
    public static String formatAngle(final double angle, final int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException(
                    "Precision can't be a negative value");
        }

        String unit = "°";

        // generate format string
        // format number with variable precision (%s.xf), with x = precision
        String formatString = "%1$." + String.format(Locale.US, "%d", precision) + "f";
        // add unit
        formatString += "%2$s";

        // formatting
        return String.format(Locale.getDefault(), formatString, angle, unit);
    }

    /**
     * Normalize an angle to be in the range 0°-360°.
     *
     * @param angle Angle in degrees
     * @return Normalized angle in range 0°-360°
     */
    public static double normalizeAngle(final double angle) {
        // TODO low refactor to also work if range would be -180°-180°
        float range = CIRCLE_FULL - CIRCLE_ZERO;
        double convertedAngle = angle;

        // returned value should be between 0° and 360°
        if (angle < CIRCLE_ZERO) {
            convertedAngle += range * Math.ceil(Math.abs(angle / range));
        } else if (angle >= CIRCLE_FULL) {
            convertedAngle -= range * Math.floor(angle / range);
        }

        return convertedAngle;
    }

    /**
     * Get an inverse angle, ie. angle pointing in opposite direction.
     *
     * @param angle Angle in degrees (0°-360°)
     * @return angle in opposite direction (0°-360°)
     */
    public static double inverseAngle(final double angle) {
        return normalizeAngle(angle - CIRCLE_HALF);
    }

    /**
     * Localize location provider name.
     *
     * @param context      Context of the app
     * @param providerName Name of the location provider
     * @return Localized location provider name
     */
    public static String localizeProviderName(
            final Context context, final String providerName) {
        String l10nProviderName = providerName;

        try {
            Resources res = context.getResources();

            if ("network".equals(providerName)) {
                l10nProviderName = res.getString(R.string.loc_provider_network);
            } else if ("gps".equals(providerName)) {
                l10nProviderName = res.getString(R.string.loc_provider_gps);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return l10nProviderName;
    }
}
