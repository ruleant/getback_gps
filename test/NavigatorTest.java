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
import static org.mockito.Mockito.*;

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
     * Zero.
     */
    private static final float F_ZERO = 0;

    /**
     * Fifty.
     */
    private static final long L_FIFTY = 50;

    /**
     * Sixty.
     */
    private static final float F_SIXTY = 60;

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
    }

    /**
     * Tests no location or destination.
     */
    public final void testNoValue() {
        assertNull(navigator.getLocation());
        assertNull(navigator.getPreviousLocation());
        assertNull(navigator.getDestination());

        assertEquals(F_ZERO, navigator.getDistance());
        assertEquals(0.0, navigator.getCurrentBearing());
        assertEquals(0.0, navigator.getAbsoluteDirection());
        assertEquals(0.0, navigator.getRelativeDirection());
        assertEquals(F_ZERO, navigator.getCurrentSpeed());
    }

    /**
     * Tests setting a location.
     */
    public final void testSetLocation() {
        // set location
        navigator.setLocation(loc1);
        assertEquals(loc1, navigator.getLocation());
        assertNull(navigator.getPreviousLocation());

        // set same location, location should not be updated
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
     * Tests location.
     */
    public final void testLocation() {
        when(loc1.getTime()).thenReturn(L_FIFTY);
        //when(loc1.isRecent()).thenReturn(true);
        //doReturn(true).when(loc1).isRecent();
        //doReturn(F_SIXTY).when(loc1).getAccuracy();
        when(loc1.getAccuracy()).thenReturn(F_SIXTY);

        navigator.setLocation(loc1);
        assertEquals(loc1, navigator.getLocation());

        assertFalse(navigator.isLocationAccurate());
    }
}
