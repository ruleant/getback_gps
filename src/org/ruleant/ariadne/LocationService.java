/**
 * Location Service
 * 
 * Copyright (C) 2012 Dieter Adriaenssens
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
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

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
	private final IBinder mBinder = new LocalBinder();
	private LocationManager locationManager;
	private String providerName = "";
	private Location location = null;
	
	@Override
	public void onCreate() {
		locationManager =
				(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		if (! getLocationProvider().isEmpty()) {
			location = locationManager.getLastKnownLocation(providerName);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// The service is starting, due to a call to startService()
	    return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// don't allow rebind
		return false;
	}

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    	location = null;
    	providerName = "";
    	locationManager = null;
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
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		if (locationManager != null) {
			providerName = locationManager.getBestProvider(criteria, true);
		}
		
		return providerName;
	}
	
	/**
	 * Retrieve Location
	 *
	 * Get last known location
	 *
	 * @return Location
	 */
	public Location getLocation() {
		if (locationManager == null || providerName.isEmpty()) {
			return null;
		}
		// todo : use more accurate location (with listener object)
		location = locationManager.getLastKnownLocation(providerName);

		return location;
	}
}
