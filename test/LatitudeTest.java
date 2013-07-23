/**
 * Unit tests for Latitude class
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
public class LatitudeTest extends TestCase {
    /**
     * Instance of the latitude class.
     */
    private Latitude latitude;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        latitude = new Latitude();
    }

    /**
     * Tests empty value.
     */
    /*public final void testNoValue() {
        // TODO add Stub for Location.convert
        assertNull(latitude.format());
    }*/

    /**
     * Tests value.
     */
    /*public final void testValue() {
        // TODO add Stub for Location.convert
        latitude.setValue(4);
        assertEquals("4° 0' 0\" N", latitude.format());
    }*/

    /**
     * Tests getSegment.
     */
    public final void testGetSegment() {
        assertEquals(Latitude.SEGMENT_NORTH, latitude.getSegment());

        latitude.setValue(4);
        assertEquals(Latitude.SEGMENT_NORTH, latitude.getSegment());

        latitude.setValue(-4);
        assertEquals(Latitude.SEGMENT_SOUTH, latitude.getSegment());
    }
}