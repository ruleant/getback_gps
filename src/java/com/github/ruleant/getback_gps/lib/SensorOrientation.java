/**
 * Calculates current orientation from sensors.
 *
 * Copyright (C) 2014-2015 Dieter Adriaenssens
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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

import com.github.ruleant.getback_gps.SettingsActivity;

import java.util.ArrayList;
import java.util.EventListener;

/**
 * Calculates current orientation from sensors.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class SensorOrientation implements SensorEventListener {
    /**
     * Accelerometer Sensor.
     */
    private Sensor mOrientationSensor;

    /**
     * Context of the Android app.
     */
    private Context mContext;

    /**
     * List with subscribed listeners.
     */
    private ArrayList<OrientationEventListener> eventListenerList
            = new ArrayList<OrientationEventListener>();

    /**
     * Current calculated orientation.
     */
    private double mOrientation = 0;

    /**
     * Timestamp in milliseconds when current orientation was calculated.
     */
    private long mOrientationTimestamp = 0;

    /**
     * Realtime timestamp in nanoseconds when current orientation was updated.
     */
    private long mOrientationRTTimestamp = 0;

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
     * Realtime timestamp in nanoseconds when accelerometer sensor was updated.
     */
    private long mAccelerometerRTTimestamp = 0;

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
     * Realtime timestamp in nanoseconds when magnetic field sensor was updated.
     */
    private long mMagneticFieldRTTimestamp = 0;

    /**
     * Sensor timestamp expiration,
     * 5 seconds in nanoseconds (5 * 10^9).
     */
    private static final long TIMESTAMP_EXPIRE = 5000 * Tools.MILLI_IN_NANO;

    /**
     * Sensor update rate in microseconds.
     */
    private static final int SENSOR_UPDATE_RATE = 200000;

    /**
     * Number of sensor value components.
     */
    private static final int SENSOR_VALUES_SIZE = 3;

    /**
     * Number of rotation matrix components.
     */
    private static final int MATRIX_SIZE = 9;

    /**
     * Low pass filter alpha value.
     */
    private static final float LOW_PASS_ALPHA = 0.6f;

    /**
     * Alpha value of circular average
     * of orientation value calculated from sensors.
     */
    private static final float ALPHA_ORIENTATION_SENSORS = 0.05f;

    /**
     * Constructor.
     *
     * @param context Context of the Android app
     * @throws IllegalArgumentException if context is not defined
     */
    public SensorOrientation(final Context context) {
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
            mOrientationSensor = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ORIENTATION);
        }
    }

    /**
     * Set acceleration by an event from a TYPE_ACCELERATION sensor.
     *
     * @param event Sensor event from TYPE_ACCELEROMETER sensor
     */
    public final void setAcceleration(final SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER
            // reject values that arrive sooner than the update rate
            || Tools.isTimestampRecent(event.timestamp,
                mAccelerometerTimestamp,
                SENSOR_UPDATE_RATE * Tools.MICRO_IN_NANO)) {
            return;
        }
        mAccelerometerValues
            = LowPassFilter.filterValueSet(mAccelerometerValues,
                event.values, LOW_PASS_ALPHA);
        mAccelerometerTimestamp = event.timestamp;
        mAccelerometerRTTimestamp = Tools.getTimestampNano();

        calculateOrientation();
        onOrientationChange();
    }

    /**
     * Set acceleration by an event from a TYPE_MAGNETIC_FIELD sensor.
     *
     * @param event Sensor event from TYPE_MAGNETIC_FIELD sensor
     */
    public final void setMagneticField(final SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD
            // reject values that arrive sooner than the update rate
            || Tools.isTimestampRecent(event.timestamp,
                mMagneticFieldTimestamp,
                SENSOR_UPDATE_RATE * Tools.MICRO_IN_NANO)) {
            return;
        }
        mMagneticFieldValues
            = LowPassFilter.filterValueSet(mMagneticFieldValues,
                event.values, LOW_PASS_ALPHA);
        mMagneticFieldTimestamp = event.timestamp;
        mMagneticFieldRTTimestamp = Tools.getTimestampNano();

        calculateOrientation();
        onOrientationChange();
    }

    /**
     * Set orientation by an event from a TYPE_ORIENTATION sensor.
     *
     * @param event Sensor event from TYPE_ACCELEROMETER sensor
     */
    public final void setOrientation(final SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ORIENTATION
                // reject values that arrive sooner than the update rate
                || Tools.isTimestampRecent(event.timestamp,
                    mOrientationTimestamp,
                    SENSOR_UPDATE_RATE * Tools.MICRO_IN_NANO)) {
            return;
        }
        mOrientation = event.values[0];
        mOrientationTimestamp = event.timestamp;
        mOrientationRTTimestamp = Tools.getTimestampNano();

        onOrientationChange();
    }

    /**
     * Checks if an orientation can be provided :
     * - required sensors are available
     * - sensor values were recently updated.
     *
     * @return true if an orientation can be provided
     */
    public boolean hasOrientation() {
        return isSensorsEnabled()
                && mAccelerometer != null && mMagneticFieldSensor != null
                && isTimestampRecent(mAccelerometerRTTimestamp)
                && isTimestampRecent(mMagneticFieldRTTimestamp)
                || (mOrientationSensor != null
                && isTimestampRecent(mOrientationRTTimestamp));
    }

    /**
     * Gets current SensorOrientation.
     *
     * @return current SensorOrientation
     */
    public double getOrientation() {
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
        return mSensorManager != null
            && (mSensorManager.getSensorList(
                Sensor.TYPE_MAGNETIC_FIELD).size() > 0
            && mSensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER).size() > 0
            || mSensorManager.getSensorList(
                Sensor.TYPE_ORIENTATION).size() > 0);
    }

    /**
     * Returns true if use of sensors is enabled
     * - TYPE_MAGNETIC_FIELD
     * - TYPE_ACCELEROMETER.
     *
     * @return true if sensors are enabled
     */
    public final boolean isSensorsEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(
                        SettingsActivity.KEY_PREF_ENABLE_SENSORS,
                        SettingsActivity.DEFAULT_PREF_ENABLE_SENSORS);
    }

    /**
     * Register for Sensor events of
     * TYPE_ACCELEROMETER and TYPE_MAGNETIC_FIELD.
     *
     * @param listener SensorEventListener
     */
    public final void registerEvents(final SensorEventListener listener) {
        if (!isSensorsEnabled()) {
            return;
        }

        int sensor = Integer.parseInt(
                PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(
                        SettingsActivity.KEY_PREF_GEO_ORIENTATION_SENSOR,
                        SettingsActivity.DEFAULT_PREF_GEO_ORIENTATION_SENSOR));

        // use calculated orientation by default
        // (until raw sensor value is stable)
        if (sensor == SettingsActivity.GEO_ORIENTATION_SENSOR_CALCULATED
            || sensor == SettingsActivity.GEO_ORIENTATION_SENSOR_AUTO
            && mOrientationSensor != null) {
            // orientation sensor is deprecated
            mSensorManager.registerListener(
                    listener, mOrientationSensor, SENSOR_UPDATE_RATE);
        } else if (mAccelerometer != null && mMagneticFieldSensor != null) {
            mSensorManager.registerListener(
                    listener, mAccelerometer, SENSOR_UPDATE_RATE);
            mSensorManager.registerListener(
                    listener, mMagneticFieldSensor, SENSOR_UPDATE_RATE);
        }
    }

    /**
     * Unregister for Sensor events of
     * TYPE_ACCELEROMETER and TYPE_MAGNETIC_FIELD.
     *
     * @param listener SensorEventListener
     */
    public final void unRegisterEvents(final SensorEventListener listener) {
        if (mAccelerometer != null && mMagneticFieldSensor != null) {
            mSensorManager.unregisterListener(listener, mAccelerometer);
            mSensorManager.unregisterListener(listener, mMagneticFieldSensor);
        }
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(listener, mOrientationSensor);
        }
    }

    /**
     * Calculates current orientation, based on
     * TYPE_MAGNETIC_FIELD and TYPE_ACCELEROMETER sensor values.
     *
     * @return current SensorOrientation
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
                mOrientation = CircularAverage.getAverageValue(
                        (float) mOrientation,
                        (float) Math.toDegrees(orientationValues[0]),
                        ALPHA_ORIENTATION_SENSORS);
                mOrientationTimestamp = Tools.getMax(mMagneticFieldTimestamp,
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
        return Tools.isTimestampNanoRecent(timestamp, TIMESTAMP_EXPIRE);
    }

    /**
     * Event listener interface for SensorOrientation updates.
     */
    public interface OrientationEventListener extends EventListener {
        /**
         * Indicates there has been a orientation change.
         */
        void onOrientationChanged();
    }

    /**
     * Adds the listener to eventListenerList.
     * @param listener SensorOrientation event listener
     */
    public final void addEventListener(
            final OrientationEventListener listener) {
        eventListenerList.add(listener);

        // register listening to events when the first listener is added
        if (eventListenerList.size() == 1) {
            registerEvents(this);
        }
    }

    /**
     * Removes the listener from eventListenerList.
     * @param listener SensorOrientation event listener
     */
    public final void removeEventListener(
            final OrientationEventListener listener) {
        eventListenerList.remove(listener);

        // unregister listening to events when the last listener is removed
        if (eventListenerList.size() == 0) {
            unRegisterEvents(this);
        }
    }

    /**
     * Notify all event listeners.
     */
    private void onOrientationChange() {
        for (OrientationEventListener eventListener : eventListenerList) {
            eventListener.onOrientationChanged();
        }
    }

    /**
     * Called when sensor accuracy changes, not implemented.
     *
     * @param sensor Sensor that has a changed accuracy
     * @param accuracy New accuracy
     */
    public final void onAccuracyChanged(final Sensor sensor,
                                        final int accuracy) {
    }

    /**
     * Called when a Sensor value changes.
     *
     * @param event Sensor event
     */
    public final void onSensorChanged(final SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                setAcceleration(event);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                setMagneticField(event);
                break;
            case Sensor.TYPE_ORIENTATION:
                setOrientation(event);
                break;
            default:
                break;
        }
    }
}
