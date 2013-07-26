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

import android.content.Context;

/**
 * Class for formatting longitude.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Longitude extends AbstractGeoCoordinate {
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
     * Segment East Unit
     */
    public static final String SEGMENT_EAST_UNIT = "E";

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
     * Segment West Unit
     */
    public static final String SEGMENT_WEST_UNIT = "W";

    /**
     * Constructor.
     */
    public Longitude() {
        super();
    }

    /**
     * Constructor.
     *
     * @param context App Context.
     */
    public Longitude(final Context context) {
        super(context);
    }

    /**
     * Initialize coordinate value range.
     */
    protected final void init() {
        // set coordinate value range
        rangeLow = SEGMENT_WEST_LOW;
        rangeHigh = SEGMENT_EAST_HIGH;
    }

    /**
     * Determine value segment, East if longitude is in the range 0..180,
     * West if longitude is in the range -180..0.
     *
     * @return segment code : SEGMENT_EAST or SEGMENT_WEST
     */
    public final int getSegment() {
        double coordinate = getValue();
        int retVal = 0;

        if (coordinate <= SEGMENT_EAST_HIGH && coordinate >= SEGMENT_EAST_LOW) {
            retVal = SEGMENT_EAST;
        }
        if (coordinate < SEGMENT_WEST_HIGH && coordinate >= SEGMENT_WEST_LOW) {
            retVal = SEGMENT_WEST;
        }

        return retVal;
    }

    /**
     * Get segment unit, E for SEGMENT_EAST, W for SEGMENT_WEST.
     *
     * @return unit
     */
    public final String getSegmentUnit() {
        String unit = null;

        switch (getSegment()) {
            case SEGMENT_EAST :
                // if context is defined, use android string
                if (mContext == null) {
                    unit = SEGMENT_EAST_UNIT;
                } else {
                    unit = mContext.getResources()
                            .getString(R.string.longitude_east_unit);
                }
                break;
            case SEGMENT_WEST :
                // if context is defined, use android string
                if (mContext == null) {
                    unit = SEGMENT_WEST_UNIT;
                } else {
                    unit = mContext.getResources()
                            .getString(R.string.longitude_west_unit);
                }
                break;
            default:
                break;
        }
        return unit;
    }

    /**
     * Convert coordinate value according to segment.
     *
     * @return converted coordinate value
     */
    protected final double getConvertedValue() {
        double coordinate = getValue();

        if (getSegment() == SEGMENT_WEST) {
            coordinate = Math.abs(coordinate);
        }

        return coordinate;
    }
}
