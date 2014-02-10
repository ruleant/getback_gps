/**
 * Calculates current orientation from sensors.
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
 * @package com.github.ruleant.getback_gps
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

/**
 * Calculates current orientation from sensors.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Orientation {
    /**
     * Context of the Android app.
     */
    private Context mContext;

    /**
     * Current calculated orientation.
     */
    private double mOrientation = 0;

    /**
     * Timestamp in milliseconds when current orientation was calculated.
     */
    private long mOrientationTimestamp = 0;

    /**
     * Sensor manager.
     */
    private SensorManager mSensorManager;

    /**
     * Accelerometer Sensor.
     */
    private Sensor mAccelerometer;

    /**
     * Accelerometer Sensor values.
     */
    private float[] mAccelerometerValues;

    /**
     * Accelerometer Sensor values timestamp.
     */
    private long mAccelerometerTimestamp;

    /**
     * Magnetic field sensor.
     */
    private Sensor mMagneticFieldSensor;

    /**
     * Magnetic field sensor values.
     */
    private float[] mMagneticFieldValues;

    /**
     * Magnetic field sensor values timestamp.
     */
    private long mMagneticFieldTimestamp;

    /**
     * Sensor timestamp expiration,
     * 5 seconds in milliseconds (5 * 1000).
     */
    private static final long TIMESTAMP_EXPIRE = 5000;

    /**
     * Millisecond to nanosecond conversion rate.
     */
    private static final long MILLI_IN_NANO = 1000000;

    /**
     * Microsecond to nanosecond conversion rate.
     */
    private static final long MICRO_IN_NANO = 1000;

    /**
     * Sensor update rate in microseconds.
     */
    private static final int SENSOR_UPDATE_RATE = 500000;

    /**
     * Number of sensor value components.
     */
    private static final int SENSOR_VALUES_SIZE = 3;

    /**
     * Number of rotation matrix components.
     */
    private static final int MATRIX_SIZE = 9;

    /**
     * Constructor.
     *
     * @param context Context of the Android app
     * @throws IllegalArgumentException if context is not defined
     */
    public Orientation(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is not defined");
        }

        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(
                Context.SENSOR_SERVICE);

        if (hasSensors()) {
            mAccelerometer = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);
            mMagneticFieldSensor = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    /**
     * Set acceleration by an event from a TYPE_ACCELERATION sensor.
     *
     * @param event Sensor event from TYPE_ACCELEROMETER sensor
     */
    public final void setAcceleration(final SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER
            || (event.timestamp - mAccelerometerTimestamp)
                < SENSOR_UPDATE_RATE * MICRO_IN_NANO) {
            return;
        }
        mAccelerometerValues = event.values;
        mAccelerometerTimestamp = event.timestamp;

        calculateOrientation();
    }

    /**
     * Set acceleration by an event from a TYPE_MAGNETIC_FIELD sensor.
     *
     * @param event Sensor event from TYPE_MAGNETIC_FIELD sensor
     */
    public final void setMagneticField(final SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD
            || (event.timestamp - mMagneticFieldTimestamp)
                < SENSOR_UPDATE_RATE * MICRO_IN_NANO) {
            return;
        }
        mMagneticFieldValues = event.values;
        mMagneticFieldTimestamp = event.timestamp;

        calculateOrientation();
    }

    /**
     * Checks if an orientation can be provided :
     * - required sensors are available
     * - sensor values were recently updated.
     *
     * @return true if an orientation can be provided
     */
    public boolean hasOrientation() {
        return mAccelerometer != null && mMagneticFieldSensor != null
                && isTimestampRecent(mAccelerometerTimestamp)
                && isTimestampRecent(mMagneticFieldTimestamp);
    }

    /**
     * Gets current Orientation.
     *
     * @return current Orientation
     */
    public final double getOrientation() {
        return mOrientation;
    }

    /**
     * Returns true if the required sensors are available :
     * - TYPE_MAGNETIC_FIELD
     * - TYPE_ACCELEROMETER.
     *
     * @return true if required sensors are available
     */
    public final boolean hasSensors() {
        if (mSensorManager == null) {
            return false;
        }

        return mSensorManager.getSensorList(
                Sensor.TYPE_MAGNETIC_FIELD).size() > 0
            && mSensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER).size() > 0;
    }

    /**
     * Register for Sensor events for
     * TYPE_ACCELEROMETER and TYPE_MAGNETIC_FIELD.
     *
     * @param listener SensorEventListener
     */
    public final void registerEvents(final SensorEventListener listener) {
        if (mAccelerometer != null && mMagneticFieldSensor != null) {
            mSensorManager.registerListener(
                    listener, mAccelerometer, SENSOR_UPDATE_RATE);
            mSensorManager.registerListener(
                    listener, mMagneticFieldSensor, SENSOR_UPDATE_RATE);
        }
    }

    /**
     * Register for Sensor events for
     * TYPE_ACCELEROMETER and TYPE_MAGNETIC_FIELD.
     *
     * @param listener SensorEventListener
     */
    public final void unRegisterEvents(final SensorEventListener listener) {
        if (mAccelerometer != null && mMagneticFieldSensor != null) {
            mSensorManager.unregisterListener(listener, mAccelerometer);
            mSensorManager.unregisterListener(listener, mMagneticFieldSensor);
        }
    }

    /**
     * Calculates current orientation, based on
     * TYPE_MAGNETIC_FIELD and TYPE_ACCELEROMETER sensor values.
     *
     * @return current Orientation
     */
    private double calculateOrientation() {
        if (mAccelerometerValues == null
                || mAccelerometerValues.length != SENSOR_VALUES_SIZE
                || mMagneticFieldValues == null
                || mMagneticFieldValues.length != SENSOR_VALUES_SIZE) {
            return 0;
        }

        float[] rotationMatrixR = new float[MATRIX_SIZE];
        float[] orientationValues = new float[SENSOR_VALUES_SIZE];

        if (SensorManager.getRotationMatrix(rotationMatrixR, null,
                mAccelerometerValues, mMagneticFieldValues)) {
            orientationValues = SensorManager.getOrientation(rotationMatrixR,
                    orientationValues);

            if (orientationValues.length == SENSOR_VALUES_SIZE) {
                mOrientation = Math.toDegrees(orientationValues[0]);
                mOrientationTimestamp = getMax(mMagneticFieldTimestamp,
                        mAccelerometerTimestamp);

                return mOrientation;
            }
        }

        return 0;
    }

    /**
     * Checks if timestamp is recent.
     *
     * @param timestamp timestamp in nanoseconds
     * @return true if timestamp is recent.
     */
    private boolean isTimestampRecent(final long timestamp) {
        // TODO use elapsedRealtimeNanos when using API 17 or higher
        return timestamp > 0
            && SystemClock.elapsedRealtime() - (timestamp / MILLI_IN_NANO)
                < TIMESTAMP_EXPIRE;
    }

    /**
     * Return biggest number.
     *
     * @param value1 one value
     * @param value2 another value
     * @return biggest number
     */
    private long getMax(final long value1, final long value2) {
        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }
}
