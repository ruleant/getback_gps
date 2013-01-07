/**
 * Location Service
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
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Location Service provides the current location
 * 
 * This service will connect to the Location Provider and retrieves the current location
 * 
 * @package org.ruleant.ariadne
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LocationService extends Service {
    // Binder given to clients
	private final IBinder mBinder = new LocationBinder();
	private LocationManager mLocationManager;
	private String mProviderName = "";
	private Location mCurrentLocation = null;
	private Location mPreviousLocation = null;
	private LocationStore mStoredLocation;

	private static final int TEN_SECONDS = 10000;
	private static final int TEN_METERS = 10;
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show();
		mLocationManager =
				(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mStoredLocation = new LocationStore(this.getApplicationContext());
		
		if (! getLocationProvider().isEmpty()) {
			setLocation(requestUpdatesFromProvider(mProviderName));
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// The service is starting, due to a call to startService()
	    return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "service bound", Toast.LENGTH_SHORT).show();
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Toast.makeText(this, "service unbound", Toast.LENGTH_SHORT).show();
		// don't allow rebind
		return false;
	}

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocationBinder extends Binder {
		LocationService getService() {
			// Return this instance of LocationService so clients can call public methods
			return LocationService.this;
		}
	}

	@Override
	public void onDestroy() {
		// The service is no longer used and is being destroyed
		mLocationManager.removeUpdates(mListener);
		mStoredLocation.save();
		mCurrentLocation = null;
		mPreviousLocation = null;
		mProviderName = "";
		mLocationManager = null;
		mStoredLocation = null;
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Retrieve Location Provider
	 *
	 * Define best location provider based on certain criteria
	 *
	 * @return String
	 */
	public String getLocationProvider() {
		// Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
		// TODO : define criteria in settings
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		if (mLocationManager != null) {
			mProviderName = mLocationManager.getBestProvider(criteria, true);
		}
		
		return mProviderName;
	}
	
	/**
	 * Set Location
	 *
	 * Update location
	 *
	 * @param Location location New location
	 */
	public void setLocation(Location location) {
		if (location == null) {
			return;
		}
		mPreviousLocation = mCurrentLocation;
		mCurrentLocation = location;
	}

	/**
	 * Retrieve Location
	 *
	 * Get last known location
	 *
	 * @return Location
	 */
	public Location getLocation() {
		if (mLocationManager == null || mProviderName.isEmpty()) {
			return null;
		}
		// TODO : remove later
		// location is not updated by listener (bug),
		// update location using getLastKnowLocation (when refresh location button is pushed)
		setLocation(mLocationManager.getLastKnownLocation(mProviderName));

		return mCurrentLocation;
	}

	/**
	 * Store current location
	 *
	 * @return void
	 */
	public void storeCurrentLocation() {
		mStoredLocation.setLocation(mCurrentLocation);
	}

	/**
	 * Get stored location
	 *
	 * @return void
	 */
	public Location getStoredLocation() {
		return mStoredLocation.getLocation();
	}

	/**
	 * Method to register location updates with a desired location provider.  If the requested
	 * provider is not available on the device, the app displays a Toast with a message referenced
	 * by a resource id.
	 *
	 * @param provider Name of the requested provider.
	 * @param errorResId Resource id for the string message to be displayed if the provider does
	 *                   not exist on the device.
	 * @return A previously returned {@link android.location.Location} from the requested provider,
	 *         if exists.
	 */
	private Location requestUpdatesFromProvider(final String provider) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			// TODO : define criteria in settings
			mLocationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, mListener);
			location = mLocationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, R.string.provider_no_support, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	private final LocationListener mListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// When new location update is received, update current location
			setLocation(location);
			// TODO : notify bound Activities of Location Update
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
}
