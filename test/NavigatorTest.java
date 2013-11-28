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
 * @package com.github.ruleant.getback_gps
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */

import com.github.ruleant.getback_gps.lib.AriadneLocation;
import com.github.ruleant.getback_gps.lib.FormatUtils;
import com.github.ruleant.getback_gps.lib.Navigator;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
     * Direction from location 2 to 1 (= 360° - DIR_LOC1_2).
     */
    private static final double DIR_LOC2_1 = 225.0;

    /**
     * Direction from location 2 to 3.
     */
    private static final double DIR_LOC2_3 = 160.0;

    /**
     * Bearing 1 (60°).
     */
    private static final double BEARING_1 = 60.0;

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

        // create mock locations
        loc1 = mock(AriadneLocation.class);
        loc2 = mock(AriadneLocation.class);
        loc3 = mock(AriadneLocation.class);

        /* setup mock objects */
        // set distance
        when(loc1.distanceTo(loc2)).thenReturn(DIST_LOC1_2);
        when(loc2.distanceTo(loc1)).thenReturn(DIST_LOC1_2);
        when(loc1.distanceTo(loc3)).thenReturn(DIST_LOC1_3);
        when(loc3.distanceTo(loc1)).thenReturn(DIST_LOC1_3);
        // set direction
        when(loc1.bearingTo(loc2)).thenReturn((float) DIR_LOC1_2);
        when(loc1.bearingTo(loc3)).thenReturn((float) DIR_LOC1_3);
        when(loc2.bearingTo(loc1)).thenReturn((float) DIR_LOC2_1);
        when(loc2.bearingTo(loc3)).thenReturn((float) DIR_LOC2_3);
    }

    /**
     * Create mock setting right conditions for
     * isBearingAccurate() to return true.
     *
     * @param currentLocation Mock object for currentLocation
     * @param previousLocation Mock object for PreviousLocation
     */
    private void initMockIsBearingAccurate(
            final AriadneLocation currentLocation,
            final AriadneLocation previousLocation) {
        // set location
        navigator.setLocation(currentLocation);
        navigator.setPreviousLocation(previousLocation);

        when(currentLocation.isRecent()).thenReturn(true);
        when(previousLocation.isRecent()).thenReturn(true);
        when(currentLocation.getAccuracy()).thenReturn(ACCURACY_OK_10);
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

        assertFalse(navigator.isLocationAccurate());
        assertFalse(navigator.isBearingAccurate());
        assertFalse(navigator.isDestinationReached());
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
     * Tests getRelativeDirection.
     */
    public final void testGetRelativeDirection() {
        // initialise mock setting with right conditions for isBearingAccurate
        // (currentLocation = loc2, previousLocation = loc1)
        initMockIsBearingAccurate(loc2, loc1);

        // set destination
        navigator.setDestination(loc3);

        // get current bearing
        assertEquals(
                DIR_LOC1_2, navigator.getCurrentBearing());

        // get absolute direction
        assertEquals(
                DIR_LOC2_3, navigator.getAbsoluteDirection());

        // get relative direction
        assertEquals(
                DIR_LOC2_3 - DIR_LOC1_2, navigator.getRelativeDirection());

        // reverse bearing of mock
        // (currentLocation = loc1, previousLocation = loc2)
        initMockIsBearingAccurate(loc1, loc2);

        // get current bearing
        assertEquals(
                DIR_LOC2_1, navigator.getCurrentBearing());

        // get relative direction
        assertEquals(
                FormatUtils.normalizeAngle(DIR_LOC1_3 - DIR_LOC2_1),
                navigator.getRelativeDirection());
    }

    /**
     * Tests isDestinationReached.
     */
    public final void testIsDestinationReached() {
        // set location
        navigator.setLocation(loc1);

        // location is not accurate, check should fail
        assertFalse(navigator.isDestinationReached());

        // location is accurate, but no destination is set
        when(loc1.isRecent()).thenReturn(true);
        when(loc1.getAccuracy()).thenReturn(ACCURACY_OK_40);
        assertFalse(navigator.isDestinationReached());

        // set destination, same location as location
        navigator.setDestination(loc1);

        // destination and current location are the same,
        // check should return true
        assertEquals((float) 0, navigator.getDistance());
        assertTrue(navigator.isDestinationReached());

        // set new destination
        navigator.setDestination(loc2);

        // distance between location and destination is smaller than accuracy,
        // check should return true
        assertTrue(navigator.isDestinationReached());

        // distance between location and destination is bigger than accuracy,
        // check should return false
        when(loc1.getAccuracy()).thenReturn(ACCURACY_OK_10);
        assertFalse(navigator.isDestinationReached());
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
     * Tests getCurrentBearing of current location.
     */
    public final void testGetBearingCurrLoc() {
        // set location
        navigator.setLocation(loc1);

        // get current bearing
        assertEquals(Navigator.DIR_ZERO, navigator.getCurrentBearing());

        // mock : define hasSpeed
        when(loc1.hasBearing()).thenReturn(true);

        // get current bearing
        assertEquals(Navigator.DIR_ZERO, navigator.getCurrentBearing());

        // mock : define getBearing
        when(loc1.getBearing()).thenReturn((float) BEARING_1);

        // get current bearing
        assertEquals(BEARING_1, navigator.getCurrentBearing());
    }

    /**
     * Tests getCurrentBearing, calculated by current and previous location.
     */
    public final void testGetBearingPrevLoc() {
        // set location
        navigator.setLocation(loc1);

        // Bearing is zero if there is no previous location
        assertEquals(Navigator.DIR_ZERO, navigator.getCurrentBearing());

        navigator.setLocation(loc2);

        // Bearing is zero if the current bearing is not accurate
        assertEquals(Navigator.DIR_ZERO, navigator.getCurrentBearing());

        // initialise mock setting right conditions for isBearingAccurate
        initMockIsBearingAccurate(loc2, loc1);

        // get current bearing
        assertEquals(DIR_LOC1_2, navigator.getCurrentBearing());
    }

    /**
     * Tests location accuracy.
     */
    public final void testIsLocationAccurate() {
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
