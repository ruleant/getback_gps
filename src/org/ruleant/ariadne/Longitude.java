/**
 * Class for formatting longitude.
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

/**
 * Class for formatting longitude.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Longitude extends AbstractGeoCoordinate{
    /**
     * Segment East
     */
    public static final int SEGMENT_EAST = 1;

    /**
     * Segment East upper limit
     */
    public static final double SEGMENT_EAST_HIGH = 180;

    /**
     * Segment East lower limit
     */
    public static final double SEGMENT_EAST_LOW = 0;

    /**
     * Segment West
     */
    public static final int SEGMENT_WEST = 2;

    /**
     * Segment West upper limit
     */
    public static final double SEGMENT_WEST_HIGH = 0;

    /**
     * Segment West lower limit
     */
    public static final double SEGMENT_WEST_LOW = -180;

    /**
     * Constructor.
     *
     * @param newValue New value for unformatted value.
     */
    public Longitude(final double newValue) {
        super(newValue);
    }

    /**
     * Determine value segment, East if longitude is in the range 0..180,
     * West if longitude is in the range -180..0.
     *
     * @return segment code : SEGMENT_EAST or SEGMENT_WEST
     */
    public final int getSegment() {
        return 0;
    }

    /**
     * Get segment unit, E for SEGMENT_EAST, W for SEGMENT_WEST.
     *
     * @return unit
     */
    public final String getSegmentUnit() {
        return null;
    }
}
