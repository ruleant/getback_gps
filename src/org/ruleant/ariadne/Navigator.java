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

import android.location.Location;

/**
 * Class with several methods useful for navigation.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Navigator {
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
    private AriadneLocation mDestination;

    /**
     * Constructor.
     */
    public Navigator() {
    }

    /**
     * Set Location.
     *
     * @param location New Location (AriadneLocation object)
     */
    public final void setLocation(final AriadneLocation location) {
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
    public final AriadneLocation getLocation() {
        return mCurrentLocation;
    }

    /**
     * Set destination location.
     *
     * @param destination New destination
     */
    public final void setDestination(final AriadneLocation destination) {
        mDestination = destination;
    }

    /**
     * Get destination location.
     *
     * @return Destination
     */
    public final AriadneLocation getDestination() {
        return mDestination;
    }

    /**
     * Calculate distance to current destination.
     *
     * @return distance in meters
     */
    public final float getDistance() {
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
    public final double getAbsoluteDirection() {
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
    public final double getRelativeDirection() {
        // don't calculate bearing if bearing is inaccurate,
        // f.e. if current location is not set or prev. loc = curr. loc.
        if (!isBearingAccurate()) {
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
    public final float getCurrentSpeed() {
        return 0;
    }

    /**
     * Calculate most accurate current bearing,
     * depending on available sensors and data.
     *
     * @return current bearing in ° relative to the North
     */
    public final double getCurrentBearing() {
        double currentBearing = 0;
        if (mCurrentLocation != null && mCurrentLocation.hasBearing()) {
            currentBearing = mCurrentLocation.getBearing();
        } else {
            // don't calculate current bearing if previous location is not set
            // or if bearing is not accurate
            if (isBearingAccurate() && mPreviousLocation != null) {
                currentBearing = mPreviousLocation.bearingTo(mCurrentLocation);
            }
        }

        return currentBearing;
    }

    /**
     * Determines if current location is accurate,
     * if it is set, if it is recent and if the accuracy is reasonable.
     *
     *
     * @return true if location is accurate
     */
    public final boolean isLocationAccurate() {
        return  mCurrentLocation != null
                && mCurrentLocation.isRecent()
                && mCurrentLocation.getAccuracy() <= 50;
    }

    /**
     * Determines if current bearing is accurate,
     * if the current location is accurate, if previous location is set,
     * if the previous location is recent, if the current location is
     * not equal to the previous location
     * and if the distance between the two is larger than the accuracy.
     *
     * @return true if bearing is accurate
     */
    public final boolean isBearingAccurate() {
        return isLocationAccurate()
                && mPreviousLocation != null
                && mPreviousLocation.isRecent()
                && !mPreviousLocation.equals(mCurrentLocation)
                && mPreviousLocation.distanceTo(mCurrentLocation)
                > mCurrentLocation.getAccuracy();
    }
}
