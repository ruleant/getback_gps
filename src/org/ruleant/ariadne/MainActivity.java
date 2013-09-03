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

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        boolean superResult = super.onCreateOptionsMenu(menu);

        // Inflate the menu;
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return superResult;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by onOptionsItemSelected()
        switch (item.getItemId()) {
            case R.id.menu_details:
                displayDetails(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the user clicks the About menu item.
     *
     * @param item MenuItem object that was clicked
     */
    public final void displayDetails(final MenuItem item) {
        Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }

    /**
     * Refresh display : refresh the values of Location Provider, Location, ...
     */
    protected final void refreshDisplay() {
        // only refresh items if activity is bound to service
        if (!isBound()) {
            return;
        }

        super.refreshDisplay();

        LocationService service = getService();
        Navigator navigator = service.getNavigator();
        Resources res = getResources();
        AriadneLocation destination = null;

        // get Destination from service
        try {
            destination = new AriadneLocation(navigator.getDestination());
        } catch (Exception e) {
            e.printStackTrace();
            destination = null;
        }

        // Refresh Directions to destination
        TextView tvToDestinationDistance
                = (TextView) findViewById(R.id.textView_toDestDist);
        TextView tvToDestinationDirection
                = (TextView) findViewById(R.id.textView_toDestDir);
        TextView tvToDestinationDirectionRel
                = (TextView) findViewById(R.id.textView_toDestDirRel);

        String toDestinationDistanceText = null;
        String toDestinationDirectionText = null;
        String toDestinationDirectionRelText = null;


        if (destination == null || !navigator.isLocationAccurate()) {
            toDestinationDistanceText = res.getString(R.string.unknown);
            toDestinationDirectionText = res.getString(R.string.unknown);
        } else {
            // Print distance and bearing
            toDestinationDistanceText
                    = FormatUtils.formatDist(navigator.getDistance());
            toDestinationDirectionText
                    = FormatUtils.formatAngle(
                        FormatUtils.normalizeAngle(
                            navigator.getAbsoluteDirection()));
        }

        // if bearing is inaccurate, don't display relative direction
        if (navigator.isBearingAccurate()) {
            toDestinationDirectionRelText
                    = FormatUtils.formatAngle(
                        navigator.getRelativeDirection());
        } else {
            toDestinationDirectionRelText
                    = res.getString(R.string.inaccurate);
        }


        tvToDestinationDistance.setText(toDestinationDistanceText);
        tvToDestinationDirection.setText(toDestinationDirectionText);
        tvToDestinationDirectionRel.setText(toDestinationDirectionRelText);
    }
}
