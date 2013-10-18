/**
 * Unit tests for Navigator class
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

import junit.framework.TestCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.ruleant.ariadne.lib.AriadneLocation;
import org.ruleant.ariadne.lib.Navigator;

/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class NavigatorTest extends TestCase {
    /**
     * Instance of the navigator class.
     */
    private Navigator navigator;

    /**
     * Distance between location 1 and 2.
     */
    private static final float DIST_LOC1_2 = 20;

    /**
     * Distance between location 1 and 3.
     */
    private static final float DIST_LOC1_3 = 30;

    /**
     * Direction from location 1 to 2.
     */
    private static final double DIR_LOC1_2 = 45.0;

    /**
     * Direction from location 1 to 3.
     */
    private static final double DIR_LOC1_3 = 135.0;

    /**
     * Timestamp 1.
     */
    private static final long TIMESTAMP_1 = 5000;

    /**
     * Timestamp 2.
     */
    private static final long TIMESTAMP_2 = 10000;

    /**
     * Timestamp 3.
     */
    private static final long TIMESTAMP_3 = 15000;

    /**
     * Speed between location 1 at timestamp 1 and location 2 at timestamp 2.
     * Speed = 20m / 5s = 4m/s
     */
    private static final float SPEED_1_2 = 4;

    /**
     * Accuracy is 10 meter (OK).
     */
    private static final float ACCURACY_OK_10 = 10;

    /**
     * Accuracy is 40 meter (OK).
     */
    private static final float ACCURACY_OK_40 = 40;

    /**
     * Accuracy is 60 meter (too low).
     */
    private static final float ACCURACY_LOW_60 = 60;

    /**
     * Test location 1.
     */
    private AriadneLocation loc1 = null;

    /**
     * Test location 2.
     */
    private AriadneLocation loc2 = null;

    /**
     * Test location 3.
     */
    private AriadneLocation loc3 = null;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        navigator = new Navigator();

        loc1 = mock(AriadneLocation.class);

        loc2 = mock(AriadneLocation.class);
        //loc2.setLatitude(0.0);
        //loc2.setLongitude(1.0);

        loc3 = mock(AriadneLocation.class);
        //loc3.setLatitude(0.0);
        //loc3.setLongitude(4.0);

        /* setup mock objects */
        // set distance
        when(loc1.distanceTo(loc2)).thenReturn(DIST_LOC1_2);
        when(loc2.distanceTo(loc1)).thenReturn(DIST_LOC1_2);
        when(loc1.distanceTo(loc3)).thenReturn(DIST_LOC1_3);
        when(loc3.distanceTo(loc1)).thenReturn(DIST_LOC1_3);
        // set direction
        when(loc1.bearingTo(loc2)).thenReturn((float) DIR_LOC1_2);
        when(loc1.bearingTo(loc3)).thenReturn((float) DIR_LOC1_3);
    }

    /**
     * Tests no location or destination.
     */
    public final void testNoValue() {
        assertNull(navigator.getLocation());
        assertNull(navigator.getPreviousLocation());
        assertNull(navigator.getDestination());

        assertEquals(Navigator.DIST_ZERO, navigator.getDistance());
        assertEquals(Navigator.DIR_ZERO, navigator.getCurrentBearing());
        assertEquals(Navigator.DIR_ZERO, navigator.getAbsoluteDirection());
        assertEquals(Navigator.DIR_ZERO, navigator.getRelativeDirection());
        assertEquals(Navigator.SPEED_ZERO, navigator.getCurrentSpeed());
    }

    /**
     * Tests setting a location.
     */
    public final void testSetLocation() {
        // set location
        navigator.setLocation(loc1);
        assertEquals(loc1, navigator.getLocation());
        assertNull(navigator.getPreviousLocation());

        // set same location
        navigator.setLocation(loc1);
        assertEquals(loc1, navigator.getLocation());
        assertEquals(loc1, navigator.getPreviousLocation());

        // set new location
        navigator.setLocation(loc2);
        assertEquals(loc2, navigator.getLocation());
        assertEquals(loc1, navigator.getPreviousLocation());

        // set empty location
        navigator.setLocation(null);
        assertNull(navigator.getLocation());
        assertEquals(loc2, navigator.getPreviousLocation());
    }

    /**
     * Tests setting previous location.
     */
    public final void testSetPreviousLocation() {
        // set previous location
        navigator.setPreviousLocation(loc2);
        assertNull(navigator.getLocation());
        assertEquals(loc2, navigator.getPreviousLocation());

        // set new location
        navigator.setLocation(loc1);
        assertEquals(loc1, navigator.getLocation());
        assertNull(navigator.getPreviousLocation());

        // set previous location
        navigator.setPreviousLocation(loc2);
        assertEquals(loc1, navigator.getLocation());
        assertEquals(loc2, navigator.getPreviousLocation());

        // set empty previous location
        navigator.setPreviousLocation(null);
        assertEquals(loc1, navigator.getLocation());
        assertNull(navigator.getPreviousLocation());
    }

    /**
     * Tests setting a destination.
     */
    public final void testSetDestination() {
        // set destination
        navigator.setDestination(loc3);
        assertEquals(loc3, navigator.getDestination());

        // set destination
        navigator.setDestination(null);
        assertNull(navigator.getDestination());
    }

    /**
     * Tests without a current Location.
     */
    public final void testNoLocation() {
        // set destination
        navigator.setDestination(loc3);
        assertEquals(Navigator.DIST_ZERO, navigator.getDistance());
        assertEquals(Navigator.DIR_ZERO, navigator.getAbsoluteDirection());
    }

    /**
     * Tests without a Destination.
     */
    public final void testGetDistanceNoDestination() {
        // set location
        navigator.setLocation(loc1);
        assertEquals(Navigator.DIST_ZERO, navigator.getDistance());
        assertEquals(Navigator.DIR_ZERO, navigator.getAbsoluteDirection());
    }

    /**
     * Tests getDistance.
     */
    public final void testGetDistance() {
        // set location
        navigator.setLocation(loc1);
        // set destination
        navigator.setDestination(loc3);

        // test Distance
        assertEquals(DIST_LOC1_3, navigator.getDistance());

        // set another destination
        navigator.setDestination(loc2);
        // test Distance
        assertEquals(DIST_LOC1_2, navigator.getDistance());
    }

    /**
     * Tests getAbsoluteDirection.
     */
    public final void testGetAbsoluteDirection() {
        // set location
        navigator.setLocation(loc1);
        // set destination
        navigator.setDestination(loc3);

        // get Absolute Direction
        assertEquals(DIR_LOC1_3, navigator.getAbsoluteDirection());

        // set another destination
        navigator.setDestination(loc2);
        // test Distance
        assertEquals(DIR_LOC1_2, navigator.getAbsoluteDirection());
    }

    /**
     * Tests getSpeed of current location.
     */
    public final void testGetSpeed() {
        // set location
        navigator.setLocation(loc1);

        // get Speed
        assertEquals(Navigator.SPEED_ZERO, navigator.getCurrentSpeed());

        // mock : define hasSpeed
        when(loc1.hasSpeed()).thenReturn(true);

        // get Speed
        assertEquals(Navigator.SPEED_ZERO, navigator.getCurrentSpeed());

        // mock : define getSpeed
        when(loc1.getSpeed()).thenReturn(SPEED_1_2);

        // get Speed
        assertEquals(SPEED_1_2, navigator.getCurrentSpeed());
    }

    /**
     * Tests getSpeed, calculated by current and previous location.
     */
    public final void testGetSpeedPrevLoc() {
        // set location
        navigator.setLocation(loc1);
        navigator.setLocation(loc1);

        // speed is zero when current and last location are the same
        assertEquals(Navigator.SPEED_ZERO, navigator.getCurrentSpeed());

        navigator.setLocation(loc2);

        // mock : define getTime of location 1 and 2
        when(loc1.getTime()).thenReturn(TIMESTAMP_1);
        when(loc2.getTime()).thenReturn(TIMESTAMP_2);

        // get Speed
        assertEquals(SPEED_1_2, navigator.getCurrentSpeed());

        // mock : define getTime of location 1
        when(loc1.getTime()).thenReturn(TIMESTAMP_3);

        // get Speed
        // should be zero because the timestamp of the previous location is
        // more recent than that of the current location
        assertEquals(Navigator.SPEED_ZERO, navigator.getCurrentSpeed());
    }

    /**
     * Tests location accuracy.
     */
    public final void testIsLocationAccurate() {
        // location is inaccurate because no location is set
        assertFalse(navigator.isLocationAccurate());

        // set location
        navigator.setLocation(loc1);

        // location is inaccurate because timestamp is not recent
        // and accuracy is above threshold
        when(loc1.isRecent()).thenReturn(false);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_LOW_60);
        assertFalse(navigator.isLocationAccurate());

        // location is inaccurate because accuracy is above threshold,
        // while timestamp is recent
        when(loc1.isRecent()).thenReturn(true);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_LOW_60);
        assertFalse(navigator.isLocationAccurate());

        // location is inaccurate because timestamp is not recent,
        // while accuracy is OK
        when(loc1.isRecent()).thenReturn(false);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_OK_40);
        assertFalse(navigator.isLocationAccurate());

        // location is accurate because timestamp is recent
        // and accuracy is OK
        when(loc1.isRecent()).thenReturn(true);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_OK_40);
        assertTrue(navigator.isLocationAccurate());
    }

    /**
     * Tests bearing accuracy.
     */
    public final void testIsBearingAccurate() {
        // location is inaccurate because no location is set
        assertFalse(navigator.isBearingAccurate());

        // set location
        navigator.setLocation(loc1);

        // location is accurate
        when(loc1.isRecent()).thenReturn(true);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_OK_40);

        // isLocationAccurate is true,
        // but previous location is not set so bearing is not accurate
        assertFalse(navigator.isBearingAccurate());

        // set previous location (= same as current location)
        navigator.setPreviousLocation(loc1);
        // current and previous location are the same,
        // so bearing is not accurate
        assertFalse(navigator.isBearingAccurate());

        // set previous location (= different as current location)
        navigator.setPreviousLocation(loc2);
        // previous location is not recent
        when(loc2.isRecent()).thenReturn(false);
        assertFalse(navigator.isBearingAccurate());

        // previous location is recent, but distance between
        // previous and current location is smaller than current accuracy
        when(loc2.isRecent()).thenReturn(true);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_OK_40);
        assertFalse(navigator.isBearingAccurate());

        // distance between previous and current location
        // is larger than current accuracy
        when(loc2.isRecent()).thenReturn(true);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_OK_10);
        assertTrue(navigator.isBearingAccurate());
    }
}
