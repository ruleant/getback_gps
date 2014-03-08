/**
 * Custom Ariadne Location object
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;

import com.github.ruleant.getback_gps.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom Ariadne Location object, it inherits from Location class,
 * but overrides the getString() method.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class AriadneLocation extends Location {
    /**
     * Location timestamp expiration,
     * 5 minutes in milliseconds (5 * 60 * 1000).
     */
    private static final long LOC_EXPIRE = 300000;

    /**
     * Constructor.
     *
     * @param provider Provider name
     */
    public AriadneLocation(final String provider) {
        super(provider);
    }

    /**
     * Constructor.
     *
     * @param location Location object
     */
    public AriadneLocation(final Location location) {
        super(location);
    }

    /**
     * Checks if the timestamp of the provided location
     * is more recent than this location.
     *
     * @param location Location object
     * @return true if location is more recent than this location
     */
    public final boolean isNewer(final Location location) {
        return location.getTime() > super.getTime();
    }

    /**
     * Checks if location timestamp is recent.
     *
     * @return true if location is recent.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean isRecent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Tools.isTimestampRecent(getTime(), LOC_EXPIRE);
        } else {
            // use getElapsedRealtimeNanos when using API 17 or higher
            return Tools.isTimestampNanoRecent(getElapsedRealtimeNanos(),
                    LOC_EXPIRE * Tools.MILLI_IN_NANO);
        }
    }

    /**
     * Returns a formatted String representing the object.
     *
     * @param context Context of the App
     * @return formatted string
     */
    public final String toFormattedString(final Context context) {
        Resources res = context.getResources();
        DebugLevel debug = new DebugLevel(context);

        String locationText = "";

        // Format location
        Latitude latitude = new Latitude(context, getLatitude());
        locationText += " "
                + res.getString(R.string.latitude) + ": " + latitude.format();

        Longitude longitude = new Longitude(context, getLongitude());
        locationText += "\n "
                + res.getString(R.string.longitude) + ": " + longitude.format();

        if (hasAltitude()) {
            locationText += "\n "
                    + res.getString(R.string.altitude) + ": "
                    + FormatUtils.formatDist(getAltitude());
        }
        if (hasBearing()) {
            CardinalDirection cd = new CardinalDirection(context, getBearing());

            locationText += "\n "
                    + res.getString(R.string.bearing) + ": "
                    + cd.format();
        }
        if (hasSpeed()) {
            locationText += "\n "
                    + res.getString(R.string.speed) + ": "
                    + FormatUtils.formatSpeed(getSpeed(), context);
        }
        if (hasAccuracy()) {
            locationText += "\n "
                    + res.getString(R.string.accuracy) + ": "
                    + FormatUtils.formatDist(getAccuracy());
        }

        // Location provider
        String providerName = getProvider();
        if (providerName != null && providerName.length() > 0) {
            locationText += "\n "
                    + res.getString(R.string.provider) + ": "
                    + FormatUtils.localizeProviderName(context, providerName);
        }

        // Format Timestamp
        if (getTime() > 0) {
            Date date = new Date(getTime());
            DateFormat formatter
                    = SimpleDateFormat.getDateTimeInstance();
            locationText += "\n "
                    + res.getString(R.string.timestamp) + ": "
                    + formatter.format(date);

            // display "recent" message
            if (debug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_MEDIUM)) {
                if (isRecent()) {
                    locationText += "\n "
                            + res.getString(R.string.loc_updated_recent);
                } else {
                    locationText += "\n "
                            + res.getString(R.string.loc_updated_not_recent);
                }
            }
        }

        // Display raw when in debug mode
        if (debug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_HIGH)) {
            locationText += "\n\n "
                    + res.getString(R.string.raw) + ": "
                    + toString();
        }

        return locationText;
    }
}
