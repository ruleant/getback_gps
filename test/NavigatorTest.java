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
package org.ruleant.ariadne;

import junit.framework.TestCase;

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
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        navigator = new Navigator();
    }

    /**
     * Tests no location or destination.
     */
    public final void testNoValue() {
        assertNull(navigator.getLocation());
        assertNull(navigator.getDestination());

        assertEquals(F_ZERO, navigator.getDistance());
        assertEquals(0.0, navigator.getCurrentBearing());
        assertEquals(0.0, navigator.getAbsoluteDirection());
        assertEquals(0.0, navigator.getRelativeDirection());
        assertEquals(F_ZERO, navigator.getCurrentSpeed());
    }
}
