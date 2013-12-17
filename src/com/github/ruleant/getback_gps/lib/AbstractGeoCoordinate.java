/**
 * Abstract class for formatting a geological coordinate.
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

/**
 * Abstract class for formatting a geological coordinate.
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public abstract class AbstractGeoCoordinate {
    /**
     * Unformatted coordinate value.
     */
    private double value;

    /**
     * Current context.
     */
    private Context mContext = null;

    /**
     * Lower value of allowed value range.
     */
    private double mRangeLow;

    /**
     * Higher value of allowed value range.
     */
    private double mRangeHigh;

    /**
     * Constructor.
     *
     * @param newValue New value for unformatted value.
     */
    public AbstractGeoCoordinate(final double newValue) {
        init();
        setValue(newValue);
    }

    /**
     * Constructor.
     *
     * @param context App Context.
     * @param newValue New value for unformatted value.
     */
    public AbstractGeoCoordinate(final Context context, final double newValue) {
        setContext(context);
        init();
        setValue(newValue);
    }

    /**
     * Initialize coordinate value range.
     */
    protected abstract void init();

    /**
     * Set coordinate value range.
     *
     * @param rangeLow Lower limit of allowed range
     * @param rangeHigh Higher limit of allowed range
     */
    protected final void setRange(
            final double rangeLow,
            final double rangeHigh) {
        mRangeLow = rangeLow;
        mRangeHigh = rangeHigh;
    }

    /**
     * Set unformatted value.
     *
     * @param newValue New value for unformatted value.
     * @throws IllegalArgumentException if new value is out of range.
     */
    public final void setValue(final double newValue) {
        if (checkRange(newValue)) {
            value = newValue;
        } else {
            throw new IllegalArgumentException(
                    "newValue is not in range "
                            + mRangeLow + " .. " + mRangeHigh);
        }
    }

    /**
     * Get unformatted value.
     *
     * @return Unformatted value.
     */
    public final double getValue() {
        return value;
    }

    /**
     * Set current context.
     *
     * @param context Current context
     */
    public final void setContext(final Context context) {
        if (context != null) {
            mContext = context;
        }
    }

    /**
     * Get current context.
     *
     * @return Current context
     */
    protected final Context getContext() {
        return mContext;
    }

    /**
     * Check if submitted value is within the allowed range.
     *
     * @param coordinate coordinate value
     * @return true if coordinate is within range
     */
    private boolean checkRange(final double coordinate) {
        return coordinate <= mRangeHigh && coordinate >= mRangeLow;
    }

    /**
     * Convert coordinate value according to segment.
     *
     * @return converted coordinate value
     */
    protected abstract double getConvertedValue();

    /**
     * Format an unformatted angle to a GeoCoordinate.
     *
     * @return String formatted string
     */
    public final String format() {
        return formatValue() + " " + getSegmentUnit();
    }

    /**
     * Format value.
     *
     * @return formatted value
     */
    protected abstract String formatValue();

    /**
     * Determine value segment.
     *
     * @return segment code
     */
    public abstract int getSegment();

    /**
     * Get segment unit.
     *
     * @return unit
     */
    public abstract String getSegmentUnit();
}
