/**
 * Unit tests for FormatUtils class
 *
 * Copyright (C) 2013 Dieter Adriaenssens
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

import java.util.Locale;
import junit.framework.TestCase;

/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class FormatUtilsTest extends TestCase {
    /**
     * Original Locale before tests.
     */
    private Locale originalLocale;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected void setUp() {
        // Set English (US) locale
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    protected void tearDown() {
        // set default locale back to original
        Locale.setDefault(originalLocale);
    }

    /**
     * Tests main functionality of method FormatDist.
     * Locale en_US is assumed, several distances are passed as an argument
     * to test the different cases : in meter, kilometer, kilometer with
     * an extra decimal, more than 1,000 km.
     */
    public final void testFormatDistMain() {
        assertEquals("9m", FormatUtils.formatDist(9.0));
        assertEquals("10m", FormatUtils.formatDist(10.0));
        assertEquals("9.0km", FormatUtils.formatDist(9000.0));
        assertEquals("9.9km", FormatUtils.formatDist(9900.0));
        assertEquals("11km", FormatUtils.formatDist(11000.0));
        assertEquals("12,345km", FormatUtils.formatDist(12345000.0));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    public final void testFormatDistBelgianFormat() {
        // Set Dutch (Belgium) locale
        Locale localeDutchBelgian = new Locale("nl", "BE");
        Locale.setDefault(localeDutchBelgian);

        assertEquals("9,0km", FormatUtils.formatDist(9000.0));
        assertEquals("9,9km", FormatUtils.formatDist(9900.0));
        assertEquals("12.345km", FormatUtils.formatDist(12345000.0));
    }

    /**
     * Test if the distance is correctly rounded.
     */
    public final void testFormatDistRoundUp() {
        assertEquals("10m", FormatUtils.formatDist(9.9));
    }

    /**
     * Tests if returned formatted distance is positive,
     * even if the distance argument is negative.
     */
    public final void testFormatDistNeg() {
        assertEquals("1m", FormatUtils.formatDist(-1.0));
        assertEquals("9.0km", FormatUtils.formatDist(-9000.0));
        assertEquals("11km", FormatUtils.formatDist(-11000.0));
    }

    /**
     * Tests conversion of the speed from m/s to km/h
     * and formatting of the speed :
     * 1 decimal when speed is smaller than 10km/h
     * no decimals when speed is bigger than 10 km/h
     * Locale en_US is assumed.
     */
    public final void testFormatSpeedMain() {
        // Set English (US) locale
        Locale.setDefault(Locale.US);

        assertEquals("3.6km/h", FormatUtils.formatDist(1.0));
        assertEquals("7.2km/h", FormatUtils.formatDist(2.0));
        assertEquals("9.9km/h", FormatUtils.formatDist(2.75));
        assertEquals("10km/h", FormatUtils.formatDist(2.78));
        assertEquals("14km/h", FormatUtils.formatDist(4.0));
        assertEquals("1,234km/h", FormatUtils.formatDist(342.7778));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    public final void testFormatSpeedBelgianFormat() {
        // Set Dutch (Belgium) locale
        Locale localeDutchBelgian = new Locale("nl", "BE");
        Locale.setDefault(localeDutchBelgian);

        assertEquals("9,9km/u", FormatUtils.formatDist(2.75));
        assertEquals("1.234km/u", FormatUtils.formatDist(342.7778));
    }

    /**
     * Test if the speed is correctly rounded.
     */
    public final void testFormatSpeedRound() {
        // 2,06389m/s = 7,43 km/h => 7.4 km/h
        assertEquals("7.4km/h", FormatUtils.formatDist(2.06389));
        // 2,0778m/s = 7,48 km/h => 7.5 km/h
        assertEquals("7.5km/h", FormatUtils.formatDist(2.0778));
        // 3.0m/s = 12.8 km/h => 13 km/h
        assertEquals("13km/h", FormatUtils.formatDist(3.0));
        // 4.0m/s = 14.4 km/h => 14 km/h
        assertEquals("14km/h", FormatUtils.formatDist(4.0));
    }

    /**
     * Tests if returned formatted speed is positive,
     * even if the speed argument is negative.
     */
    public final void testFormatSpeedNeg() {
        assertEquals("3.6km/h", FormatUtils.formatDist(-1.0));
        assertEquals("14km/h", FormatUtils.formatDist(-4.0));
    }
}
