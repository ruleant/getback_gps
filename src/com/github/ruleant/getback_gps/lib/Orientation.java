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

import java.util.ArrayList;
import java.util.EventListener;

/**
 * Calculates current orientation from sensors.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Orientation implements SensorEventListener {
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
            // reject values that arrive sooner than the update rate
            || (event.timestamp - mAccelerometerTimestamp)
                < SENSOR_UPDATE_RATE * MICRO_IN_NANO) {
            return;
        }
        mAccelerometerValues
            = lowPassFilterArray(mAccelerometerValues, event.values);
        mAccelerometerTimestamp = event.timestamp;

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
            || (event.timestamp - mMagneticFieldTimestamp)
                < SENSOR_UPDATE_RATE * MICRO_IN_NANO) {
            return;
        }
        mMagneticFieldValues
            = lowPassFilterArray(mMagneticFieldValues, event.values);
        mMagneticFieldTimestamp = event.timestamp;

        calculateOrientation();
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
        return mAccelerometer != null && mMagneticFieldSensor != null
                && isTimestampRecent(mAccelerometerTimestamp)
                && isTimestampRecent(mMagneticFieldTimestamp)
                && isTimestampRecent(mOrientationTimestamp);
    }

    /**
     * Gets current Orientation.
     *
     * @return current Orientation
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

    /**
     * Implements a low pass filter, topping of high frequency changes,
     * reducing the jumpiness of the signal.
     *
     * @param previousValue previous sensor value
     * @param newValue new sensor value
     * @return filtered value
     */
    public static float lowPassFilter(
            final float previousValue, final float newValue) {
        return previousValue + LOW_PASS_ALPHA * (newValue - previousValue);
    }

    /**
     * Runs a low pass filter on an array of unrelated values in parallel.
     *
     * There is no relation between the values,
     * this method passes a set of separate signals in parallel.
     *
     * Not be confused by an array of values from the same sensor (FIFO),
     * where the result of passing each value in an array is influenced
     * by the result of the previous value.
     *
     * @param previousArray array of previous values
     * @param newArray array of current values
     * @return array with filtered values.
     */
    public static float[] lowPassFilterArray(
            final float[] previousArray, final float[] newArray) {
        // newArray should not be empty
        if (newArray == null || newArray.length == 0) {
            throw new IllegalArgumentException(
                    "parameter newArray should not be an empty array");
        }

        float[] returnArray = new float[newArray.length];

        if (previousArray == null) {
            return newArray;
        }

        // previousArray should have the same size as newArray
        if (newArray.length != previousArray.length) {
            throw new IllegalArgumentException(
                "parameter previousArray (length = "
                    + Integer.toString(previousArray.length)
                    + ") should have the same size as parameter "
                    + "newArray (length = " + Integer.toString(newArray.length)
                    + ")");
        }

        for (int i = 0; i < newArray.length; i++) {
            returnArray[i] = lowPassFilter(previousArray[i], newArray[i]);
        }

        return returnArray;
    }

    /**
     * Event listener interface for Orientation updates.
     */
    public interface OrientationEventListener extends EventListener {
        /**
         * Indicates there has been a orientation change.
         */
        void onOrientationChanged();
    }

    /**
     * List with subscribed listeners.
     */
    ArrayList<OrientationEventListener> eventListenerList
            = new ArrayList<OrientationEventListener>();

    /**
     * Adds the listener to eventListenerList.
     * @param listener Orientation event listener
     */
    public void addEventListener(OrientationEventListener listener){
        eventListenerList.add(listener);

        // register listening to events when the first listener is added
        if (eventListenerList.size() == 1) {
            registerEvents(this);
        }
    }

    /**
     * Removes the listener from eventListenerList.
     * @param listener Orientation event listener
     */
    public void removeEventListener(OrientationEventListener listener){
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
            default:
                break;
        }
    }
}
