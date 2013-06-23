/**
 * Custom Ariadne Location object
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
 * @package org.ruleant.ariadne
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

/**
 * Custom Ariadne Location object, it inherits from Location class,
 * but overrides the getString() method.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class AriadneLocation extends Location {

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
    public boolean isNewer(final Location location) {
        return (location.getTime() > super.getTime());
    }

    /**
     * Checks if location timestamp is recent.
     *
     * @return true if location is recent.
     */
    public boolean isRecent() {
        // TODO use elapsedRealtimeNanos when using API 17 or higher
        // if ((SystemClock.elapsedRealtimeNanos()
        // - getElapsedRealtimeNanos()) < 300000000) {
        return ((System.currentTimeMillis() - getTime()) < 300000);
    }

    /**
     * Overrides the toString() method, implementing a formatted String.
     *
     * @param context Context of the App
     * @return String formatted string
     */
    public String toString(final Context context) {
        Resources res = context.getResources();
        DebugLevel debug = new DebugLevel(context);

        String locationText = "";

        // Format location
        locationText += " "
            + res.getString(R.string.latitude) + ": "
            + convert(getLatitude(), FORMAT_SECONDS).replaceFirst(":", "° ")
                .replace(":", "' ") + "\"";
        locationText += "\n "
            + res.getString(R.string.longitude) + ": "
            + convert(getLongitude(), FORMAT_SECONDS).replaceFirst(":", "° ")
                .replace(":", "' ") + "\"";
        if (hasAltitude()) {
            locationText += "\n "
                + res.getString(R.string.altitude) + ": "
                + FormatUtils.formatDist(getAltitude());
        }
        if (hasBearing()) {
            locationText += "\n "
                + res.getString(R.string.bearing) + ": "
                + FormatUtils.formatAngle(getBearing());
        }
        if (hasSpeed()) {
            locationText += "\n "
                + res.getString(R.string.speed) + ": "
                + FormatUtils.formatSpeed(getSpeed());
        }
        if (hasAccuracy()) {
            locationText += "\n "
                + res.getString(R.string.accuracy) + ": "
                + FormatUtils.formatDist(getAccuracy());
        }

        // Location provider
        String providerName = getProvider();
        if (!providerName.isEmpty()) {
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
                + super.toString();
        }

        return locationText;
    }
}
