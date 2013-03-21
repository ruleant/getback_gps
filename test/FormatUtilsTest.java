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
     * Number nine in double format.
     */
    private static final double D_9 = 9.0;

    /**
     * Number 9.9 in double format.
     */
    private static final double D_9P9 = 9.9;

    /**
     * Number ten in double format.
     */
    private static final double D_10 = 10.0;

    /**
     * Number 9000 in double format.
     */
    private static final double D_9K = 9000.0;

    /**
     * Number 9900 in double format.
     */
    private static final double D_9P9K = 9900.0;

    /**
     * Number 11000 in double format.
     */
    private static final double D_11K = 11000.0;

    /**
     * Number 12345000 in double format.
     */
    private static final double D_12345K = 12345000.0;

    /**
     * 3.6 km/h in m/s.
     */
    private static final double MPS_3P6KPH = 1.0;

    /**
     * 7.2 km/h in m/s.
     */
    private static final double MPS_7P2KPH = 2.0;

    /**
     * 7.43 km/h in m/s.
     */
    private static final double MPS_7P43KPH = 2.06389;

    /**
     * 7.48 km/h in m/s.
     */
    private static final double MPS_7P48KPH = 2.0778;

    /**
     * 9.9 km/h in m/s.
     */
    private static final double MPS_9P9KPH = 2.75;

    /**
     * 10.0 km/h in m/s.
     */
    private static final double MPS_10KPH = 2.78;

    /**
     * 10.8 km/h in m/s.
     */
    private static final double MPS_10P8KPH = 3.0;

    /**
     * 14.4 km/h in m/s.
     */
    private static final double MPS_14P4KPH = 4.0;

    /**
     * 1234 km/h in m/s.
     */
    private static final double MPS_1234KPH = 342.7778;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected final void setUp() {
        // Set English (US) locale
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    protected final void tearDown() {
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
        assertEquals("9m", FormatUtils.formatDist(D_9));
        assertEquals("10m", FormatUtils.formatDist(D_10));
        assertEquals("9.0km", FormatUtils.formatDist(D_9K));
        assertEquals("9.9km", FormatUtils.formatDist(D_9P9K));
        assertEquals("11km", FormatUtils.formatDist(D_11K));
        assertEquals("12,345km", FormatUtils.formatDist(D_12345K));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    public final void testFormatDistBelgianFormat() {
        // Set Dutch (Belgium) locale
        Locale localeDutchBelgian = new Locale("nl", "BE");
        Locale.setDefault(localeDutchBelgian);

        assertEquals("9,0km", FormatUtils.formatDist(D_9K));
        assertEquals("9,9km", FormatUtils.formatDist(D_9P9K));
        assertEquals("12.345km", FormatUtils.formatDist(D_12345K));
    }

    /**
     * Test if the distance is correctly rounded.
     */
    public final void testFormatDistRoundUp() {
        assertEquals("10m", FormatUtils.formatDist(D_9P9));
    }

    /**
     * Tests if returned formatted distance is positive,
     * even if the distance argument is negative.
     */
    public final void testFormatDistNeg() {
        assertEquals("1m", FormatUtils.formatDist(-1.0));
        assertEquals("9.0km", FormatUtils.formatDist(-1.0 * D_9K));
        assertEquals("11km", FormatUtils.formatDist(-1.0 * D_11K));
    }

    /**
     * Tests conversion of the speed from m/s to km/h
     * and formatting of the speed :
     * 1 decimal when speed is smaller than 10km/h
     * no decimals when speed is bigger than 10 km/h
     * Locale en_US is assumed.
     */
    public final void testFormatSpeedMain() {
        assertEquals("3.6km/h", FormatUtils.formatSpeed(MPS_3P6KPH));
        assertEquals("7.2km/h", FormatUtils.formatSpeed(MPS_7P2KPH));
        assertEquals("9.9km/h", FormatUtils.formatSpeed(MPS_9P9KPH));
        assertEquals("10km/h", FormatUtils.formatSpeed(MPS_10KPH));
        assertEquals("14km/h", FormatUtils.formatSpeed(MPS_14P4KPH));
        assertEquals("1,234km/h", FormatUtils.formatSpeed(MPS_1234KPH));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    public final void testFormatSpeedBelgianFormat() {
        // Set Dutch (Belgium) locale
        Locale localeDutchBelgian = new Locale("nl", "BE");
        Locale.setDefault(localeDutchBelgian);

        assertEquals("9,9km/u", FormatUtils.formatSpeed(MPS_9P9KPH));
        assertEquals("1.234km/u", FormatUtils.formatSpeed(MPS_1234KPH));
    }

    /**
     * Test if the speed is correctly rounded.
     */
    public final void testFormatSpeedRound() {
        // 2.06389m/s = 7.43 km/h => 7.4 km/h
        assertEquals("7.4km/h", FormatUtils.formatSpeed(MPS_7P43KPH));
        // 2.0778m/s = 7.48 km/h => 7.5 km/h
        assertEquals("7.5km/h", FormatUtils.formatSpeed(MPS_7P48KPH));
        // 3.0m/s = 10.8 km/h => 11 km/h
        assertEquals("11km/h", FormatUtils.formatSpeed(MPS_10P8KPH));
        // 4.0m/s = 14.4 km/h => 14 km/h
        assertEquals("14km/h", FormatUtils.formatSpeed(MPS_14P4KPH));
    }

    /**
     * Tests if returned formatted speed is positive,
     * even if the speed argument is negative.
     */
    public final void testFormatSpeedNeg() {
        assertEquals("3.6km/h", FormatUtils.formatSpeed(-1.0 * MPS_3P6KPH));
        assertEquals("14km/h", FormatUtils.formatSpeed(-1.0 * MPS_14P4KPH));
    }
}
