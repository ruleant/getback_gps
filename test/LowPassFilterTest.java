/**
 * Unit tests for LowPassFilter class
 *
 * Copyright (C) 2014 Dieter Adriaenssens
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

import com.github.ruleant.getback_gps.lib.LowPassFilter;

import junit.framework.TestCase;

/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class LowPassFilterTest extends TestCase {
    /**
     * Value for alpha parameter.
     */
    private static final float ALPHA_VALUE = 0.6f;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
    }

    /**
     * Tests range of alpha parameter.
     */
    public final void testAlphaParameterRange() {
        // valid range for parameter alpha
        assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 0));
        assertEquals(0.0f, LowPassFilter.filterValue(0, 0, ALPHA_VALUE));
        assertEquals(0.0f, LowPassFilter.filterValue(0, 0, 1));

        // invalid range for parameter alpha
        try {
            LowPassFilter.filterValue(0, 0, -1);
            fail("should have thrown an exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "parameter alpha is not in range 0.0 .. 1.0",
                    e.getMessage());
        }

        try {
            LowPassFilter.filterValue(0, 0, -1 * 2);
            fail("should have thrown an exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "parameter alpha is not in range 0.0 .. 1.0",
                    e.getMessage());
        }

        try {
            LowPassFilter.filterValue(0, 0, 2);
            fail("should have thrown an exception.");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "parameter alpha is not in range 0.0 .. 1.0",
                    e.getMessage());
        }
    }
}
