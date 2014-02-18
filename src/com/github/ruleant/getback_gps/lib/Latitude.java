/**
 * Class for formatting latitude.
 *
 * Copyright (C) 2012-2014 Dieter Adriaenssens
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
 * @package com.github.ruleant.getback_gps.lib
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import android.content.Context;
import android.location.Location;

import com.github.ruleant.getback_gps.R;

/**
 * Class for formatting latitude.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Latitude extends AbstractGeoCoordinate {
    /**
     * Segment North.
     */
    public static final int SEGMENT_NORTH = 1;

    /**
     * Segment North upper limit.
     */
    public static final double SEGMENT_NORTH_HIGH = 90;

    /**
     * Segment North lower limit.
     */
    public static final double SEGMENT_NORTH_LOW = 0;

    /**
     * Segment North Unit.
     */
    public static final String SEGMENT_NORTH_UNIT = "N";

    /**
     * Segment South.
     */
    public static final int SEGMENT_SOUTH = 2;

    /**
     * Segment South upper limit.
     */
    public static final double SEGMENT_SOUTH_HIGH = 0;

    /**
     * Segment South lower limit.
     */
    public static final double SEGMENT_SOUTH_LOW = -90;

    /**
     * Segment South Unit.
     */
    public static final String SEGMENT_SOUTH_UNIT = "S";

    /**
     * Constructor.
     *
     * @param newValue New value for unformatted value.
     */
    public Latitude(final double newValue) {
        super(newValue);
    }

    /**
     * Constructor.
     *
     * @param context App Context.
     * @param newValue New value for unformatted value.
     */
    public Latitude(final Context context, final double newValue) {
        super(context, newValue);
    }

    /**
     * Initialize coordinate value range.
     */
    protected final void init() {
        // set coordinate value range
        setRange(SEGMENT_SOUTH_LOW, SEGMENT_NORTH_HIGH);
    }

    /**
     * Determine value segment, North if latitude is in the range 0..90,
     * South if latitude is in the range -90..0.
     *
     * @return segment code : SEGMENT_NORTH or SEGMENT_SOUTH
     */
    public final int getSegment() {
        double coordinate = getValue();
        int retVal = 0;

        if (coordinate <= SEGMENT_NORTH_HIGH
                && coordinate >= SEGMENT_NORTH_LOW) {
            retVal = SEGMENT_NORTH;
        }
        if (coordinate < SEGMENT_SOUTH_HIGH
                && coordinate >= SEGMENT_SOUTH_LOW) {
            retVal = SEGMENT_SOUTH;
        }

        return retVal;
    }

    /**
     * Get segment unit, N for SEGMENT_NORTH, S for SEGMENT_SOUTH.
     *
     * @return unit
     */
    public final String getSegmentUnit() {
        String unit = null;
        Context context = getContext();

        switch (getSegment()) {
            case SEGMENT_NORTH :
                // if context is defined, use android string
                if (context == null) {
                    unit = SEGMENT_NORTH_UNIT;
                } else {
                    unit = context.getResources()
                            .getString(R.string.latitude_north_unit);
                }
                break;
            case SEGMENT_SOUTH :
                // if context is defined, use android string
                if (context == null) {
                    unit = SEGMENT_SOUTH_UNIT;
                } else {
                    unit = context.getResources()
                            .getString(R.string.latitude_south_unit);
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

        if (getSegment() == SEGMENT_SOUTH) {
            coordinate = Math.abs(coordinate);
        }

        return coordinate;
    }

    /**
     * Format value.
     *
     * @return formatted value
     */
    protected final String formatValue() {
        return Location.convert(getConvertedValue(), Location.FORMAT_SECONDS)
                .replaceFirst(":", "° ").replace(":", "' ") + "\"";
    }
}
