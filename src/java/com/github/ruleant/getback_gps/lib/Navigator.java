/**
 * Class with several methods useful for navigation.
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

/**
 * Class with several methods useful for navigation.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Navigator {
    /**
     * Required location accuracy in meter.
     */
    private static final double ACCURACY_LIMIT = 50;

    /**
     * Zero distance.
     */
    public static final float DIST_ZERO = 0;

    /**
     * Direction zero.
     */
    public static final double DIR_ZERO = 0.0;

    /**
     * Zero distance.
     */
    public static final float SPEED_ZERO = 0;

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
     * Current orientation based on sensors.
     */
    private SensorOrientation mSensorOrientation;

    /**
     * Offset between bearing provided by sensors
     * and bearing provided by geolocation,
     * used to calibrate current bearing.
     */
    private double mSensorBearingOffset = 0;

    /**
     * Constructor.
     */
    public Navigator() {
    }

    /**
     * Constructor.
     *
     * @param sensorOrientation SensorOrientation instance
     */
    public Navigator(final SensorOrientation sensorOrientation) {
        if (sensorOrientation == null) {
            throw new IllegalArgumentException(
                    "Parameter sensorOrientation should not be null");
        }

        mSensorOrientation = sensorOrientation;
    }

    /**
     * Set Location.
     *
     * @param location New Location (AriadneLocation object)
     */
    public final void setLocation(final AriadneLocation location) {
        mPreviousLocation = mCurrentLocation;
        mCurrentLocation = location;

        calculateSensorBearingOffset();
    }

    /**
     * Set Previous Location,
     * this should only be done to restore a previous state.
     * Use setLocation() for normal operation.
     *
     * @param location Previous Location (AriadneLocation object)
     */
    public final void setPreviousLocation(final AriadneLocation location) {
        mPreviousLocation = location;
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
     * Retrieve Previous Location.
     *
     * @return Previous Location
     */
    public final AriadneLocation getPreviousLocation() {
        return mPreviousLocation;
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
        AriadneLocation destination = getDestination();

        // don't calculate distance if current location is not set
        if (mCurrentLocation == null || destination == null) {
            return DIST_ZERO;
        }
        return mCurrentLocation.distanceTo(destination);
    }

    /**
     * Calculate absolute direction to current destination.
     *
     * @return direction in ° relative to the North
     */
    public final double getAbsoluteDirection() {
        AriadneLocation destination = getDestination();

        // don't calculate direction if current location is not set
        if (mCurrentLocation == null || destination == null) {
            return DIR_ZERO;
        }
        return mCurrentLocation.bearingTo(destination);
    }

    /**
     * Offset between bearing provided by sensors
     * and bearing provided by geolocation,
     * used to calibrate current bearing.
     *
     * @return offset of sensor based bearing
     */
    public final double getSensorBearingOffset() {
        return mSensorBearingOffset;
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
            return DIR_ZERO;
        }
        double absoluteDirection = getAbsoluteDirection();
        double currentBearing = getCurrentBearing();

        return FormatUtils.normalizeAngle(absoluteDirection - currentBearing);
    }

    /**
     * Check if current location is destination.
     *
     * @return true if current location is destination
     */
    public final boolean isDestinationReached() {
        // don't check destination if location is not accurate,
        // or if destination is not set
        return isLocationAccurate() && getDestination() != null
                && getDistance() < mCurrentLocation.getAccuracy();
    }

    /**
     * Calculate most accurate current speed,
     * depending on available sensors and data.
     *
     * @return current speed in m/s
     */
    public final float getCurrentSpeed() {
        float currentSpeed = SPEED_ZERO;
        if (mCurrentLocation == null) {
            return currentSpeed;
        }

        // if location has speed, use this
        if (mCurrentLocation.hasSpeed()) {
            currentSpeed = mCurrentLocation.getSpeed();
        } else {
            if (mPreviousLocation != null
                    && !mCurrentLocation.equals(mPreviousLocation)) {
                // calculate speed from difference with previous location
                float distance
                        = mCurrentLocation.distanceTo(mPreviousLocation);
                long time
                        = mCurrentLocation.getTime()
                        - mPreviousLocation.getTime();
                // only calculate speed if there is a time difference
                // and a distance bigger than the current accuracy
                if (time > 0
                        && distance > mCurrentLocation.getAccuracy()
                        && distance > mPreviousLocation.getAccuracy()) {
                    // calculate speed from distance travelled and time spent
                    // time is in milliseconds, convert to seconds.
                    currentSpeed = distance /
                            (float) (time / Tools.SECOND_IN_MILLIS);
                }
            }
        }

        return currentSpeed;
    }

    /**
     * Calculate most accurate current bearing,
     * depending on available sensors and data.
     *
     * @return current bearing in ° relative to the North
     */
    public final double getCurrentBearing() {
        double currentBearing;
        if (isSensorBearingAccurate()) {
            currentBearing = mSensorOrientation.getOrientation()
                    - mSensorBearingOffset;
        } else {
            currentBearing = getLocationBearing();
        }

        return currentBearing;
    }

    /**
     * Get location based bearing.
     *
     * @return bearing in ° relative to the North
     */
    public final double getLocationBearing() {
        double currentBearing = DIR_ZERO;
        if (mCurrentLocation != null && mCurrentLocation.hasBearing()) {
            currentBearing = mCurrentLocation.getBearing();
        } else {
            // don't calculate current bearing if previous location is not set
            // or if bearing is not accurate
            // (both are checked in isBearingAccurate)
            if (isLocationBearingAccurate()) {
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
                && mCurrentLocation.getAccuracy() <= ACCURACY_LIMIT;
    }

    /**
     * Determines if current bearing is accurate,
     * if sensor based bearing is accurate
     * OR
     * if location (GPS) based bearing is accurate.
     *
     * @return true if bearing is accurate
     */
    public final boolean isBearingAccurate() {
        return isSensorBearingAccurate() || isLocationBearingAccurate();
    }

    /**
     * Determines if sensor based bearing is accurate.
     *
     * @return true if sensor based bearing is accurate
     */
    public final boolean isSensorBearingAccurate() {
        return  mSensorOrientation != null
                && mSensorOrientation.hasOrientation();
    }

    /**
     * Determines if location (GPS signal) based bearing is accurate,
     * if the current location is accurate, if previous location is set,
     * if the previous location is recent, if the current location is
     * not equal to the previous location
     * and if the distance between the two is larger than the accuracy.
     *
     * @return true if location based bearing is accurate
     */
    public final boolean isLocationBearingAccurate() {
        return  isLocationAccurate()
                && mPreviousLocation != null
                && mPreviousLocation.isRecent()
                && !mPreviousLocation.equals(mCurrentLocation)
                && mPreviousLocation.distanceTo(mCurrentLocation)
                > mCurrentLocation.getAccuracy();
    }

    /**
     * Calculate offset between bearing provided by sensors
     * and bearing provided by geolocation.
     */
    public final void calculateSensorBearingOffset() {
        if (isSensorBearingAccurate()
            && (mCurrentLocation != null && mCurrentLocation.hasBearing()
            || isLocationBearingAccurate())) {
            // Calculate offset
            mSensorBearingOffset = mSensorOrientation.getOrientation()
                    - getLocationBearing();
        } else {
            // Reset offset
            mSensorBearingOffset = 0;
        }
    }
}
