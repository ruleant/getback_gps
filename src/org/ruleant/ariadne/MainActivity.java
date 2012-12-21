/**
 * Main Activity
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
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * Main Activity class
 * 
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class MainActivity extends Activity {
	private LocationManager locationManager;
	private String providerName = "";
	private Location location = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		locationManager =
				(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		if (! getLocationProvider().isEmpty()) {
			location = locationManager.getLastKnownLocation(providerName);
			refreshInterface();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Called when the user clicks the Renew provider button
	 * 
	 * @return void
	 */
	public void renewProvider(View view) {
		getLocationProvider();
		refreshInterface();
	}

	/**
	 * Called when the user clicks the Renew location button
	 *
	 * @return void
	 */
	public void renewLocation(View view) {
		getLocation();
		refreshInterface();
	}
	
	/**
	 * Retrieve Location Provider
	 *
	 * Define best location provider based on certain criteria
	 *
	 * @return String
	 */
	private String getLocationProvider() {
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
	private Location getLocation() {
		if (locationManager == null || providerName.isEmpty()) {
			return null;
		}
		// todo : use more accurate location (with listener object)
		location = locationManager.getLastKnownLocation(providerName);

		return location;
	}

	/**
	 * refresh interface messages
	 * 
	 * Refresh the values of Location Provider, Location, ...
	 * 
	 * @return void
	 */
	private void refreshInterface() {
		// Refresh locationProvider
		TextView tv_provider = (TextView) findViewById(R.id.textView_LocationProvider);
		String providerText = getResources().getString(R.string.location_provider) + ": ";
		if (providerName.isEmpty()) {
			providerText += getResources().getString(R.string.none);
		} else {
			providerText += providerName;
		}
		tv_provider.setText(providerText);

		// Refresh Location
		TextView tv_location = (TextView) findViewById(R.id.textView_Location);
		String locationText = getResources().getString(R.string.location) + ": ";
		if (location == null) {
			locationText += getResources().getString(R.string.unknown);
		} else {
			locationText += location.toString();
		}
		tv_location.setText(locationText);
	}
}
