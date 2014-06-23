/**
 * Main Activity
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
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import com.github.ruleant.getback_gps.lib.AriadneLocation;
import com.github.ruleant.getback_gps.lib.CardinalDirection;
import com.github.ruleant.getback_gps.lib.FormatUtils;
import com.github.ruleant.getback_gps.lib.Navigator;

/**
 * Main Activity class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class DetailsActivity extends AbstractGetBackGpsActivity {
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    @Override
    protected final boolean refreshDisplay() {
        if (!super.refreshDisplay()) {
            return false;
        }

        // refresh views with "current" info
        // display value even if it is inaccurate
        refreshCurrentViews(true);

        // only refresh items if activity is bound to service
        // connection state is checked in getNavigator
        LocationService service = getService();
        Navigator navigator = getNavigator();

        if (service == null || navigator == null) {
            return false;
        }

        Resources res = getResources();
        // get Destination and current location from service
        AriadneLocation destination = navigator.getDestination();
        AriadneLocation currentLocation = service.getLocation();

        // Refresh locationProvider
        TextView tvProvider
                = (TextView) findViewById(R.id.textView_LocationProvider);
        String providerText = res.getString(R.string.location_provider) + ": ";
        if (!service.isSetLocationProvider()) {
            providerText += res.getString(R.string.none);
        } else {
            providerText += FormatUtils.localizeProviderName(
                    this, service.getLocationProvider());
        }
        tvProvider.setText(providerText);

        // Refresh Location
        TextView tvLocation
                = (TextView) findViewById(R.id.textView_Location);
        String locationText
                = res.getString(R.string.curr_location) + ":\n";

        if (currentLocation == null) {
            locationText += " " + res.getString(R.string.unknown);
        } else {
            locationText += currentLocation.toFormattedString(this);
        }
        tvLocation.setText(locationText);

        // Refresh Destination
        TextView tvDestination
                = (TextView) findViewById(R.id.textView_Destination);
        String destinationText
                = res.getString(R.string.destination) + ":\n";

        if (destination == null) {
            destinationText += " "
                    + res.getString(R.string.notset);

            // display notice when no destination is set
            // and there is a current location
            if (currentLocation != null) {
                destinationText += "\n "
                        + res.getString(R.string.notice_no_dest);
            }
        } else {
            destinationText += destination.toFormattedString(this);
        }
        tvDestination.setText(destinationText);

        // Refresh Bearing offset
        TextView tvBearingOffset
                = (TextView) findViewById(R.id.textView_BearingOffset);
        tvBearingOffset.setText(res.getString(R.string.sensor_bearing_offset)
                + " : " + FormatUtils.formatAngle(
                    navigator.getSensorBearingOffset(), 0));

        // Refresh travel direction
        TextView tvTravelDirection
                = (TextView) findViewById(R.id.textView_TravelDirection);
        String travelDirectionText = res.getString(R.string.travel_direction)
                + " : ";
        switch (navigator.getTravelDirection()) {
        case Unknown :
        default:
            travelDirectionText += res.getString(R.string.unknown);
            break;
        case Forward :
            travelDirectionText
                    += res.getString(R.string.travel_direction_forward);
            break;
        case Backwards :
            travelDirectionText
                    += res.getString(R.string.travel_direction_backwards);
            break;
        }
        tvTravelDirection.setText(travelDirectionText);

        // Refresh Directions to destination
        TextView tvToDestination
                = (TextView) findViewById(R.id.textView_ToDestination);
        String toDestinationText
                = res.getString(R.string.to_dest) + ":\n";
        if (destination == null || currentLocation == null) {
            toDestinationText += " "
                    + res.getString(R.string.unknown);
        } else {
            // Print distance and bearing
            toDestinationText += " "
                    + res.getString(R.string.distance) + ": "
                    + FormatUtils.formatDist(navigator.getDistance()) + "\n";

            CardinalDirection cd = new CardinalDirection(
                    this,
                    FormatUtils.normalizeAngle(
                            navigator.getAbsoluteDirection()));

            toDestinationText += " "
                    + res.getString(R.string.direction) + ": "
                    + cd.format();

            boolean isBearingAccurate = navigator.isBearingAccurate();

            // if bearing is inaccurate, don't display relative direction
            // and display warning
            if (isBearingAccurate) {
                toDestinationText += "\n "
                        + res.getString(R.string.direction_relative) + ": "
                        + FormatUtils.formatAngle(
                        navigator.getRelativeDirection(), 2);
            }
        }
        tvToDestination.setText(toDestinationText);

        return true;
    }
}
