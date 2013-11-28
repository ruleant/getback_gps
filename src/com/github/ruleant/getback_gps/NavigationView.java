/**
 * Navigation View
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
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ruleant.getback_gps.lib.FormatUtils;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Navigation view is used to indicate the direction to the destination.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class NavigationView extends ImageView {
    /**
     * Paint used for drawing.
     */
    private Paint mPaint = new Paint();

    /**
     * Direction to destination.
     */
    private double mDirection = 0;

    /**
     * Navigation mode.
     */
    private int mMode = 0;

    /**
     * Attribute layout_width.
     */
    private int mAttributeLayoutWidth = ViewGroup.LayoutParams.MATCH_PARENT;

    /**
     * Attribute layout_height.
     */
    private int mAttributeLayoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * X coordinate.
     */
    public static final int X = 0;

    /**
     * Y coordinate.
     */
    public static final int Y = 1;

    /**
     * Mode disabled.
     */
    public static final int DISABLED = 0;

    /**
     * Mode inaccurate.
     */
    public static final int INACCURATE = 1;

    /**
     * Mode accurate.
     */
    public static final int ACCURATE = 2;

    /**
     * Line thickness.
     */
    private static final float LINE_THICKNESS = 4;

    /**
     * 20 %.
     */
    private static final double D_20PCT = 0.2;

    /**
     * 80 %.
     */
    private static final double D_80PCT = 0.8;

    /**
     * arrow side angle.
     */
    private static final double ARROW_ANGLE = 25.0;

    /**
     * Constructor.
     *
     * @param context App context
     */
    public NavigationView(final Context context) {
        super(context);

        init();
    }

    /**
     * Constructor.
     *
     * @param context App context
     * @param attributes View Attributes
     */
    public NavigationView(final Context context,
                          final AttributeSet attributes) {
        super(context, attributes);

        int[] lookForAttributes = new int[] {android.R.attr.layout_width,
                android.R.attr.layout_height};

        TypedArray foundAttributes = context.getTheme().obtainStyledAttributes(
                attributes,
                lookForAttributes,
                0, 0);

        try {
            mAttributeLayoutWidth = foundAttributes.getInteger(
                    0, ViewGroup.LayoutParams.MATCH_PARENT);
            mAttributeLayoutHeight = foundAttributes.getInteger(
                    1, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            foundAttributes.recycle();
        }

        init();
    }

    /**
     * Sets Direction.
     *
     * @param direction Direction to destination (0-360°).
     */
    public final void setDirection(final double direction) {
        this.mDirection =  FormatUtils.normalizeAngle(direction);
    }

    /**
     * Get direction, return nothing if mode is DISABLED.
     *
     * @return Direction to destination (0-360°)
     */
    public final double getDirection() {
        if (getMode() == DISABLED) {
            return 0;
        } else {
            return mDirection;
        }
    }

    /**
     * Sets navigation mode.
     *
     * @param mode Navigation mode : DISABLED, INACCURATE, ACCURATE
     */
    public final void setMode(final int mode) {
        switch (mode) {
            default:
            case DISABLED:
                this.mMode = DISABLED;
                mPaint.setColor(Color.LTGRAY);
                break;
            case INACCURATE:
                this.mMode = INACCURATE;
                mPaint.setColor(Style.holoBlueLight);
                break;
            case ACCURATE:
                this.mMode = ACCURATE;
                mPaint.setColor(Style.holoGreenLight);
                break;
        }
    }

    /**
     * Get navigation mode.
     *
     * @return Navigation mode : DISABLED, INACCURATE, ACCURATE
     */
    public final int getMode() {
        return mMode;
    }

    @Override
    public final void onDraw(final Canvas canvas) {
        // scale View if it is not square
        if (getWidth() != getHeight()) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();

            // adjust height/width according to attribute setting
            if (mAttributeLayoutHeight
                    == ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = getWidth();
            } else if (mAttributeLayoutWidth
                    == ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.width = getHeight();
            }

            setLayoutParams(layoutParams);
        }

        // draw arrow to destination
        double radius = getHeight() / 2;
        double arrowLength = radius * D_80PCT;
        double arrowLengthTail = radius * D_20PCT;
        double directionArrowPoint = getDirection();
        double directionArrowTail
                = FormatUtils.inverseAngle(directionArrowPoint);

        long[] arrowPointCoordinate
                = polarToCartesian(directionArrowPoint, arrowLength);
        long[] tailRightCoordinate = polarToCartesian(
                directionArrowTail - ARROW_ANGLE, arrowLengthTail);
        long[] tailLeftCoordinate = polarToCartesian(
                directionArrowTail + ARROW_ANGLE, arrowLengthTail);

        canvas.drawLine(tailRightCoordinate[X], tailRightCoordinate[Y],
                arrowPointCoordinate[X], arrowPointCoordinate[Y], mPaint);
        canvas.drawLine(tailLeftCoordinate[X], tailLeftCoordinate[Y],
                arrowPointCoordinate[X], arrowPointCoordinate[Y], mPaint);
        canvas.drawLine(tailLeftCoordinate[X], tailLeftCoordinate[Y],
                tailRightCoordinate[X], tailRightCoordinate[Y], mPaint);
    }

    /**
     * Initialise NavigationView.
     */
    private void init() {
        // set Background
        setBackgroundResource(R.drawable.custom_grid);

        // initialise paint
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(LINE_THICKNESS);
    }

    /**
     * Convert polar coordinate to Cartesian coordinate
     * and apply the necessary transformations.
     *
     * The angle transformations :
     * - to clockwise : a => -a
     * - rotate 90° counter-clockwise : -a => PI/2 - a
     * X = cos(PI/2 - a) = sin(a)
     * Y = sin(PI/2 - a) = cos(a)
     * - flip Y coordinate
     * Y = -cos(a)
     *
     * @param angleDegrees Angle to coordinate point (in degrees)
     * @param distance distance to coordinate point
     * @return Cartesian coordinate 0 = x, 1 = y
     */
    private long[] polarToCartesian(final double angleDegrees,
                                    final double distance) {
        long[] coordinate = new long[2];

        // get center of ImageView
        long centerX = getWidth() / 2;
        long centerY = getHeight() / 2;

        // convert angle in degrees to Radians
        double angleRadian = Math.toRadians(angleDegrees);

        // Transform angle and convert to Cartesian
        coordinate[X] = centerX + Math.round(Math.sin(angleRadian) * distance);
        coordinate[Y] = centerY - Math.round(Math.cos(angleRadian) * distance);

        return coordinate;
    }
}
