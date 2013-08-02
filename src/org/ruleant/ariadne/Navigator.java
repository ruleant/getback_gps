/**
 * Class with several methods useful for navigation.
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

import android.content.Context;
import android.location.Location;

/**
 * Class with several methods useful for navigation.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Navigator {
    /**
     * SharedPreferences location for StoredDestination.
     */
    public static final String PREFS_STORE_DEST = "stored_destination";

    /**
     * Current context.
     */
    private Context mContext = null;

    /**
     * Current Location.
     */
    private AriadneLocation mCurrentLocation = null;

    /**
     * Previous Location.
     */
    private AriadneLocation mPreviousLocation = null;

    /**
     * Current destination.
     */
    private StoredDestination mDestination;

    /**
     * Constructor.
     *
     * @param context App context
     */
    public Navigator(Context context) {
        setContext(context);

        // retrieve stored destination
        mDestination = new StoredDestination(
                getContext(), PREFS_STORE_DEST);
    }

    /**
     * Set current context.
     *
     * @param context Current context
     */
    public final void setContext(final Context context) {
        if (context != null) {
            mContext = context;
        }
    }

    /**
     * Get current context.
     *
     * @return Current context
     */
    protected final Context getContext() {
        return mContext;
    }

    /**
     * Set Location.
     *
     * @param location New Location (AriadneLocation object)
     */
    public void setLocation(final AriadneLocation location) {
        mPreviousLocation = mCurrentLocation;
        mCurrentLocation = location;
    }

    /**
     * Retrieve Location.
     *
     * Get last known location
     *
     * @return Location
     */
    public AriadneLocation getLocation() {
        return mCurrentLocation;
    }

    /**
     * Set destination location.
     *
     * @param location New destination
     */
    public void setDestination(Location location) {
        mDestination.save(location);
    }

    /**
     * Get destination location.
     *
     * @return Location
     */
    public Location getDestination() {
        return mDestination.getLocation();
    }

    /**
     * Calculate distance to current destination.
     *
     * @return distance in meters
     */
    public float getDistance() {
        Location destination = getDestination();

        // don't calculate distance if current location is not set
        if (mCurrentLocation == null || destination == null) {
            return 0;
        }
        return mCurrentLocation.distanceTo(destination);
    }

    /**
     * Calculate absolute direction to current destination.
     *
     * @return direction in ° relative to the North
     */
    public double getAbsoluteDirection() {
        Location destination = getDestination();

        // don't calculate direction if current location is not set
        if (mCurrentLocation == null || destination == null) {
            return 0;
        }
        return mCurrentLocation.bearingTo(destination);
    }

    /**
     * Calculate direction to current destination,
     * relative to current bearing.
     *
     * @return direction in ° relative to current bearing
     */
    public double getRelativeDirection() {
        // don't calculate bearing if current location is not set
        // TODO or if bearing is unknown/unreliable (prev. loc = curr. loc.)
        if (mCurrentLocation == null) {
            return 0;
        }
        double absoluteDirection = getAbsoluteDirection();
        double currentBearing = getCurrentBearing();

        return FormatUtils.normalizeAngle(absoluteDirection - currentBearing);
    }

    /**
     * Calculate most accurate current speed,
     * depending on available sensors and data.
     *
     * @return current speed in m/s
     */
    public float getCurrentSpeed() {
        return 0;
    }

    /**
     * Calculate most accurate current bearing,
     * depending on available sensors and data.
     *
     * @return current bearing in ° relative to the North
     */
    public double getCurrentBearing() {
        double currentBearing = 0;
        if (mCurrentLocation.hasBearing()) {
            currentBearing = mCurrentLocation.getBearing();
        } else {
            // don't calculate current bearing if previous location is not set
            // current location was checked earlier
            if (mPreviousLocation != null) {
                currentBearing = mPreviousLocation.bearingTo(mCurrentLocation);
            }
        }

        return currentBearing;
    }
}
