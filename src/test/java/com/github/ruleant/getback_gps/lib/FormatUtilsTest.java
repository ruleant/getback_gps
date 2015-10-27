/**
 * Unit tests for FormatUtils class
 *
 * Copyright (C) 2013-2015 Dieter Adriaenssens
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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.Charset;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit tests for FormatUtils class.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@RunWith(RobolectricTestRunner.class)
public class FormatUtilsTest {
    /**
     * Expected Exception.
     */
    @Rule public final ExpectedException thrown = ExpectedException.none();

    /**
     * Original Locale before tests.
     */
    private Locale originalLocale;

    /**
     * 9 meter.
     */
    private static final double M_9M = 9.0;

    /**
     * 9.9 meter.
     */
    private static final double M_9P9M = 9.9;

    /**
     * 10 meter.
     */
    private static final double M_10M = 10.0;

    /**
     * 999.3 meter.
     */
    private static final double M_999P3M = 999.3;

    /**
     * 999.9 meter.
     */
    private static final double M_999P9M = 999.9;

    /**
     * 1.33 kilometer in meter.
     */
    private static final double M_1P33KM = 1330.0;

    /**
     * 1.37 kilometer in meter.
     */
    private static final double M_1P37KM = 1370.0;

    /**
     * 9 kilometer in meter.
     */
    private static final double M_9KM = 9000.0;

    /**
     * 9.93 kilometer in meter.
     */
    private static final double M_9P93KM = 9930.0;

    /**
     * 9.98 kilometer in meter.
     */
    private static final double M_9P98KM = 9980.0;

    /**
     * 9.9 kilometer in meter.
     */
    private static final double M_9P9KM = 9900.0;

    /**
     * 11 kilometer in meter.
     */
    private static final double M_11KM = 11000.0;

    /**
     * 11.4 kilometer in meter.
     */
    private static final double M_11P4KM = 11400.0;

    /**
     * 11.7 kilometer in meter.
     */
    private static final double M_11P7KM = 11700.0;

    /**
     * 12345 kilometer in meter.
     */
    private static final double M_12345KM = 12345000.0;

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
     * 0° angle.
     */
    private static final double A_0 = 0.0;

    /**
     * 45° angle.
     */
    private static final double A_45 = 45.0;

    /**
     * 45.674° angle.
     */
    private static final double A_45P674 = 45.674;

    /**
     * 45.678° angle.
     */
    private static final double A_45P678 = 45.678;

    /**
     * 180° angle.
     */
    private static final double A_180 = 180.0;

    /**
     * 225° angle.
     */
    private static final double A_225 = 225.0;

    /**
     * -315° angle (= 45°).
     */
    private static final double A_M315 = -315.0;

    /**
     * -360° angle (= 0°).
     */
    private static final double A_M360 = -360.0;

    /**
     * -675° angle (= 45°).
     */
    private static final double A_M675 = -675.0;

    /**
     * -720° angle (= 0°).
     */
    private static final double A_M720 = -720.0;

    /**
     * 360° angle (= 0°).
     */
    private static final double A_360 = 360.0;

    /**
     * 405° angle (= 45°).
     */
    private static final double A_405 = 405.0;

    /**
     * 720° angle (= 0°).
     */
    private static final double A_720 = 720.0;

    /**
     * 765° angle (= 45°).
     */
    private static final double A_765 = 765.0;

    /**
     * Precision 0 decimals.
     */
    private static final int PRECISION_0 = 0;

    /**
     * Precision 1 decimal.
     */
    private static final int PRECISION_1 = 1;

    /**
     * Precision 2 decimals.
     */
    private static final int PRECISION_2 = 2;

    /**
     * Precision 3 decimals.
     */
    private static final int PRECISION_3 = 3;

    /**
     * Precision 5 decimals.
     */
    private static final int PRECISION_5 = 5;

    /**
     * Precision 10 decimals.
     */
    private static final int PRECISION_10 = 10;

    /**
     * Angle accuracy.
     */
    private static final double ANGLE_ACCURACY = 0.001;

    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    @Before
    public final void setUp() {
        // Set English (US) locale
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    @After
    public final void tearDown() {
        // set default locale back to original
        Locale.setDefault(originalLocale);
    }

    /**
     * Tests default charset.
     * Default charset should be UTF-8.
     */
    @Test
    public final void testDefaultCharset() {
        assertEquals("UTF-8", Charset.defaultCharset().name());
    }

    /**
     * Tests main functionality of method FormatDist.
     * Locale en_US is assumed, several distances are passed as an argument
     * to test the different cases : in meter, kilometer, kilometer with
     * an extra decimal, more than 1,000 km.
     */
    @Test
    public final void testFormatDistMain() {
        assertEquals("9m", FormatUtils.formatDist(M_9M));
        assertEquals("10m", FormatUtils.formatDist(M_10M));
        assertEquals("9.0km", FormatUtils.formatDist(M_9KM));
        assertEquals("9.9km", FormatUtils.formatDist(M_9P9KM));
        assertEquals("11km", FormatUtils.formatDist(M_11KM));
        assertEquals("12,345km", FormatUtils.formatDist(M_12345KM));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    @Test
    public final void testFormatDistBelgianFormat() {
        // Set Dutch (Belgium) locale
        Locale localeDutchBelgian = new Locale("nl", "BE");
        Locale.setDefault(localeDutchBelgian);

        assertEquals("9,0km", FormatUtils.formatDist(M_9KM));
        assertEquals("9,9km", FormatUtils.formatDist(M_9P9KM));
        assertEquals("12.345km", FormatUtils.formatDist(M_12345KM));
    }

    /**
     * Test if the distance is correctly rounded.
     */
    @Test
    public final void testFormatDistRoundUp() {
        assertEquals("10m", FormatUtils.formatDist(M_9P9M));
        assertEquals("999m", FormatUtils.formatDist(M_999P3M));
        assertEquals("1.0km", FormatUtils.formatDist(M_999P9M));
        assertEquals("1.3km", FormatUtils.formatDist(M_1P33KM));
        assertEquals("1.4km", FormatUtils.formatDist(M_1P37KM));
        assertEquals("9.9km", FormatUtils.formatDist(M_9P93KM));
        assertEquals("10km", FormatUtils.formatDist(M_9P98KM));
        assertEquals("11km", FormatUtils.formatDist(M_11P4KM));
        assertEquals("12km", FormatUtils.formatDist(M_11P7KM));
    }

    /**
     * Tests if returned formatted distance is positive,
     * even if the distance argument is negative.
     */
    @Test
    public final void testFormatDistNeg() {
        assertEquals("1m", FormatUtils.formatDist(-1.0));
        assertEquals("9.0km", FormatUtils.formatDist(-1.0 * M_9KM));
        assertEquals("11km", FormatUtils.formatDist(-1.0 * M_11KM));
    }

    /**
     * Tests main functionality of method FormatHeight.
     * Locale en_US is assumed, several distances are passed as an argument
     * to test the different cases : in meter, more than 1,000 m.
     */
    @Test
    public final void testFormatHeightMain() {
        assertEquals("9m", FormatUtils.formatHeight(M_9M));
        assertEquals("10m", FormatUtils.formatHeight(M_10M));
        assertEquals("9,000m", FormatUtils.formatHeight(M_9KM));
        assertEquals("9,900m", FormatUtils.formatHeight(M_9P9KM));
        assertEquals("11,000m", FormatUtils.formatHeight(M_11KM));
        assertEquals("12,345,000m", FormatUtils.formatHeight(M_12345KM));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    @Test
    public final void testFormatHeightBelgianFormat() {
        // Set Dutch (Belgium) locale
        Locale localeDutchBelgian = new Locale("nl", "BE");
        Locale.setDefault(localeDutchBelgian);

        assertEquals("9.000m", FormatUtils.formatHeight(M_9KM));
        assertEquals("9.900m", FormatUtils.formatHeight(M_9P9KM));
        assertEquals("12.345.000m", FormatUtils.formatHeight(M_12345KM));
    }

    /**
     * Test if the height is correctly rounded.
     */
    @Test
    public final void testFormatHeightRoundUp() {
        assertEquals("10m", FormatUtils.formatHeight(M_9P9M));
        assertEquals("999m", FormatUtils.formatHeight(M_999P3M));
        assertEquals("1,000m", FormatUtils.formatHeight(M_999P9M));
        assertEquals("1,330m", FormatUtils.formatHeight(M_1P33KM));
        assertEquals("1,370m", FormatUtils.formatHeight(M_1P37KM));
        assertEquals("9,930m", FormatUtils.formatHeight(M_9P93KM));
        assertEquals("9,980m", FormatUtils.formatHeight(M_9P98KM));
        assertEquals("11,400m", FormatUtils.formatHeight(M_11P4KM));
        assertEquals("11,700m", FormatUtils.formatHeight(M_11P7KM));
    }

    /**
     * Tests if returned formatted height is negative,
     * if the distance argument is negative.
     */
    @Test
    public final void testFormatHeightNeg() {
        assertEquals("-1m", FormatUtils.formatHeight(-1.0));
        assertEquals("-9,000m", FormatUtils.formatHeight(-1.0 * M_9KM));
        assertEquals("-11,000m", FormatUtils.formatHeight(-1.0 * M_11KM));
    }

    /**
     * Tests conversion of the speed from m/s to km/h
     * and formatting of the speed :
     * 1 decimal when speed is smaller than 10km/h
     * no decimals when speed is bigger than 10 km/h
     * Locale en_US is assumed.
     */
    @Test
    public final void testFormatSpeedMain() {
        assertEquals(
                "3.6" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_3P6KPH));
        assertEquals(
                "7.2" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_7P2KPH));
        assertEquals(
                "9.9" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_9P9KPH));
        assertEquals(
                "10" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_10KPH));
        assertEquals(
                "14" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_14P4KPH));
        assertEquals(
                "1,234" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_1234KPH));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    // FIXME speed unit is not localized yet
    @Ignore("speed unit is not localized yet")
    @Test
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
    @Test
    public final void testFormatSpeedRound() {
        // 2.06389m/s = 7.43 km/h => 7.4 km/h
        assertEquals(
                "7.4" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_7P43KPH));
        // 2.0778m/s = 7.48 km/h => 7.5 km/h
        assertEquals(
                "7.5" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_7P48KPH));
        // 3.0m/s = 10.8 km/h => 11 km/h
        assertEquals(
                "11" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_10P8KPH));
        // 4.0m/s = 14.4 km/h => 14 km/h
        assertEquals(
                "14" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(MPS_14P4KPH));
    }

    /**
     * Tests if returned formatted speed is positive,
     * even if the speed argument is negative.
     */
    @Test
    public final void testFormatSpeedNeg() {
        assertEquals(
                "3.6" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(-1.0 * MPS_3P6KPH));
        assertEquals(
                "14" + FormatUtils.SPEED_KPH,
                FormatUtils.formatSpeed(-1.0 * MPS_14P4KPH));
    }

    /**
     * Tests main functionality of method FormatAngle.
     * Locale en_US is assumed, several angels are passed as an argument.
     */
    @Test
    public final void testFormatAngle() {
        assertEquals("45.00°", FormatUtils.formatAngle(A_45, 2));
        assertEquals("45.67°", FormatUtils.formatAngle(A_45P674, 2));
        assertEquals("45.68°", FormatUtils.formatAngle(A_45P678, 2));
    }

    /**
     * Tests range of precision parameter of method FormatAngle.
     */
    @Test
    public final void testFormatAngleWrongPrecision() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Precision can't be a negative value");

        FormatUtils.formatAngle(A_45, -1);
        fail("Expected an IllegalArgumentException to be thrown");
    }

    /**
     * Tests precision parameter of method FormatAngle.
     */
    @Test
    public final void testFormatAnglePrecision() {
        assertEquals("45°", FormatUtils.formatAngle(A_45, PRECISION_0));
        assertEquals("46°", FormatUtils.formatAngle(A_45P674, PRECISION_0));
        assertEquals("45.7°", FormatUtils.formatAngle(A_45P674, PRECISION_1));
        assertEquals("45.67°", FormatUtils.formatAngle(A_45P674, PRECISION_2));
        assertEquals("45.674°",
                FormatUtils.formatAngle(A_45P674, PRECISION_3));
        assertEquals("45.678°",
                FormatUtils.formatAngle(A_45P678, PRECISION_3));

        assertEquals("3.14159°",
                FormatUtils.formatAngle(Math.PI, PRECISION_5));
        assertEquals("3.1415926536°",
                FormatUtils.formatAngle(Math.PI, PRECISION_10));
    }

    /**
     * Tests the formatting when a European locale is used, in this case nl_BE.
     */
    @Test
    public final void testFormatAngleBelgianFormat() {
        // Set Dutch (Belgium) locale
        Locale localeDutchBelgian = new Locale("nl", "BE");
        Locale.setDefault(localeDutchBelgian);

        assertEquals("45,00°", FormatUtils.formatAngle(A_45, PRECISION_2));
        assertEquals("45,67°", FormatUtils.formatAngle(A_45P674, PRECISION_2));
        assertEquals("45,68°", FormatUtils.formatAngle(A_45P678, PRECISION_2));
    }

    /**
     * Tests if returned formatted angle is positive and normalized,
     * even if the angle argument is negative.
     */
    @Test
    public final void testFormatAngleNeg() {
        assertEquals("-45.00°",
                FormatUtils.formatAngle(-1.0 * A_45, PRECISION_2));
        assertEquals("-45.67°",
                FormatUtils.formatAngle(-1.0 * A_45P674, PRECISION_2));
        assertEquals("-45.68°",
                FormatUtils.formatAngle(-1.0 * A_45P678, PRECISION_2));
    }

    /**
     * Tests if returned normalized angle is unaffected if
     * it is in the 0°-360° range.
     */
    @Test
    public final void testNormalizeAngle() {
        assertEquals(A_0, FormatUtils.normalizeAngle(A_0), ANGLE_ACCURACY);
        assertEquals(A_45, FormatUtils.normalizeAngle(A_45), ANGLE_ACCURACY);
    }

    /**
     * Tests if returned normalized angle is converted correctly
     * to the 0°-360° range, if the angle is negative.
     */
    @Test
    public final void testNormalizeAngleNeg() {
        assertEquals(A_0, FormatUtils.normalizeAngle(A_M360), ANGLE_ACCURACY);
        assertEquals(A_0, FormatUtils.normalizeAngle(A_M720), ANGLE_ACCURACY);
        assertEquals(A_45, FormatUtils.normalizeAngle(A_M315), ANGLE_ACCURACY);
        assertEquals(A_45, FormatUtils.normalizeAngle(A_M675), ANGLE_ACCURACY);
    }

    /**
     * Tests if returned normalized angle is converted correctly
     * to the 0°-360° range, if the angle is bigger than 360°.
     */
    @Test
    public final void testNormalizeAngleBig() {
        assertEquals(A_0, FormatUtils.normalizeAngle(A_360), ANGLE_ACCURACY);
        assertEquals(A_0, FormatUtils.normalizeAngle(A_720), ANGLE_ACCURACY);
        assertEquals(A_45, FormatUtils.normalizeAngle(A_405), ANGLE_ACCURACY);
        assertEquals(A_45, FormatUtils.normalizeAngle(A_765), ANGLE_ACCURACY);
    }

    /**
     * Tests inverseAngle.
     */
    @Test
    public final void testInverseAngle() {
        assertEquals(A_180, FormatUtils.inverseAngle(A_0), ANGLE_ACCURACY);
        assertEquals(A_0, FormatUtils.inverseAngle(A_180), ANGLE_ACCURACY);
        assertEquals(A_225, FormatUtils.inverseAngle(A_45), ANGLE_ACCURACY);
        assertEquals(A_45, FormatUtils.inverseAngle(A_225), ANGLE_ACCURACY);
    }
}
