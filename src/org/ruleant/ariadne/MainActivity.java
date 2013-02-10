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
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

import org.ruleant.ariadne.LocationService.LocationBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Main Activity class
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class MainActivity extends Activity {
    /**
     * Interface to LocationService instance
     */
    LocationService mService;
    /**
     * Connection state with LocationService
     */
    boolean mBound = false;
    /**
     * Name of the LocationProvider
     */
    private String mProviderName = "";
    /**
     * Current Location
     */
    private Ariadne_Location mCurrentLocation = null;
    /**
     * Previously stored Location
     */
    private Ariadne_Location mStoredLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Called when the user clicks the Renew provider button
     *
     * @param view Button that was clicked
     */
    public void renewProvider(View view) {
        if (mBound) {
            mProviderName = mService.updateLocationProvider();
        } else {
            mProviderName = "";
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the Update location button
     *
     * @param view Button that was clicked
     */
    public void renewLocation(View view) {
        if (mBound) {
            // manually update location (don't wait for listener to update location)
            mCurrentLocation = new Ariadne_Location(mService.updateLocation());
        } else {
            mCurrentLocation = null;
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the Store Location menu item
     *
     * @param item MenuItem object that was clicked
     */
    public void storeLocation(MenuItem item) {
        if (mBound) {
            mService.storeCurrentLocation();
            mStoredLocation = new Ariadne_Location(mService.getStoredLocation());
        } else {
            mStoredLocation = null;
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the refresh menu item
     *
     * @param item MenuItem object that was clicked
     */
    public void refresh(MenuItem item) {
        if (mBound) {
            mProviderName = mService.getLocationProvider();
            mCurrentLocation = new Ariadne_Location(mService.getLocation());
            mStoredLocation = new Ariadne_Location(mService.getStoredLocation());
        } else {
            mProviderName = null;
            mCurrentLocation = null;
            mStoredLocation = null;
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the About menu item
     *
     * @param item MenuItem object that was clicked
     */
    public void displayAbout(MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Settings menu item
     *
     * @param item MenuItem object that was clicked
     */
    public void displaySettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * refresh display
     *
     * Refresh the values of Location Provider, Location, ...
     *
     * @return void
     */
    private void refreshDisplay() {
        // Refresh locationProvider
        TextView tv_provider = (TextView) findViewById(R.id.textView_LocationProvider);
        String providerText = getResources().getString(R.string.location_provider) + ": ";
        if (mProviderName.isEmpty()) {
            providerText += getResources().getString(R.string.none);
        } else {
            providerText += mProviderName;
        }
        tv_provider.setText(providerText);

        // Refresh Location
        TextView tvLocation = (TextView) findViewById(R.id.textView_Location);
        String locationText = getResources().getString(R.string.location) + ":\n";
        if (mCurrentLocation == null) {
            locationText += " "  + getResources().getString(R.string.unknown);
        } else {
            locationText += mCurrentLocation.toString(this);
        }
        tvLocation.setText(locationText);

        // Refresh Stored Location
        TextView tvStoredLocation = (TextView) findViewById(R.id.textView_StoredLocation);
        String storedLocationText = getResources().getString(R.string.stored_location) + ":\n";
        if (mStoredLocation == null) {
            storedLocationText += " "  + getResources().getString(R.string.unknown);
        } else {
            storedLocationText += mStoredLocation.toString(this);
        }
        tvStoredLocation.setText(storedLocationText);

        // Refresh Directions to destination
        TextView tvToDestination = (TextView) findViewById(R.id.textView_ToDestination);
        String toDestinationText = getResources().getString(R.string.to_dest) + ":\n";
        if (mStoredLocation == null || mCurrentLocation == null) {
            toDestinationText += " "  + getResources().getString(R.string.unknown);
        } else {
            // Print distance and bearing
            toDestinationText += " "  + getResources().getString(R.string.distance) + ": ";
            toDestinationText += mService.getDistance() + "m\n";
            toDestinationText += " "  + getResources().getString(R.string.bearing) + ": ";
            toDestinationText += mService.getBearing() + "°";
        }
        tvToDestination.setText(toDestinationText);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationBinder binder = (LocationBinder) service;
            mService = binder.getService();
            mBound = true;
            mProviderName = mService.getLocationProvider();
            if (!mProviderName.isEmpty()) {
                mCurrentLocation = new Ariadne_Location(mService.getLocation());
                mStoredLocation = new Ariadne_Location(mService.getStoredLocation());
                refreshDisplay();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
