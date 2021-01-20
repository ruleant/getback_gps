/**
 * Unit tests for Tools class
 *
 * Copyright (C) 2014-2021 Dieter Adriaenssens
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class ToolsTest {
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
    @BeforeEach
    public final void setUp() {
        // nothing to set up
    }

    /**
     * Test getMax() method.
     */
    @Test
    public final void testGetMax() {
        // equal values
        Assertions.assertEquals(0, Tools.getMax(0, 0));
        Assertions.assertEquals(1, Tools.getMax(1, 1));
        Assertions.assertEquals(-1, Tools.getMax(-1, -1));

        // each parameter can be biggest
        Assertions.assertEquals(1, Tools.getMax(0, 1));
        Assertions.assertEquals(1, Tools.getMax(1, 0));
        Assertions.assertEquals(0, Tools.getMax(0, -1));
        Assertions.assertEquals(0, Tools.getMax(-1, 0));

        // a positive value is bigger than negative
        Assertions.assertEquals(1, Tools.getMax(-1, 1));
        Assertions.assertEquals(1, Tools.getMax(1, -1));

        // bigger numbers
        Assertions.assertEquals(SMALL_VALUE, Tools.getMax(0, SMALL_VALUE));
        Assertions.assertEquals(BIG_VALUE, Tools.getMax(0, BIG_VALUE));
        Assertions.assertEquals(Long.MAX_VALUE, Tools.getMax(0, Long.MAX_VALUE));

        Assertions.assertEquals(BIG_VALUE, Tools.getMax(BIG_VALUE, SMALL_VALUE));
        Assertions.assertEquals(BIG_VALUE, Tools.getMax(SMALL_VALUE, BIG_VALUE));
        Assertions.assertEquals(Long.MAX_VALUE,
                Tools.getMax(SMALL_VALUE, Long.MAX_VALUE));
        Assertions.assertEquals(Long.MAX_VALUE, Tools.getMax(BIG_VALUE, Long.MAX_VALUE));

        // smaller numbers
        Assertions.assertEquals(0, Tools.getMax(0, -1 * SMALL_VALUE));
        Assertions.assertEquals(0, Tools.getMax(0, -1 * BIG_VALUE));
        Assertions.assertEquals(0, Tools.getMax(0, Long.MIN_VALUE));

        Assertions.assertEquals(-1 * SMALL_VALUE,
                Tools.getMax(-1 * BIG_VALUE, -1 * SMALL_VALUE));
        Assertions.assertEquals(-1 * SMALL_VALUE,
                Tools.getMax(-1 * SMALL_VALUE, -1 * BIG_VALUE));
        Assertions.assertEquals(-1 * SMALL_VALUE,
                Tools.getMax(-1 * SMALL_VALUE, Long.MIN_VALUE));
        Assertions.assertEquals(-1 * BIG_VALUE,
                Tools.getMax(-1 * BIG_VALUE, Long.MIN_VALUE));
    }

    /**
     * Tests range of currentTimestamp parameter of method isTimestampRecent.
     */
    @Test
    public final void testIsTimestampRecentRangeCurrent() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Tools.isTimestampRecent(-1, 1, 1);
        });
    }

    /**
     * Tests range of previousTimestamp parameter of method isTimestampRecent.
     */
    @Test
    public final void testIsTimestampRecentRangePrevious() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Tools.isTimestampRecent(1, -1, 1);
        });
    }

    /**
     * Tests range of validity parameter of method isTimestampRecent,
     * shouldn't be zero.
     */
    @Test
    public final void testIsTimestampRecentRangeValidityZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Tools.isTimestampRecent(1, 1, 0);
        });
    }

    /**
     * Tests range of validity parameter of method isTimestampRecent,
     * shouldn't be negative.
     */
    @Test
    public final void testIsTimestampRecentRangeValidityNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Tools.isTimestampRecent(1, 1, -1);
        });
    }

    /**
     * Tests isTimestampRecent.
     */
    @Test
    public final void testIsTimestampRecent() {
        // timestamps are equal and within range
        Assertions.assertTrue(Tools.isTimestampRecent(0, 0, 1));

        // timestamp is more recent and within range
        Assertions.assertTrue(Tools.isTimestampRecent(1, 0, 1));
        Assertions.assertTrue(Tools.isTimestampRecent(2, 1, 1));
        Assertions.assertTrue(Tools.isTimestampRecent(2, 1, 2));
        Assertions.assertTrue(Tools.isTimestampRecent(2, 0, 2));

        // timestamp is more recent and not within range
        Assertions.assertFalse(Tools.isTimestampRecent(2, 0, 1));

        // timestamp is not more recent and not within range
        Assertions.assertFalse(Tools.isTimestampRecent(0, 2, 1));

        // timestamp is not more recent but within range
        Assertions.assertFalse(Tools.isTimestampRecent(0, 1, 1));
        Assertions.assertFalse(Tools.isTimestampRecent(1, 2, 1));
        Assertions.assertFalse(Tools.isTimestampRecent(1, 2, 2));
        Assertions.assertFalse(Tools.isTimestampRecent(0, 2, 2));
    }
}
