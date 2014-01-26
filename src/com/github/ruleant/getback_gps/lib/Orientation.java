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
import android.hardware.SensorEvent;

/**
 * Calculates current orientation from sensors.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Orientation {
    /**
     * Constructor.
     */
    public Orientation() {
    }

    /**
     * Set acceleration by an event from a TYPE_ACCELERATION sensor
     *
     * @param event Sensor event from TYPE_ACCELERATION sensor
     */
    public final void setAcceleration(final SensorEvent event) {
    }

    /**
     * Set acceleration by an event from a TYPE_MAGNETIC_FIELD sensor
     *
     * @param event Sensor event from TYPE_MAGNETIC_FIELD sensor
     */
    public final void setMagneticField(final SensorEvent event) {
    }

    /**
     * Returns true if an orientation can be provided :
     * - required sensors are available
     * - sensor values were recently updated
     */
    public final boolean hasOrientation() {
        return false;
    }

    /**
     * Returns true if the required sensors are available :
     * - TYPE_MAGNETIC_FIELD
     * - TYPE_ACCELERATION
     *
     * @param context Context of the Android app
     */
    public final boolean hasSensors(final Context context) {
        return false;
    }

    /**
     * Calculates current orientation, based on
     * TYPE_MAGNETIC_FIELD and TYPE_ACCELERATION sensor values.
     *
     * @return current Orientation
     */
    public final double getOrientation() {
        return 0;
    }
}
