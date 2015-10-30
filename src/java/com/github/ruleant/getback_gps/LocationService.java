/**
 * Location Service
 *
 * Copyright (C) 2012-2015 Dieter Adriaenssens
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.github.ruleant.getback_gps.lib.AriadneLocation;
import com.github.ruleant.getback_gps.lib.DebugLevel;
import com.github.ruleant.getback_gps.lib.Navigator;
import com.github.ruleant.getback_gps.lib.SensorOrientation;
import com.github.ruleant.getback_gps.lib.StoredDestination;
import com.github.ruleant.getback_gps.lib.StoredLocation;

/**
 * Location Service provides the current location.
 *
 * This service will connect to the Location Provider
 * and retrieves the current location
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LocationService extends Service
        implements SensorOrientation.OrientationEventListener {
    /**
     * SharedPreferences location for StoredDestination.
     */
    public static final String PREFS_STORE_DEST = "stored_destination";

    /**
     * SharedPreferences location for last known good location.
     */
    public static final String PREFS_LAST_LOC = "last_location";

    /**
     * SharedPreferences location for previous location.
     */
    public static final String PREFS_PREV_LOC = "prev_location";

    /**
     * Binder given to clients.
     */
    private final IBinder mBinder = new LocationBinder();
    /**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    private final RemoteCallbackList<ILocationServiceCallback> mCallbacks
            = new RemoteCallbackList<ILocationServiceCallback>();
    /**
     * Debug class instance.
     */
    private DebugLevel mDebug;

    /**
     * Current context.
     */
    private final Context mContext = this;

    /**
     * LocationManager instance.
     */
    private LocationManager mLocationManager;
    /**
     * Name of the LocationProvider.
     */
    private String mProviderName = "";
    /**
     * Navigator.
     */
    private Navigator mNavigator = null;
    /**
     * SensorOrientation class.
     */
    private SensorOrientation mSensorOrientation = null;
    /**
     * Last known good location.
     */
    private StoredLocation mLastLocation = null;
    /**
     * Last known good location.
     */
    private StoredLocation mPrevLocation = null;
    /**
     * Stored location/destination.
     */
    private StoredDestination mStoredDestination = null;

    @Override
    public final void onCreate() {
        // Create debug class instance
        mDebug = new DebugLevel(this);

        if (mDebug != null
                && mDebug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_HIGH)) {
            Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show();
        }
        mLocationManager
                = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);

        mSensorOrientation = new SensorOrientation(this);
        mNavigator = new Navigator(mSensorOrientation);

        // retrieve last known good location
        mLastLocation = new StoredLocation(
                this.getApplicationContext(), PREFS_LAST_LOC);
        setLocation(mLastLocation.getLocation());

        // retrieve previous location
        mPrevLocation = new StoredLocation(this, PREFS_PREV_LOC);
        mNavigator.setPreviousLocation(mPrevLocation.getLocation());

        // retrieve stored destination
        mStoredDestination = new StoredDestination(this, PREFS_STORE_DEST);
        setDestination(mStoredDestination.getLocation());

        // mProviderName is set by updateLocationProvider
        updateLocationProvider();
        // and used in requestUpdatesFromProvider, which sets location
        requestUpdatesFromProvider();

        // Subscribe to sensor events
        if (mSensorOrientation.hasSensors()
                && mSensorOrientation.isSensorsEnabled()) {
            mSensorOrientation.addEventListener(this);
        }
    }

    @Override
    public final void onDestroy() {
        // The service is no longer used and is being destroyed

        // Unregister all callbacks.
        mCallbacks.kill();

        // unsubscribe from LocationManager updates
        mLocationManager.removeUpdates(mListener);

        // unsubscribe from SensorOrientation sensor events
        mSensorOrientation.removeEventListener(this);

        // save stored locations
        mLastLocation.save();
        mPrevLocation.setLocation(mNavigator.getPreviousLocation());
        mPrevLocation.save();
        mStoredDestination.save();

        // cleanup class properties
        mProviderName = "";
        mLocationManager = null;
        mLastLocation = null;
        mStoredDestination = null;
        mSensorOrientation = null;
        mNavigator = null;

        // display message announcing end of service
        if (mDebug != null
                && mDebug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_HIGH)) {
            Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        }
        mDebug = null;
    }

    @Override
    public final int onStartCommand(
            final Intent intent, final int flags, final int startId) {
        // The service is starting, due to a call to startService()
        return START_NOT_STICKY;
    }

    @Override
    public final IBinder onBind(final Intent intent) {
        if (mDebug != null
                && mDebug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_HIGH)) {
            Toast.makeText(this, "service bound", Toast.LENGTH_SHORT).show();
        }
        return mBinder;
    }

    @Override
    public final boolean onUnbind(final Intent intent) {
        if (mDebug != null
                && mDebug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_HIGH)) {
            Toast.makeText(this, "service unbound", Toast.LENGTH_SHORT).show();
        }
        // don't allow rebind
        return false;
    }

    /**
     * Retrieve Location Provider.
     *
     * Define best location provider based on certain criteria
     */
    public final void updateLocationProvider() {
        // Retrieve a list of location providers that have fine accuracy,
        // no monetary cost, etc
        // TODO define criteria in settings
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        if (mLocationManager != null) {
            mProviderName = mLocationManager.getBestProvider(criteria, true);
        }
    }

    /**
     * Set Location.
     *
     * @param location New location
     */
    public final void setLocation(final Location location) {
        if (location != null) {
            setLocation(new AriadneLocation(location));
        }
    }

    /**
     * Set Location.
     *
     * @param location New Location (AriadneLocation object)
     */
    public final void setLocation(final AriadneLocation location) {
        AriadneLocation currentLocation = getLocation();

        // don't update location if no location is provided,
        // or if new location is the same as the previous one
        // or if the new location is not more recent than the current one
        if (location == null
                || (currentLocation != null
                && ((location.getTime() == currentLocation.getTime()
                && location.getProvider()
                .equals(currentLocation.getProvider()))
                || !currentLocation.isNewer(location)))
                ) {
            return;
        }

        if (mNavigator != null) {
            mNavigator.setLocation(location);
        }

        // save current location
        if (mLastLocation != null) {
            mLastLocation.setLocation(location);
        }
    }

    /**
     * Retrieve Location.
     *
     * Get last known location
     *
     * @return Location
     */
    public final AriadneLocation getLocation() {
        if (mNavigator == null) {
            return null;
        }

        return mNavigator.getLocation();
    }

    /**
     * Set Destination.
     *
     * @param destination New destination
     */
    public final void setDestination(final Location destination) {
        if (destination != null) {
            setDestination(new AriadneLocation(destination));
        }
    }

    /**
     * Set Destination.
     *
     * @param destination New destination
     */
    public final void setDestination(final AriadneLocation destination) {
        if (mNavigator != null) {
            mNavigator.setDestination(destination);
        }
    }

    /**
     * Retrieve Location Provider.
     *
     * @return String current location provider
     */
    public final String getLocationProvider() {
        return mProviderName;
    }

    /**
     * Retrieve Navigator.
     *
     * @return Navigator Navigator object
     */
    public final Navigator getNavigator() {
        return mNavigator;
    }

    /**
     * Checks if Location Provider is defined.
     *
     * @return boolean true if Location Provider is defined.
     */
    public final boolean isSetLocationProvider() {
        return mProviderName != null && mProviderName.length() > 0;
    }

    /**
     * Update Location.
     *
     * Force location update, using getLastKnownLocation()
     */
    public final void updateLocation() {
        if (mLocationManager == null || !isSetLocationProvider()) {
            return;
        }
        // update location using getLastKnownLocation,
        // don't wait for listener update
        setLocation(mLocationManager.getLastKnownLocation(mProviderName));
    }

    /**
     * Store current location.
     *
     * @param locationName Descriptive name of the location to store
     */
    public final void storeCurrentLocation(final String locationName) {
        AriadneLocation currentLocation = getLocation();
        String locationStoredMessage = "";

        // don't store current location if it is not set
        if (currentLocation != null && mStoredDestination != null) {
            // check if a location name was entered
            if (locationName == null || locationName.trim().length() == 0) {
                // display a message if location name is not entered
                Toast.makeText(
                        this,
                        R.string.no_location_name,
                        Toast.LENGTH_SHORT
                ).show();

                // set message to show when location is stored
                locationStoredMessage
                        = getResources().getString(R.string.location_stored);
            } else {
                currentLocation.setName(locationName);

                // set message to show when location is stored
                locationStoredMessage = String.format(
                        getResources().getString(R.string.location_name_stored),
                        locationName
                );
            }

            mStoredDestination.save(currentLocation);
            setDestination(mStoredDestination.getLocation());
            Toast.makeText(
                    this,
                    locationStoredMessage,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    R.string.store_location_disabled,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Rename Destination.
     *
     * @param locationName Descriptive name of the location to store
     */
    public final void renameDestination(final String locationName) {
        String locationStoredMessage = "";

        // don't store current location if it is not set
        if (mStoredDestination != null) {
            // check if a location name was entered
            if (locationName == null || locationName.trim().length() == 0) {
                // display a message if location name is not entered
                Toast.makeText(
                        this,
                        R.string.no_location_name,
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                AriadneLocation location = mStoredDestination.getLocation();
                if (location != null) {
                    location.setName(locationName);

                    // set message to show when location is stored
                    locationStoredMessage = getResources().getString(
                            R.string.destination_renamed);

                    mStoredDestination.save(location);
                    setDestination(mStoredDestination.getLocation());
                    Toast.makeText(
                            this,
                            locationStoredMessage,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        } else {
            Toast.makeText(
                    this,
                    R.string.rename_destination_disabled,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Get destination.
     *
     * @return Location
     */
    public final AriadneLocation getDestination() {
        if (mNavigator == null) {
            return null;
        }

        return mNavigator.getDestination();
    }

    /**
     * Get distance to stored location.
     *
     * @return float distance in meters
     */
    public final float getDistance() {
        if (mNavigator == null) {
            return 0;
        }

        return mNavigator.getDistance();
    }

    /**
     * Get bearing to stored location.
     *
     * @return direction in ° relative to current bearing
     */
    public final double getDirection() {
        if (mNavigator == null) {
            return 0;
        }

        return mNavigator.getRelativeDirection();
    }

    /**
     * Method to register location updates with the current location provider.
     *
     * If the requested provider is not available on the device,
     * the app displays a Toast with a message referenced by a resource id.
     *
     * @return true if a location was retrieved
     */
    private boolean requestUpdatesFromProvider() {
        if (isSetLocationProvider()
                && mLastLocation != null
                && mLocationManager != null
                && mLocationManager.isProviderEnabled(mProviderName)) {

            // Get debug level from SharedPreferences
            SharedPreferences sharedPref
                    = PreferenceManager.getDefaultSharedPreferences(this);
            String prefLocationUpdateDistance
                    = sharedPref.getString(
                    SettingsActivity.KEY_PREF_LOC_UPDATE_DIST,
                    SettingsActivity.DEFAULT_PREF_LOC_UPDATE_DIST);
            String prefLocationUpdateTime
                    = sharedPref.getString(
                    SettingsActivity.KEY_PREF_LOC_UPDATE_TIME,
                    SettingsActivity.DEFAULT_PREF_LOC_UPDATE_TIME);

            /* don't allow to disable distance based updates
             * before Jelly Bean, because the time based update parameter
             * is not respected before Jelly Bean,
             * so the distance based update parameter should have a value.
             */
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                    && Integer.parseInt(prefLocationUpdateDistance) == 0) {
                prefLocationUpdateDistance =
                        SettingsActivity.DEFAULT_PREF_LOC_UPDATE_DIST;
            }

            mLocationManager.requestLocationUpdates(
                    mProviderName,
                    Integer.parseInt(prefLocationUpdateTime),
                    Integer.parseInt(prefLocationUpdateDistance),
                    mListener);
            Location location
                    = mLocationManager.getLastKnownLocation(mProviderName);

            if (location != null) {
                setLocation(location);
                return true;
            }
        } else {
            Toast.makeText(
                    this,
                    R.string.provider_no_support,
                    Toast.LENGTH_LONG
            ).show();
        }

        return false;
    }

    /**
     * Listener object to connect with LocationManager and retrieve updates.
     */
    private final LocationListener mListener = new LocationListener() {

        @Override
        public void onLocationChanged(final Location location) {
            // When new location update is received, update current location
            setLocation(location);

            // display message on update
            if (mDebug != null
                    && mDebug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_MEDIUM)
                    ) {
                Toast.makeText(
                        mContext,
                        R.string.location_updated,
                        Toast.LENGTH_SHORT
                ).show();
            }

            // Notify bound Activities of Location Update
            final int noCallbacks = mCallbacks.beginBroadcast();
            for (int i = 0; i < noCallbacks; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).locationUpdated();
                } catch (RemoteException e) {
                    // The RemoteCallbackList will take care of removing
                    // the dead object for us.
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        }

        @Override
        public void onProviderDisabled(final String provider) {
        }

        @Override
        public void onProviderEnabled(final String provider) {
        }

        @Override
        public void onStatusChanged(
                final String provider, final int status, final Bundle extras) {
        }
    };

    /**
     * Called when the orientation value changes.
     */
    public final void onOrientationChanged() {
        // Notify bound Activities of orientation Update
        final int noCallbacks = mCallbacks.beginBroadcast();
        for (int i = 0; i < noCallbacks; i++) {
            try {
                mCallbacks.getBroadcastItem(i).orientationUpdated();
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing
                // the dead object for us.
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocationBinder extends Binder {
        /**
         * Returns an instance of the bound LocationService.
         *
         * @return LocationService
         */
        public final LocationService getService() {
            // Return this instance of LocationService so clients
            // can call public methods
            return LocationService.this;
        }

        /**
         * Register a client callback.
         *
         * @param cb client callback
         */
        public final void registerCallback(final ILocationServiceCallback cb) {
            if (cb != null) {
                mCallbacks.register(cb);
            }
        }

        /**
         * Unregister a client callback.
         *
         * @param cb client callback
         */
        public final void unregisterCallback(
                final ILocationServiceCallback cb) {
            if (cb != null) {
                mCallbacks.unregister(cb);
            }
        }
    }
}
