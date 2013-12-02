/**
 * Class for formatting cardinal direction.
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
 * @package com.github.ruleant.getback_gps
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import android.content.Context;

import com.github.ruleant.getback_gps.R;

/**
 * Class for formatting cardinal direction.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class CardinalDirection extends AbstractGeoCoordinate {
    /**
     * Segment Northeast (NE).
     */
    public static final int SEGMENT_NORTHEAST = 1;

    /**
     * Segment Northeast upper limit.
     */
    public static final double SEGMENT_NE_HIGH = 90;

    /**
     * Segment Northeast lower limit.
     */
    public static final double SEGMENT_NE_LOW = 0;

    /**
     * Segment Northeast Unit.
     */
    public static final String SEGMENT_NE_UNIT = "NE";

    /**
     * Segment Southeast (SE).
     */
    public static final int SEGMENT_SOUTHEAST = 2;

    /**
     * Segment Southeast upper limit.
     */
    public static final double SEGMENT_SE_HIGH = 180;

    /**
     * Segment Southeast lower limit.
     */
    public static final double SEGMENT_SE_LOW = 90;

    /**
     * Segment Southeast Unit.
     */
    public static final String SEGMENT_SE_UNIT = "SE";

    /**
     * Segment Southwest (SE).
     */
    public static final int SEGMENT_SOUTHWEST = 3;

    /**
     * Segment Southwest upper limit.
     */
    public static final double SEGMENT_SW_HIGH = 270;

    /**
     * Segment Southwest lower limit.
     */
    public static final double SEGMENT_SW_LOW = 180;

    /**
     * Segment Southwest Unit.
     */
    public static final String SEGMENT_SW_UNIT = "SW";

    /**
     * Segment Northwest (NW).
     */
    public static final int SEGMENT_NORTHWEST = 4;

    /**
     * Segment Northwest upper limit.
     */
    public static final double SEGMENT_NW_HIGH = 360;

    /**
     * Segment Northwest lower limit.
     */
    public static final double SEGMENT_NW_LOW = 270;

    /**
     * Segment Northwest Unit.
     */
    public static final String SEGMENT_NW_UNIT = "NW";

    /**
     * Constructor.
     *
     * @param newValue New value for unformatted value.
     */
    public CardinalDirection(final double newValue) {
        super(newValue);
    }

    /**
     * Constructor.
     *
     * @param context App Context.
     * @param newValue New value for unformatted value.
     */
    public CardinalDirection(final Context context, final double newValue) {
        super(context, newValue);
    }

    /**
     * Initialize coordinate value range.
     */
    protected final void init() {
        // set coordinate value range
        setRange(SEGMENT_NE_LOW, SEGMENT_NW_HIGH);
    }

    /**
     * Determine segment :
     * Northeast (SEGMENT_NORTHEAST) if angle is in range 0..90,
     * Southeast (SEGMENT_SOUTHEAST) if angle is in range 90..180,
     * Southwest (SEGMENT_SOUTHWEST) if angle is in range 180..270,
     * Northwest (SEGMENT_NORTHWEST) if angle is in range 270..360.
     *
     * @return segment code.
     */
    public final int getSegment() {
        double coordinate = getValue();
        int retVal = 0;

        if (coordinate <= SEGMENT_NE_HIGH && coordinate >= SEGMENT_NE_LOW) {
            retVal = SEGMENT_NORTHEAST;
        }
        if (coordinate <= SEGMENT_SE_HIGH && coordinate > SEGMENT_SE_LOW) {
            retVal = SEGMENT_SOUTHEAST;
        }
        if (coordinate <= SEGMENT_SW_HIGH && coordinate > SEGMENT_SW_LOW) {
            retVal = SEGMENT_SOUTHWEST;
        }
        if (coordinate <= SEGMENT_NW_HIGH && coordinate > SEGMENT_NW_LOW) {
            retVal = SEGMENT_NORTHWEST;
        }

        return retVal;
    }

    /**
     * Get segment unit.
     *
     * @return unit
     */
    public final String getSegmentUnit() {
        String unit = null;
        Context context = getContext();

        switch (getSegment()) {
            case SEGMENT_NORTHEAST :
                // if context is defined, use android string
                if (context == null) {
                    unit = SEGMENT_NE_UNIT;
                } else {
                    unit = context.getResources()
                            .getString(R.string.northeast_unit);
                }
                break;
            case SEGMENT_SOUTHEAST :
                // if context is defined, use android string
                if (context == null) {
                    unit = SEGMENT_SE_UNIT;
                } else {
                    unit = context.getResources()
                            .getString(R.string.southeast_unit);
                }
                break;
            case SEGMENT_SOUTHWEST :
                // if context is defined, use android string
                if (context == null) {
                    unit = SEGMENT_SW_UNIT;
                } else {
                    unit = context.getResources()
                            .getString(R.string.southwest_unit);
                }
                break;
            case SEGMENT_NORTHWEST :
                // if context is defined, use android string
                if (context == null) {
                    unit = SEGMENT_NW_UNIT;
                } else {
                    unit = context.getResources()
                            .getString(R.string.northwest_unit);
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
        return getValue();
    }

    /**
     * Format value.
     *
     * @return formatted value
     */
    protected final String formatValue() {
        return FormatUtils.formatAngle(getConvertedValue(), 0);
    }
}
