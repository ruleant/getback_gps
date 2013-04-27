/**
 * Location Store
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

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Location Store saves a location, it will store a location for future use,
 * and will the save the location when the application is stopped.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LocationStore {
    /**
     * Context of App.
     */
    private Context mContext;
    /**
     * Selected Location.
     */
    private Location mLocation;
    /**
     * SharedPreference object.
     */
    private SharedPreferences mPrefs;
    /**
     * SharedPreferences location for LocationStore class.
     */
    public static final String PREFS_STORE_LOC = "LocationStore";
    /**
     * Name of Longitude object in SharedPreferences.
     */
    private static final String LONGITUDE = "longitude";
    /**
     * Name of Latitude object in SharedPreferences.
     */
    private static final String LATITUDE = "latitude";
    /**
     * Name of Altitude object in SharedPreferences.
     */
    private static final String ALTITUDE = "altitude";
    /**
     * Name of hasAltitude object in SharedPreferences.
     */
    private static final String HAS_ALTITUDE = "has_altitude";
    /**
     * Name of Accuracy object in SharedPreferences.
     */
    private static final String ACCURACY = "accuracy";
    /**
     * Name of hasAccuracy object in SharedPreferences.
     */
    private static final String HAS_ACCURACY = "has_accuracy";
    /**
     * Name of Timestamp object in SharedPreferences.
     */
    private static final String TIMESTAMP = "timestamp";
    /**
     * Name of Location provider object in SharedPreferences.
     */
    private static final String LOC_PROVIDER = "loc_provider";

    /**
     * Constructor.
     *
     * @param context Context of the Android app
     */
    public LocationStore(final Context context) {
        mContext = context;
        mLocation = new Location("stored");
        mPrefs = mContext.getSharedPreferences(
                PREFS_STORE_LOC, Context.MODE_PRIVATE
                );

        restore();
    }

    /**
     * Get Location.
     *
     * @return Location location
     */
    public Location getLocation() {
        return mLocation;
    }

    /**
     * Set Location.
     *
     * @param location New location
     */
    public void setLocation(final Location location) {
        mLocation.setLongitude(location.getLongitude());
        mLocation.setLatitude(location.getLatitude());
    }

    /**
     * Save stored location in Shared Preferences.
     */
    public void save() {
        // don't save if mLocation is not set
        if (mLocation == null) {
            return;
        }

        /* set Locale temporary to US to avoid Android bug 5734
            https://code.google.com/p/android/issues/detail?id=5734
            Location.convert(String) is localization independent,
            so it throws an exception when a parameter contains a ","
            instead of a "." as decimal separator
            changing the local to US, converts the double to a string with a "."
         */
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        // save location to a SharedPreferences file
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(
                LONGITUDE,
                Location.convert(
                        mLocation.getLongitude(), Location.FORMAT_DEGREES
                        )
                );
        editor.putString(
                LATITUDE,
                Location.convert(
                        mLocation.getLatitude(), Location.FORMAT_DEGREES
                        )
                );

        // save altitude, if defined
        editor.putString(
                HAS_ALTITUDE, Boolean.toString(mLocation.hasAltitude()));
        if (mLocation.hasAltitude()) {
            editor.putString(
                    ALTITUDE, Double.toString(mLocation.getAltitude()));
        }

        // save accuracy, if defined
        editor.putString(
                HAS_ACCURACY, Boolean.toString(mLocation.hasAccuracy()));
        if (mLocation.hasAccuracy()) {
            editor.putString(ACCURACY, Float.toString(mLocation.getAccuracy()));
        }

        editor.putLong(TIMESTAMP, mLocation.getTime());
        editor.putString(LOC_PROVIDER, mLocation.getProvider());
        // Commit the edits!
        editor.commit();

        // set default locale back to original, workaround for Android bug 5734
        Locale.setDefault(originalLocale);
    }

    /**
     * Restore stored location from Shared Preferences.
     *
     * @return location retrieved from Preferences
     */
    public Location restore() {
        Location location = new Location("");

        // retrieve longitude and latitude,
        // return null when not set or exception is thrown
        try {
            location.setLongitude(
                    Location.convert(mPrefs.getString(LONGITUDE, "0.0"))
                    );
            location.setLatitude(
                    Location.convert(mPrefs.getString(LATITUDE, "0.0"))
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // retrieve altitude, if defined
        try {
            if (Boolean.parseBoolean(mPrefs.getString(HAS_ALTITUDE, "false"))) {
                location.setAltitude(
                    Double.parseDouble(mPrefs.getString(ALTITUDE, "0.0")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // retrieve accuracy, if defined
        try {
            if (Boolean.parseBoolean(mPrefs.getString(HAS_ACCURACY, "false"))) {
                location.setAccuracy(
                    Float.parseFloat(mPrefs.getString(ACCURACY, "0.0"))
                    );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // retrieve time stamp
        try {
            location.setTime(mPrefs.getLong(TIMESTAMP, 0));
        } catch (Exception e) {
            e.printStackTrace();
            location.setTime(0);
        }

        // retrieve location provider
        try {
            location.setProvider(mPrefs.getString(LOC_PROVIDER, ""));
        } catch (Exception e) {
            e.printStackTrace();
            location.setProvider("");
        }

        // set retrieved location
        mLocation.set(location);

        return mLocation;
    }
}
