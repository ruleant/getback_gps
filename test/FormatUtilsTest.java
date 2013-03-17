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
     * Tests main functionality of method FormatDist.
     * Locale en_US is assumed, several distances are passed as an argument
     * to test the different cases : in meter, kilometer, kilometer with
     * an extra decimal, more than 1,000 km.
     */
    public void testFormatDist() {
        // Set English (US) locale
        Locale.setDefault(Locale.US);

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
    public void testFormatDistBelgianFormat() {
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
    public void testFormatDistRoundUp() {
        assertEquals("10m", FormatUtils.formatDist(9.9));
    }

    /**
     * Tests if returned formatted distance is positive,
     * even if the distance argument is negative.
     */
    public void testFormatDistNeg() {
        assertEquals("1m", FormatUtils.formatDist(-1.0));
    }
}
