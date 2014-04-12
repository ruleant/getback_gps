/**
 * Location Service Callback Interface
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
 * @package com.github.ruleant.getback_gps
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps;

/**
 * Callback interface used by LocationService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
oneway interface ILocationServiceCallback {
    /**
     * Called when the service has an updated location.
     */
    void locationUpdated();

    /**
     * Called when the service has an updated sensorOrientation.
     */
    void orientationUpdated();

    /**
     * Called when the service has an updated provider.
     */
    void providerUpdated();
}
