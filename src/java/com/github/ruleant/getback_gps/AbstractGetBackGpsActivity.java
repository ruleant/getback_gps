/**
 * Main Activity
 *
 * Copyright (C) 2012-2021 Dieter Adriaenssens
 * Copyright (C) 2019 Timotheos Constambeys
 * Copyright (C) 2022 Jan Scheible
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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ruleant.getback_gps.LocationService.LocationBinder;
import com.github.ruleant.getback_gps.lib.CardinalDirection;
import com.github.ruleant.getback_gps.lib.FormatUtils;
import com.github.ruleant.getback_gps.lib.Navigator;
import com.github.ruleant.getback_gps.lib.Tools;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Abstract Ariadne Activity class, contains the common methods
 * to connect to LocationService. Other activities that need LocationService
 * can extend this class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
abstract class AbstractGetBackGpsActivity extends Activity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Interface to LocationService instance.
     */
    private LocationService mService;
    /**
     * Connection state with LocationService.
     */
    private boolean mBound = false;

    /**
     * Real-time timestamp in nanoseconds when activity was updated.
     */
    private long mUpdatedTimestamp = 0;

    /**
     * Activity update rate in nanoseconds (500ms).
     */
    private static final int ACTIVITY_UPDATE_RATE = 500000000;

    /**
     * Id to identify a location permission request.
     */
    private static final int REQUEST_LOCATION = 0;

    /**
     * Permissions required to update location.
     */
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * Crouton status.
     */
    private short mCroutonStatus = 0;

    /**
     * Crouton status 'None'.
     */
    private static final short CROUTON_STATUS_NONE = 0;

    /**
     * Crouton status 'Permission required'.
     */
    private static final short CROUTON_STATUS_PERMISSION_REQUIRED = 1;

    /**
     * Crouton status 'Inaccurate location'.
     */
    private static final short CROUTON_STATUS_INACCURATE_LOCATION = 2;

    /**
     * Crouton status 'No destination set'.
     */
    private static final short CROUTON_STATUS_NO_DESTINATION = 3;

    /**
     * Crouton status 'Inaccurate direction'.
     */
    private static final short CROUTON_STATUS_INACCURATE_DIRECTION = 4;

    /**
     * Crouton status 'Inaccurate direction'.
     */
    private static final short CROUTON_STATUS_DESTINATION_REACHED = 5;

    /**
     * Location permission required crouton.
     */
    private Crouton crLocationPermissionRequired;

    /**
     * Inaccurate location crouton.
     */
    private Crouton crInaccurateLocation;

    /**
     * 'No destination set' crouton.
     */
    private Crouton crNoDestination;

    /**
     * Inaccurate direction crouton.
     */
    private Crouton crInaccurateDirection;

    /**
     * 'Destination reached' crouton.
     */
    private Crouton crDestinationReached;

    /**
     * State prefix of the store location dialog.
     */
    private static final String STORE_LOCATION_DIALOG_STATE_PREFIX = "store-location-dialog_";

    /**
     * State prefix of the enter location dialog.
     */
    private static final String ENTER_LOCATION_DIALOG_STATE_PREFIX = "enter-location-dialog_";

    /**
     * State prefix of the rename destination dialog.
     */
    private static final String RENAME_DESTINATION_DIALOG_STATE_PREFIX = "rename-destination-dialog_";

    /**
     * Used to store the (partial) state that is currently edited in a dialog.
     */
    private Bundle dialogState = new Bundle();

    /**
     * Tracks the latest opened dialog.
     */
    private Dialog latestDialog = null;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu;
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    protected final void onStart() {
        super.onStart();
        // Bind to LocationService
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected final void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create Crouton configuration
        Configuration croutonConfig = new Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE)
                .build();

        // create permission required crouton
        crLocationPermissionRequired = Crouton.makeText(this,
                R.string.notice_location_permission_required, Style.ALERT);
        crLocationPermissionRequired.setConfiguration(croutonConfig);

        // create inaccurate location crouton
        crInaccurateLocation = Crouton.makeText(this,
                R.string.inaccurate_location, Style.ALERT);
        crInaccurateLocation.setConfiguration(croutonConfig);

        // create inaccurate direction crouton
        crInaccurateDirection = Crouton.makeText(this,
                R.string.inaccurate_direction, Style.INFO);
        crInaccurateDirection.setConfiguration(croutonConfig);

        // create 'no destination set' crouton
        crNoDestination = Crouton.makeText(this,
                R.string.notice_no_dest, Style.INFO);
        crNoDestination.setConfiguration(croutonConfig);

        // create 'destination reached' crouton
        crDestinationReached = Crouton.makeText(this,
                R.string.notice_destination_reached, Style.CONFIRM);
        crDestinationReached.setConfiguration(croutonConfig);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putAll(dialogState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Bundle storeLocationDialogState = StateBundleHelper.filterStringValuesForPrefix(
                STORE_LOCATION_DIALOG_STATE_PREFIX, savedInstanceState);
        Bundle enterLocationDialogState = StateBundleHelper.filterStringValuesForPrefix(
                ENTER_LOCATION_DIALOG_STATE_PREFIX, savedInstanceState);
        Bundle renameDestinationDialogState = StateBundleHelper.filterStringValuesForPrefix(
                RENAME_DESTINATION_DIALOG_STATE_PREFIX, savedInstanceState);

        if (!storeLocationDialogState.isEmpty()) {
            dialogState.putAll(storeLocationDialogState);
            storeLocation();
        } else if (!enterLocationDialogState.isEmpty()) {
            dialogState.putAll(enterLocationDialogState);
            enterLocation();
        } else if (!renameDestinationDialogState.isEmpty()) {
            dialogState.putAll(renameDestinationDialogState);
            renameDestination();
        }
    }

    @Override
    protected final void onDestroy() {
        super.onDestroy();

        // Cancel active croutons
        Crouton.cancelAllCroutons();

        if(latestDialog != null && latestDialog.isShowing()) {
            // Dismisses the open dialog avoiding a "activity has leaked window" exception
            latestDialog.dismiss();
        }
    }

    /**
     * Called when the user clicks the Store Location menu item.
     * It displays a dialog, where the user confirm or cancel storing
     * the current location.
     */
    public final void storeLocation() {
        if (mBound && mService.getLocation() == null) {
            Toast.makeText(
                    this,
                    R.string.store_location_disabled,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Use the Builder class for convenient dialog construction,
        // based on the example on
        // https://developer.android.com/guide/topics/ui/dialogs.html
        StateTrackingDialogBuilder builder = new StateTrackingDialogBuilder(this,
                STORE_LOCATION_DIALOG_STATE_PREFIX, dialogState);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();


        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView
                = inflater.inflate(R.layout.dialog_location_name, null);

        // Get the EditText object containing the location name
        final EditText etLocationName
                = (EditText) dialogView.findViewById(R.id.location_name);

        // Set the layout for the dialog
        builder.setView(dialogView)
                .setTitle(R.string.store_location)
                .setPositiveButton(R.string.store_location,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                if (etLocationName != null) {
                                    String locationName
                                        = etLocationName.getText().toString();

                                    // store current location
                                    // and refresh display
                                    if (mBound) {
                                        mService.storeCurrentLocation(
                                                locationName);
                                    }
                                    refreshDisplay();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                // User cancelled the dialog
                            }
                        });
        // Create the AlertDialog object and display it
        (latestDialog = builder.create()).show();
    }

    /**
     * Called when the user clicks the Enter Location menu item.
     * It displays a dialog, where the user enters a gps location.
     */
    public final void enterLocation() {

        // Use the Builder class for convenient dialog construction,
        // based on the example on
        // https://developer.android.com/guide/topics/ui/dialogs.html
        StateTrackingDialogBuilder builder = new StateTrackingDialogBuilder(this,
               ENTER_LOCATION_DIALOG_STATE_PREFIX, dialogState);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView
                = inflater.inflate(R.layout.dialog_location_enter, null);
        // Get the EditText object containing the location name
        final EditText etLocationName
                = (EditText) dialogView.findViewById(R.id.location_name);
        // Get the EditText object containing the latitude
        final EditText etLocationLatitude
                = (EditText) dialogView.findViewById(R.id.location_latitude);
        // Get the EditText object containing the longitude
        final EditText etLocationLongitude
                = (EditText) dialogView.findViewById(R.id.location_longtitude);
        // Set the layout for the dialog
        builder.setView(dialogView)
                .setTitle(R.string.enter_location)
                .setPositiveButton(R.string.store_location,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                try {
                                    String locationName
                                            = etLocationName.getText().toString();
                                    double locationLatitude
                                            = Double.parseDouble(etLocationLatitude.getText().toString());
                                    double locationLongitude
                                            = Double.parseDouble(etLocationLongitude.getText().toString());

                                    if (locationLatitude < -90 || locationLatitude > 90) {
                                        Toast.makeText(
                                                AbstractGetBackGpsActivity.this,
                                                R.string.enter_location_invalid_latitude,
                                                Toast.LENGTH_LONG
                                        ).show();
                                        return;
                                    }

                                    if (locationLongitude < -180.0 || locationLongitude > 180.0) {
                                        Toast.makeText(
                                                AbstractGetBackGpsActivity.this,
                                                R.string.enter_location_invalid_longitude,
                                                Toast.LENGTH_LONG
                                        ).show();
                                        return;
                                    }

                                    // store current location
                                    // and refresh display
                                    if (mBound) {
                                        mService.storeLocation(locationName, locationLatitude, locationLongitude);
                                    }
                                    refreshDisplay();

                                } catch (Exception ex) {
                                    Toast.makeText(
                                            AbstractGetBackGpsActivity.this,
                                            ex.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                // User cancelled the dialog
                            }
                        });

        // Create the AlertDialog object and display it
        (latestDialog = builder.create()).show();
    }

    /**
     * Called when the user clicks the Rename Destination menu item.
     * It displays a dialog, where the user can enter a new name
     * for the current destination.
     */
    public final void renameDestination() {
        if (mBound && mService.getDestination() == null) {
            Toast.makeText(
                    this,
                    R.string.rename_destination_disabled,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Use the Builder class for convenient dialog construction,
        // based on the example on
        // https://developer.android.com/guide/topics/ui/dialogs.html
        StateTrackingDialogBuilder builder = new StateTrackingDialogBuilder(this,
               RENAME_DESTINATION_DIALOG_STATE_PREFIX, dialogState);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate the layout for the dialog
        // Pass null as the parent view because it's going
        // into the dialog layout
        final View dialogView
                = inflater.inflate(R.layout.dialog_location_name, null);

        // Get the TextView object containing the location question
        final TextView tvLocationMessage
                = (TextView) dialogView.findViewById(R.id.location_message);
        if (tvLocationMessage != null) {
            // set message for renaming destination
            tvLocationMessage.setText(R.string.dialog_rename_destination);
        }

        // Get the EditText object containing the location name
        final EditText etLocationName
                = (EditText) dialogView.findViewById(R.id.location_name);
        if (etLocationName != null && mService != null && mService.getDestination() != null) {
            // set current destination name as default
            etLocationName.setText(mService.getDestination().getName());
        }

        // Set the layout for the dialog
        builder.setView(dialogView)
                .setTitle(R.string.rename_destination)
                .setPositiveButton(R.string.rename_destination,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                if (etLocationName != null) {
                                    String locationName
                                        = etLocationName.getText().toString();

                                    // store current location
                                    // and refresh display
                                    if (mBound) {
                                        mService.renameDestination(
                                                locationName);
                                    }
                                    refreshDisplay();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                // User cancelled the dialog
                            }
                        });

        // Create the AlertDialog object and display it
        (latestDialog = builder.create()).show();
    }

    /**
     * Called when the user clicks the refresh menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public final void refresh(final MenuItem item) {
        if (mBound) {
            mService.updateLocationProvider();
            mService.updateLocation();
        }
        refreshDisplay();
    }

    /**
     *  Define screen orientation mode
     */
    protected boolean isOrientationLandscape() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        // If x is larger than y, the display is on Landscape mode
        return screenSize.x > screenSize.y;
    }

    /**
     * Called when the user clicks the About menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public final void displayAbout(final MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Settings menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public final void displaySettings(final MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by onOptionsItemSelected()
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            displaySettings(item);
            return true;
        } else if (itemId == R.id.menu_about) {
            displayAbout(item);
            return true;
        } else if (itemId == R.id.menu_storelocation) {
            storeLocation();
            return true;
        } else if (itemId == R.id.menu_enterlocation) {
            enterLocation();
            return true;
        } else if (itemId == R.id.menu_renamedestination) {
            renameDestination();
            return true;
        } else if (itemId == R.id.menu_refresh) {
            refresh(item);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuItem miStoreLocation = menu.findItem(R.id.menu_storelocation);
        MenuItem miEnterLocation = menu.findItem(R.id.menu_enterlocation);
        MenuItem miRenameDest = menu.findItem(R.id.menu_renamedestination);
        if (isBound()) {
            // enable store location button if a location is set
            miStoreLocation.setEnabled(mService.getLocation() != null);
            miEnterLocation.setEnabled(true);
            // enable store location button if a location is set
            miRenameDest.setEnabled(mService.getDestination() != null);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Refresh display : refresh the values of Location Provider, Location, ...
     *
     * @return true if refresh was successful
     */
    protected boolean refreshDisplay() {
        // only refresh items if activity is bound to service
        if (!isBound()
            || Tools.isTimestampNanoRecent(
                mUpdatedTimestamp, ACTIVITY_UPDATE_RATE)) {
            return false;
        }

        mUpdatedTimestamp = Tools.getTimestampNano();

        if (!mService.isLocationPermissionGranted()) {
            requestLocationPermission();
        }

        refreshCrouton();

        return true;
    }

    /**
     * Update which crouton should be displayed.
     */
    protected final void refreshCrouton() {
        // only refresh items if activity is bound to service
        // connection state is checked in getNavigator
        Navigator navigator = getNavigator();

        if (navigator == null) {
            return;
        }

        if (mService == null) {
            return;
        }

        short CroutonStatusNew = CROUTON_STATUS_NONE;

        // if Location Permission is not granted, display warning
        if (!mService.isLocationPermissionGranted()) {
            CroutonStatusNew = CROUTON_STATUS_PERMISSION_REQUIRED;
        } else {
            // if location is inaccurate, display warning
            if (!navigator.isLocationAccurate()) {
                CroutonStatusNew = CROUTON_STATUS_INACCURATE_LOCATION;
            } else {
                // if no destination is set, display message
                if (navigator.getDestination() == null) {
                    CroutonStatusNew = CROUTON_STATUS_NO_DESTINATION;
                } else {
                    // destination was reached
                    if (navigator.isDestinationReached()) {
                        CroutonStatusNew = CROUTON_STATUS_DESTINATION_REACHED;
                    } else {
                        // if bearing is inaccurate, display warning
                        if (!navigator.isBearingAccurate()) {
                            CroutonStatusNew = CROUTON_STATUS_INACCURATE_DIRECTION;
                        }
                    }
                }
            }
        }

        // Check if Crouton Status changes
        if ( mCroutonStatus == CroutonStatusNew ) {
            return;
        }

        mCroutonStatus = CroutonStatusNew;

        // Cancel active croutons
        Crouton.cancelAllCroutons();

        switch (mCroutonStatus) {
            case CROUTON_STATUS_PERMISSION_REQUIRED :
                crLocationPermissionRequired.show();
                break;
            case CROUTON_STATUS_INACCURATE_LOCATION :
                crInaccurateLocation.show();
                break;
            case CROUTON_STATUS_NO_DESTINATION :
                crNoDestination.show();
                break;
            case CROUTON_STATUS_INACCURATE_DIRECTION :
                crInaccurateDirection.show();
                break;
            case CROUTON_STATUS_DESTINATION_REACHED :
                crDestinationReached.show();
                break;
            default:
                break;
        }
    }

    /**
     * Requests location permission if necessary.
     */
    protected final void requestLocationPermission() {
        if (mService == null) {
            return;
        }

        // request permission if location permission is not granted
        if (!mService.isLocationPermissionGranted()
                && !ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS_LOCATION,
                        REQUEST_LOCATION);
        }
    }

    /**
     * Refresh current speed/bearing views.
     *
     * @param displayInaccurate display value when it is inaccurate
     */
    public final void refreshCurrentViews(final boolean displayInaccurate) {
        // only refresh items if activity is bound to service
        // connection state is checked in getNavigator
        Navigator navigator = getNavigator();

        if (navigator == null) {
            return;
        }

        Resources res = getResources();

        // Get "Current" TextViews
        TextView tvCurrentSpeed
                = (TextView) findViewById(R.id.textView_currSpeed);
        TextView tvCurrentBearing
                = (TextView) findViewById(R.id.textView_currBearing);

        // Define strings
        String currentSpeedText = res.getString(R.string.inaccurate);
        String currentBearingText = res.getString(R.string.inaccurate);

        // Update current speed
        if (displayInaccurate || navigator.isLocationAccurate()) {
            currentSpeedText = FormatUtils.formatSpeed(
                    navigator.getCurrentSpeed(), this);
        }

        // Update current bearing
        if (displayInaccurate || navigator.isBearingAccurate()) {
            CardinalDirection cd = new CardinalDirection(
                    this,
                    FormatUtils.normalizeAngle(
                            navigator.getCurrentBearing()));

            currentBearingText = cd.format();
        }

        // update views
        tvCurrentSpeed.setText(currentSpeedText);
        tvCurrentBearing.setText(currentBearingText);
    }

    /**
     * Returns bound state to Location Service.
     *
     * @return boolean Bound State
     */
    protected final boolean isBound() {
        return mBound;
    }

    /**
     * Returns Location Service.
     *
     * @return LocationService
     */
    protected final LocationService getService() {
        if (isBound()) {
            return mService;
        } else {
            return null;
        }
    }

    /**
     * Returns Navigator.
     *
     * @return Navigator
     */
    protected final Navigator getNavigator() {
        LocationService service = getService();

        if (service == null) {
            return null;
        }

        return service.getNavigator();
    }

    /**
     * Defines callbacks for service binding, passed to bindService().
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

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
    private final ILocationServiceCallback mCallback
            = new ILocationServiceCallback.Stub() {
        /**
         * Called by the LocationService when a location is updated,
         * it gets the new location and refreshes the display.
         */
        public void locationUpdated() {
            refreshDisplay();
        }

        /**
         * Called by the LocationService when a orientation is updated,
         * it gets the new location provider and refreshes the display.
         */
        public void orientationUpdated() {
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

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult (
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ){
        if (requestCode == REQUEST_LOCATION) {
            refreshDisplay();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
