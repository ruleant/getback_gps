/**
 * Main Activity
 *
 * Copyright (C) 2012-2014 Dieter Adriaenssens
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

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ruleant.getback_gps.lib.AriadneLocation;
import com.github.ruleant.getback_gps.lib.CardinalDirection;
import com.github.ruleant.getback_gps.lib.DebugLevel;
import com.github.ruleant.getback_gps.lib.FormatUtils;
import com.github.ruleant.getback_gps.lib.Navigator;

/**
 * Main Activity class.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class MainActivity extends AbstractGetBackGpsActivity {
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        boolean superResult = super.onCreateOptionsMenu(menu);

        DebugLevel debug = new DebugLevel(this);

        // don't add details button when debugging is disabled
        if (debug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_LOW)) {
            // Inflate the menu;
            // this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
        }

        return superResult;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by onOptionsItemSelected()
        if (item.getItemId() == R.id.menu_details) {
            displayDetails(item);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuItem miDetails = menu.findItem(R.id.menu_details);
        DebugLevel debug = new DebugLevel(this);

        if (miDetails != null) {
            // hide details button when debugging is disabled
            miDetails.setVisible(
                    debug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_LOW));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Called when the user clicks the About menu item.
     *
     * @param item MenuItem object that was clicked
     */
    final void displayDetails(final MenuItem item) {
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }

    @Override
    protected final boolean refreshDisplay() {
        if (!super.refreshDisplay()) {
            return false;
        }

        // refresh views with "current" info
        // don't display value if it is inaccurate
        refreshCurrentViews(false);

        // only refresh items if activity is bound to service
        // connection state is checked in getNavigator
        Navigator navigator = getNavigator();

        if (navigator == null) {
            return false;
        }

        Resources res = getResources();

        // get Destination from service
        AriadneLocation destination = navigator.getDestination();

        // Refresh Directions to destination
        NavigationView nvToDestination
                = (NavigationView) findViewById(R.id.navigationView_ToDest);
        TextView tvToDestinationDistance
                = (TextView) findViewById(R.id.textView_toDestDist);
        TextView tvToDestinationDirection
                = (TextView) findViewById(R.id.textView_toDestDir);

        LinearLayout sectionToDestination
                = (LinearLayout) findViewById(R.id.section_toDestination);
        TextView tvToDestinationMessage
                = (TextView) findViewById(R.id.textView_toDest_Message);

        String toDestinationDistanceText = res.getString(R.string.unknown);
        String toDestinationDirectionText = res.getString(R.string.unknown);
        String toDestinationMessage = res.getString(R.string.unknown);
        NavigationView.NavigationMode nvMode
                = NavigationView.NavigationMode.Disabled;
        Boolean displayToDest = false;

        if (destination == null) {
            toDestinationMessage
                    = res.getString(R.string.no_destination);
        } else if (navigator.isDestinationReached()) {
            toDestinationMessage
                    = res.getString(R.string.destination_reached);
        } else {
            displayToDest = true;

            if (navigator.isLocationAccurate()) {
                // Set distance to destination
                toDestinationDistanceText
                        = FormatUtils.formatDist(navigator.getDistance());

                // Set direction to destination
                CardinalDirection cd = new CardinalDirection(
                        this,
                        FormatUtils.normalizeAngle(
                                navigator.getAbsoluteDirection()));
                toDestinationDirectionText = cd.format();

                // if bearing is accurate, display relative direction
                // if not, display absolute direction
                if (navigator.isBearingAccurate()) {
                    nvToDestination.setDirection(
                            navigator.getRelativeDirection());
                    nvToDestination.setAzimuth(
                            navigator.getCurrentBearing());
                    nvMode = NavigationView.NavigationMode.Accurate;
                } else {
                    nvToDestination.setDirection(
                            navigator.getAbsoluteDirection());
                    nvMode = NavigationView.NavigationMode.Inaccurate;
                }
            }
        }

        if (displayToDest) {
            // show 'to Destination' info, hide message
            sectionToDestination.setVisibility(LinearLayout.VISIBLE);
            tvToDestinationMessage.setVisibility(LinearLayout.INVISIBLE);

            // update views
            tvToDestinationDistance.setText(toDestinationDistanceText);
            tvToDestinationDirection.setText(toDestinationDirectionText);
        } else {
            // hide 'to Destination' info, show message
            sectionToDestination.setVisibility(LinearLayout.INVISIBLE);
            tvToDestinationMessage.setVisibility(LinearLayout.VISIBLE);

            // update views
            tvToDestinationMessage.setText(toDestinationMessage);
        }

        // update views
        nvToDestination.setMode(nvMode);
        nvToDestination.invalidate();

        return true;
    }
}
