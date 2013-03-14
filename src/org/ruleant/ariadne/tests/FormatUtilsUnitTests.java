package org.ruleant.ariadne.tests;

import junit.framework.TestCase;
import org.ruleant.ariadne.FormatUtils;

public class FormatUtilsUnitTests extends TestCase {
    public void testFormatDist() {
        assertEquals("10m", FormatUtils.formatDist(9.9));
        assertEquals("10m", FormatUtils.formatDist(10.0));
        assertEquals("9,0km", FormatUtils.formatDist(9000.0));
        assertEquals("11km", FormatUtils.formatDist(11000.0));
    }
}
