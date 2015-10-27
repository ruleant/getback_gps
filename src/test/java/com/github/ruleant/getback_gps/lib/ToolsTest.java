/**
 * Unit tests for Tools class
 *
 * Copyright (C) 2014-2015 Dieter Adriaenssens
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
package com.github.ruleant.getback_gps.lib;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class ToolsTest {
    /**
     * Expected Exception.
     */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * Test value 1.
     */
    private static final long SMALL_VALUE = 10;

    /**
     * Test value 2.
     */
    private static final long BIG_VALUE = 100;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public final void setUp() {
    }

    /**
     * Test getMax() method.
     */
    @Test
    public final void testGetMax() {
        // equal values
        assertEquals(0, Tools.getMax(0, 0));
        assertEquals(1, Tools.getMax(1, 1));
        assertEquals(-1, Tools.getMax(-1, -1));

        // each parameter can be biggest
        assertEquals(1, Tools.getMax(0, 1));
        assertEquals(1, Tools.getMax(1, 0));
        assertEquals(0, Tools.getMax(0, -1));
        assertEquals(0, Tools.getMax(-1, 0));

        // a positive value is bigger than negative
        assertEquals(1, Tools.getMax(-1, 1));
        assertEquals(1, Tools.getMax(1, -1));

        // bigger numbers
        assertEquals(SMALL_VALUE, Tools.getMax(0, SMALL_VALUE));
        assertEquals(BIG_VALUE, Tools.getMax(0, BIG_VALUE));
        assertEquals(Long.MAX_VALUE, Tools.getMax(0, Long.MAX_VALUE));

        assertEquals(BIG_VALUE, Tools.getMax(BIG_VALUE, SMALL_VALUE));
        assertEquals(BIG_VALUE, Tools.getMax(SMALL_VALUE, BIG_VALUE));
        assertEquals(Long.MAX_VALUE,
                Tools.getMax(SMALL_VALUE, Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, Tools.getMax(BIG_VALUE, Long.MAX_VALUE));

        // smaller numbers
        assertEquals(0, Tools.getMax(0, -1 * SMALL_VALUE));
        assertEquals(0, Tools.getMax(0, -1 * BIG_VALUE));
        assertEquals(0, Tools.getMax(0, Long.MIN_VALUE));

        assertEquals(-1 * SMALL_VALUE,
                Tools.getMax(-1 * BIG_VALUE, -1 * SMALL_VALUE));
        assertEquals(-1 * SMALL_VALUE,
                Tools.getMax(-1 * SMALL_VALUE, -1 * BIG_VALUE));
        assertEquals(-1 * SMALL_VALUE,
                Tools.getMax(-1 * SMALL_VALUE, Long.MIN_VALUE));
        assertEquals(-1 * BIG_VALUE,
                Tools.getMax(-1 * BIG_VALUE, Long.MIN_VALUE));
    }

    /**
     * Tests range of currentTimestamp parameter of method isTimestampRecent.
     */
    @Test
    public final void testIsTimestampRecentRangeCurrent() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("currentTimestamp can't be a negative value");

        Tools.isTimestampRecent(-1, 1, 1);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests range of previousTimestamp parameter of method isTimestampRecent.
     */
    @Test
    public final void testIsTimestampRecentRangePrevious() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("previousTimestamp can't be a negative value");

        Tools.isTimestampRecent(1, -1, 1);
    }

    /**
     * Tests range of validity parameter of method isTimestampRecent,
     * shouldn't be zero.
     */
    @Test
    public final void testIsTimestampRecentRangeValidityZero() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("validity should be a non-zero positive value");

        Tools.isTimestampRecent(1, 1, 0);
    }

    /**
     * Tests range of validity parameter of method isTimestampRecent,
     * shouldn't be negative.
     */
    @Test
    public final void testIsTimestampRecentRangeValidityNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("validity should be a non-zero positive value");

        Tools.isTimestampRecent(1, 1, -1);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests isTimestampRecent.
     */
    @Test
    public final void testIsTimestampRecent() {
        // timestamps are equal and within range
        assertTrue(Tools.isTimestampRecent(0, 0, 1));

        // timestamp is more recent and within range
        assertTrue(Tools.isTimestampRecent(1, 0, 1));
        assertTrue(Tools.isTimestampRecent(2, 1, 1));
        assertTrue(Tools.isTimestampRecent(2, 1, 2));
        assertTrue(Tools.isTimestampRecent(2, 0, 2));

        // timestamp is more recent and not within range
        assertFalse(Tools.isTimestampRecent(2, 0, 1));

        // timestamp is not more recent and not within range
        assertFalse(Tools.isTimestampRecent(0, 2, 1));

        // timestamp is not more recent but within range
        assertFalse(Tools.isTimestampRecent(0, 1, 1));
        assertFalse(Tools.isTimestampRecent(1, 2, 1));
        assertFalse(Tools.isTimestampRecent(1, 2, 2));
        assertFalse(Tools.isTimestampRecent(0, 2, 2));
    }
}
