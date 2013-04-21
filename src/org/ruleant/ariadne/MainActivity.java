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

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Main Activity class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class MainActivity extends AbstractAriadneActivity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user clicks the Renew provider button.
     *
     * @param view Button that was clicked
     */
    public void renewProvider(final View view) {
        if (isBound()) {
            mProviderName = mService.updateLocationProvider();
        } else {
            mProviderName = "";
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the Update location button.
     *
     * @param view Button that was clicked
     */
    public void renewLocation(final View view) {
        if (isBound()) {
            // manually update location
            // (don't wait for listener to update location)
            mCurrentLocation = new AriadneLocation(mService.updateLocation());
        } else {
            mCurrentLocation = null;
        }
        refreshDisplay();
    }

    /**
     * Refresh display : refresh the values of Location Provider, Location, ...
     */
    protected final void refreshDisplay() {
        // Refresh locationProvider
        TextView tvProvider
            = (TextView) findViewById(R.id.textView_LocationProvider);
        String providerText
            = getResources().getString(R.string.location_provider) + ": ";
        if (mProviderName.isEmpty()) {
            providerText += getResources().getString(R.string.none);
        } else {
            providerText += mProviderName;
        }
        tvProvider.setText(providerText);

        // Refresh Location
        TextView tvLocation
            = (TextView) findViewById(R.id.textView_Location);
        String locationText
            = getResources().getString(R.string.location) + ":\n";
        if (mCurrentLocation == null) {
            locationText += " "
                + getResources().getString(R.string.unknown);
        } else {
            locationText += mCurrentLocation.toString(this);
        }
        tvLocation.setText(locationText);

        // Refresh Stored Location
        TextView tvStoredLocation
            = (TextView) findViewById(R.id.textView_StoredLocation);
        String storedLocationText
            = getResources().getString(R.string.stored_location) + ":\n";
        if (mStoredLocation == null) {
            storedLocationText += " "
                + getResources().getString(R.string.unknown);
        } else {
            storedLocationText += mStoredLocation.toString(this);
        }
        tvStoredLocation.setText(storedLocationText);

        // Refresh Directions to destination
        TextView tvToDestination
            = (TextView) findViewById(R.id.textView_ToDestination);
        String toDestinationText
            = getResources().getString(R.string.to_dest) + ":\n";
        if (mStoredLocation == null || mCurrentLocation == null) {
            toDestinationText += " "
                + getResources().getString(R.string.unknown);
        } else {
            // Print distance and bearing
            toDestinationText += " "
                + getResources().getString(R.string.distance) + ": "
                + FormatUtils.formatDist(mService.getDistance()) + "\n";
            toDestinationText += " "
                + getResources().getString(R.string.direction) + ": "
                + mService.getDirection() + "°";
        }
        tvToDestination.setText(toDestinationText);
    }
}
