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

import org.ruleant.ariadne.LocationService.LocationBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Abstract Ariadne Activity class, contains the common methods
 * to connect to LocationService. Other activities that need LocationService
 * can extend this class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
abstract public class AbstractAriadneActivity extends Activity {
    /**
     * Interface to LocationService instance.
     */
    protected LocationService mService;
    /**
     * Connection state with LocationService.
     */
    protected boolean mBound = false;
    /**
     * Name of the LocationProvider.
     */
    protected String mProviderName = "";
    /**
     * Current Location.
     */
    protected AriadneLocation mCurrentLocation = null;
    /**
     * Previously stored Location.
     */
    protected AriadneLocation mStoredLocation = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocationService
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
     * Called when the user clicks the Store Location menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public void storeLocation(MenuItem item) {
        if (mBound) {
            mService.storeCurrentLocation();
            mStoredLocation
                = new AriadneLocation(mService.getStoredLocation());
        } else {
            mStoredLocation = null;
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the refresh menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public void refresh(MenuItem item) {
        if (mBound) {
            mProviderName = mService.getLocationProvider();
            mCurrentLocation = new AriadneLocation(mService.getLocation());
            mStoredLocation
                = new AriadneLocation(mService.getStoredLocation());
        } else {
            mProviderName = null;
            mCurrentLocation = null;
            mStoredLocation = null;
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the About menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public void displayAbout(MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Settings menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public void displaySettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Refresh display : refresh the values of Location Provider, Location, ...
     */
    abstract protected void refreshDisplay();

    /**
     * Defines callbacks for service binding, passed to bindService().
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(
                ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder
            // and get LocalService instance
            LocationBinder binder = (LocationBinder) service;
            mService = binder.getService();
            mBound = true;

            // We want to monitor the service for as long as we are
            // connected to it.
            binder.registerCallback(mCallback);

            mProviderName = mService.getLocationProvider();
            if (!mProviderName.isEmpty()) {
                mCurrentLocation = new AriadneLocation(mService.getLocation());
                mStoredLocation
                    = new AriadneLocation(mService.getStoredLocation());
                refreshDisplay();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * This implementation is used to receive callbacks
     * from the remote service.
     */
    private ILocationServiceCallback mCallback
        = new ILocationServiceCallback.Stub() {
        /**
         * Called by the LocationService when a location is updated,
         * it gets the new location and refreshes the display.
         */
        public void locationUpdated() {
            if (mBound) {
                mCurrentLocation
                    = new AriadneLocation(mService.getLocation());
            } else {
                mCurrentLocation = null;
            }
            refreshDisplay();
        }

        /**
         * Called by the LocationService when a location provider is updated,
         * it gets the new location provider and refreshes the display.
         */
        public void providerUpdated() {
            if (mBound) {
                mProviderName = mService.getLocationProvider();
            } else {
                mProviderName = null;
            }
            refreshDisplay();
        }
    };
}
