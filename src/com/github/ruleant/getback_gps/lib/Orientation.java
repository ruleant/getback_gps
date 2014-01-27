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
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        mAccelerometerValues = event.values;
        mAccelerometerTimestamp = event.timestamp;
    }

    /**
     * Set acceleration by an event from a TYPE_MAGNETIC_FIELD sensor.
     *
     * @param event Sensor event from TYPE_MAGNETIC_FIELD sensor
     */
    public final void setMagneticField(final SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD) {
            return;
        }
        mMagneticFieldValues = event.values;
        mMagneticFieldTimestamp = event.timestamp;
    }

    /**
     * Returns true if an orientation can be provided :
     * - required sensors are available
     * - sensor values were recently updated.
     */
    public final boolean hasOrientation() {
        // TODO define when Orientation is present and accurate
        return false;
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
                    listener, mAccelerometer, 500000);
            mSensorManager.registerListener(
                    listener, mMagneticFieldSensor, 500000);
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
    public final double getOrientation() {
        if (mAccelerometerValues.length != 3
                || mMagneticFieldValues.length != 3) {
            return 0;
        }

        float[] rotationMatrixR = new float[9];
        float[] orientationValues = new float[3];

        if (SensorManager.getRotationMatrix(rotationMatrixR, null,
                mAccelerometerValues, mMagneticFieldValues)) {
            orientationValues = SensorManager.getOrientation(rotationMatrixR,
                    orientationValues);

            if (orientationValues.length == 3) {
                return Math.toDegrees(orientationValues[0]);
            }
        }

        return 0;
    }
}
