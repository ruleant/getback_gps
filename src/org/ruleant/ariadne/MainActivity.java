/**
 * Main Activity
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
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Main Activity class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class MainActivity extends AbstractAriadneActivity {
    /**
     * Rotation of the direction pointer image.
     */
    private static final int POINTER_ROT = 90;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Refresh display : refresh the values of Location Provider, Location, ...
     */
    protected final void refreshDisplay() {
        LocationService service = getService();
        AriadneLocation destination = null;
        AriadneLocation currentLocation = null;

        // Refresh locationProvider
        TextView tvProvider
            = (TextView) findViewById(R.id.textView_LocationProvider);
        String providerText
            = getResources().getString(R.string.location_provider) + ": ";
        String providerName = service.getLocationProvider();
        if (providerName.isEmpty()) {
            providerText += getResources().getString(R.string.none);
        } else {
            providerText += FormatUtils.localizeProviderName(this, providerName);
        }
        tvProvider.setText(providerText);

        // Refresh Location
        TextView tvLocation
            = (TextView) findViewById(R.id.textView_Location);
        String locationText
            = getResources().getString(R.string.curr_location) + ":\n";
        currentLocation = service.getLocation();
        if (currentLocation == null) {
            locationText += " "
                + getResources().getString(R.string.unknown);
        } else {
            locationText += currentLocation.toString(this);
        }
        tvLocation.setText(locationText);

        // Refresh Stored Location
        TextView tvStoredLocation
            = (TextView) findViewById(R.id.textView_StoredLocation);
        String storedLocationText
            = getResources().getString(R.string.destination) + ":\n";

        // get Destination from service
        try {
            destination
                = new AriadneLocation(service.getDestination());
        } catch (Exception e) {
            e.printStackTrace();
            destination = null;
        }

        if (destination == null) {
            storedLocationText += " "
                + getResources().getString(R.string.notset);
        } else {
            storedLocationText += destination.toString(this);
        }
        tvStoredLocation.setText(storedLocationText);

        // Refresh Directions to destination
        TextView tvToDestination
            = (TextView) findViewById(R.id.textView_ToDestination);
        TextView tvInaccurateDirection
        = (TextView) findViewById(R.id.textView_InaccurateDirection);
        ImageView ivDestPointer
        = (ImageView) findViewById(R.id.image_DestinationPointer);
        String toDestinationText
            = getResources().getString(R.string.to_dest) + ":\n";
        if (destination == null || currentLocation == null) {
            toDestinationText += " "
                + getResources().getString(R.string.unknown);
            tvInaccurateDirection.setVisibility(TextView.INVISIBLE);
            ivDestPointer.setVisibility(ImageView.INVISIBLE);
        } else {
            // Print distance and bearing
            toDestinationText += " "
                + getResources().getString(R.string.distance) + ": "
                + FormatUtils.formatDist(service.getDistance()) + "\n";
            toDestinationText += " "
                + getResources().getString(R.string.direction) + ": "
                + FormatUtils.formatAngle(service.getDirection());
            tvInaccurateDirection.setVisibility(TextView.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ivDestPointer.setVisibility(ImageView.VISIBLE);
                // rotate 90° counter clockwise,
                // current image is pointing right.
                ivDestPointer.setRotation(
                        (float) FormatUtils.normalizeAngle(
                                service.getDirection() - POINTER_ROT));
            } else {
                ivDestPointer.setVisibility(ImageView.INVISIBLE);
            }
        }
        tvToDestination.setText(toDestinationText);
    }
}
