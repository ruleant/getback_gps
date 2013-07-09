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
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
public abstract class AbstractAriadneActivity extends Activity {
    /**
     * Interface to LocationService instance.
     */
    private LocationService mService;
    /**
     * Connection state with LocationService.
     */
    private boolean mBound = false;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Called when the user clicks the Store Location menu item.
     * It displays a dialog, where the user confirm or cancel storing
     * the current location.
     *
     * @param item MenuItem object that was clicked
     */
    public void storeLocation(final MenuItem item) {
        // Use the Builder class for convenient dialog construction,
        // based on the example on
        // https://developer.android.com/guide/topics/ui/dialogs.html
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_store_location)
               .setPositiveButton(R.string.store_location,
                       new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // store current location and refresh display
                       if (mBound) {
                           mService.storeCurrentLocation();
                       }
                       refreshDisplay();
                   }
               })
               .setNegativeButton(R.string.no,
                       new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });

        // Create the AlertDialog object and display it
        builder.create().show();
    }

    /**
     * Called when the user clicks the refresh menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public void refresh(final MenuItem item) {
        if (mBound) {
            mService.updateLocationProvider();
            mService.updateLocation();
        }
        refreshDisplay();
    }

    /**
     * Called when the user clicks the About menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public void displayAbout(final MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Settings menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public void displaySettings(final MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by onOptionsItemSelected()
        switch (item.getItemId()) {
        case R.id.menu_settings:
            displaySettings(item);
            return true;
        case R.id.menu_about:
            displayAbout(item);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Refresh display : refresh the values of Location Provider, Location, ...
     */
    protected abstract void refreshDisplay();

    /**
     * Returns bound state to Location Service.
     *
     * @return boolean Bound State
     */
    protected boolean isBound() {
        return mBound;
    }

    /**
     * Returns Location Service.
     *
     * @return LocationService
     */
    protected LocationService getService() {
        return mService;
    }

    /**
     * Defines callbacks for service binding, passed to bindService().
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(
                final ComponentName className, final IBinder service) {
            // We've bound to LocationService, cast the IBinder
            // and get LocationService instance
            LocationBinder binder = (LocationBinder) service;
            mService = binder.getService();
            mBound = true;

            // We want to monitor the service for as long as we are
            // connected to it.
            binder.registerCallback(mCallback);

            refreshDisplay();
        }

        @Override
        public void onServiceDisconnected(final ComponentName arg0) {
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
            refreshDisplay();
        }

        /**
         * Called by the LocationService when a location provider is updated,
         * it gets the new location provider and refreshes the display.
         */
        public void providerUpdated() {
            refreshDisplay();
        }
    };
}
