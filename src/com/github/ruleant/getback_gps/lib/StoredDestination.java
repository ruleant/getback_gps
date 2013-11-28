package com.github.ruleant.getback_gps.lib;
/**
 * Stored Destination
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

import android.content.Context;
import android.location.Location;

/**
 * StoredDestination saves a location, it will store a location for future use,
 * and will the save the location when the application is stopped.
 * The difference with StoredLocation is it will only keep location data
 * (longitude, latitude, altitude).
 * Other, time dependent, info will be discarded.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class StoredDestination extends StoredLocation {
    /**
     * SharedPreferences location for StoredDestination class.
     */
    public static final String DEFAULT_PREF_NAME = "stored_destination";

    /**
     * Constructor.
     *
     * @param context        Context of the Android app
     * @param sharedPrefName Name of Shared Preferences file name
     */
    public StoredDestination(
            final Context context, final String sharedPrefName) {
        super(context, sharedPrefName);
    }

    /**
     * Set Location.
     *
     * @param location New location
     */
    public final void setLocation(final Location location) {
        Location tempLocation = new Location("");

        tempLocation.setLongitude(location.getLongitude());
        tempLocation.setLatitude(location.getLatitude());

        super.setLocation(tempLocation);
    }
}
