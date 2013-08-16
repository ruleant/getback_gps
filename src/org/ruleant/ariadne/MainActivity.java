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

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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

    /**
     * Crouton configuration.
     */
    protected Configuration croutonConfig;

    /**
     * Inaccurate location crouton.
     */
    protected Crouton crInaccurateLocation;

    /**
     * Inaccurate direction crouton.
     */
    protected Crouton crInaccurateDirection;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create Crouton configuration
        croutonConfig = new Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE)
                .build();

        // create inaccurate location crouton
        crInaccurateLocation = Crouton.makeText(this,
                R.string.inaccurate_location, Style.ALERT);
        crInaccurateLocation.setConfiguration(croutonConfig);

        // create inaccurate direction crouton
        crInaccurateDirection = Crouton.makeText(this,
                R.string.inaccurate_direction, Style.INFO);
        crInaccurateDirection.setConfiguration(croutonConfig);
    }

    @Override
    protected final void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        Crouton.cancelAllCroutons();
    }

    /**
     * Refresh display : refresh the values of Location Provider, Location, ...
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected final void refreshDisplay() {
        // only refresh items if activity is bound to service
        if (!isBound()) {
            return;
        }

        LocationService service = getService();
        Navigator navigator = service.getNavigator();
        Resources res = getResources();
        AriadneLocation destination = null;
        AriadneLocation currentLocation = null;

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
        currentLocation = service.getLocation();
        if (currentLocation == null) {
            locationText += " " + res.getString(R.string.unknown);
        } else {
            locationText += currentLocation.toString(this);
        }
        tvLocation.setText(locationText);

        // Refresh current
        TextView tvCurrent
                = (TextView) findViewById(R.id.textView_Current);
        String currentText
                = res.getString(R.string.current);

        // current speed
        currentText += "\n " + res.getString(R.string.speed) + ": ";
        if (navigator == null) {
            currentText += " " + res.getString(R.string.unknown);
        } else {
            currentText += FormatUtils.formatSpeed(
                navigator.getCurrentSpeed(), this);
        }

        // current bearing
        currentText += "\n " + res.getString(R.string.bearing) + ": ";
        if (navigator == null) {
            currentText += " " + res.getString(R.string.unknown);
        } else {
            currentText += FormatUtils.formatAngle(
                    FormatUtils.normalizeAngle(navigator.getCurrentBearing()));
        }

        // update string
        tvCurrent.setText(currentText);

        // Refresh Destination
        TextView tvDestination
            = (TextView) findViewById(R.id.textView_Destination);
        String destinationText
            = res.getString(R.string.destination) + ":\n";

        // get Destination from service
        try {
            destination = new AriadneLocation(navigator.getDestination());
        } catch (Exception e) {
            e.printStackTrace();
            destination = null;
        }

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
            destinationText += destination.toString(this);
        }
        tvDestination.setText(destinationText);

        // Refresh Directions to destination
        TextView tvToDestination
            = (TextView) findViewById(R.id.textView_ToDestination);
        TextView tvInaccurateDirection
        = (TextView) findViewById(R.id.textView_InaccurateDirection);
        ImageView ivDestPointer
        = (ImageView) findViewById(R.id.image_DestinationPointer);
        String toDestinationText
            = res.getString(R.string.to_dest) + ":\n";
        if (destination == null || currentLocation == null) {
            toDestinationText += " "
                + res.getString(R.string.unknown);
            tvInaccurateDirection.setVisibility(TextView.INVISIBLE);
            ivDestPointer.setVisibility(ImageView.INVISIBLE);
        } else {
            // Print distance and bearing
            toDestinationText += " "
                + res.getString(R.string.distance) + ": "
                + FormatUtils.formatDist(navigator.getDistance()) + "\n";
            toDestinationText += " "
                + res.getString(R.string.direction) + ": "
                + FormatUtils.formatAngle(
                    FormatUtils.normalizeAngle(
                        navigator.getAbsoluteDirection()));

            boolean isBearingAccurate = navigator.isBearingAccurate();

            // if bearing is inaccurate, don't display relative direction
            // and display warning
            if (isBearingAccurate) {
                toDestinationText += "\n "
                        + res.getString(R.string.direction_relative) + ": "
                        + FormatUtils.formatAngle(
                            navigator.getRelativeDirection());
                tvInaccurateDirection.setVisibility(TextView.INVISIBLE);
             } else {
                tvInaccurateDirection.setVisibility(TextView.VISIBLE);
             }

            // setRotation requires API level 11
            if (isBearingAccurate
                  && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ivDestPointer.setVisibility(ImageView.VISIBLE);
                // rotate 90° counter clockwise,
                // current image is pointing right.
                ivDestPointer.setRotation(
                    (float) FormatUtils.normalizeAngle(
                        navigator.getRelativeDirection() - POINTER_ROT));
            } else {
                ivDestPointer.setVisibility(ImageView.INVISIBLE);
            }
        }
        tvToDestination.setText(toDestinationText);

        refreshCrouton();
    }

    /**
     * Update which crouton should be displayed.
     */
    protected final void refreshCrouton() {
        // only refresh items if activity is bound to service
        if (!isBound()) {
            return;
        }

        Navigator navigator = getService().getNavigator();

        // if location is inaccurate, display warning
        if (!navigator.isLocationAccurate()) {
            crInaccurateLocation.show();
        } else {
            crInaccurateLocation.cancel();
            // if bearing is inaccurate, display warning
            if (!navigator.isBearingAccurate()) {
                crInaccurateDirection.show();
            } else {
                crInaccurateDirection.cancel();
            }
        }
    }
}