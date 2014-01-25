/**
 * Navigation View
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
 * @package com.github.ruleant.getback_gps
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ruleant.getback_gps.lib.Coordinate;
import com.github.ruleant.getback_gps.lib.CoordinateRotation;
import com.github.ruleant.getback_gps.lib.Coordinates;
import com.github.ruleant.getback_gps.lib.FormatUtils;

/**
 * Navigation view is used to indicate the direction to the destination.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class NavigationView extends ImageView {
    /**
     * Paint used for drawing lines.
     */
    private Paint mPaintLines = new Paint();

    /**
     * Paint used for drawing solids.
     */
    private Paint mPaintSolids = new Paint();

    /**
     * Arrow indicating direction.
     */
    private Coordinates mArrowLines = new Coordinates();

    /**
     * Arrow indicating direction.
     */
    private Coordinates mArrowBody = new Coordinates();

    /**
     * Arrow indicating direction.
     */
    private CoordinateRotation mRotationConverter;

    /**
     * Rotation center.
     */
    private Coordinate mRotationCenter;

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
     * 10 %.
     */
    private static final double D_10PCT = 0.1;

    /**
     * 40 %.
     */
    private static final double D_40PCT = 0.4;

    /**
     * 80 %.
     */
    private static final double D_80PCT = 0.8;

    /**
     * arrow side angle.
     */
    private static final double ARROW_ANGLE = 35.0;

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
        Resources res = getResources();

        switch (mode) {
            default:
            case DISABLED:
                this.mMode = DISABLED;
                mPaintLines.setColor(Color.GRAY);
                mPaintSolids.setColor(Color.LTGRAY);
                break;
            case INACCURATE:
                this.mMode = INACCURATE;
                mPaintLines.setColor(
                        res.getColor(android.R.color.holo_blue_dark));
                mPaintSolids.setColor(
                        res.getColor(android.R.color.holo_blue_light));
                break;
            case ACCURATE:
                this.mMode = ACCURATE;
                mPaintLines.setColor(
                        res.getColor(android.R.color.holo_blue_dark));
                mPaintSolids.setColor(
                        res.getColor(android.R.color.holo_blue_light));
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

        // Set up rotation converter
        mRotationCenter.setCartesianCoordinate(getWidth() / 2, getHeight() / 2);
        mRotationConverter.setRotationAngle(getDirection());
        mRotationConverter.setScaleRadius(getHeight() / 2);
        // no need to reassign mRotationCenter to mRotationConverter,
        // and mRotationConverter to mArrowLines and mArrowBody,
        // the instances were assigned in init().

        // draw arrow to destination
        canvas.drawPath(mArrowBody.toPath(), mPaintSolids);
        canvas.drawLines(mArrowLines.toLinesArray(), mPaintLines);
    }

    /**
     * Initialise NavigationView.
     */
    private void init() {
        Resources res = getResources();

        // set Background
        setBackgroundResource(R.drawable.custom_grid);

        // initialise paint
        mPaintLines.setColor(
                res.getColor(android.R.color.holo_red_dark));
        mPaintSolids.setColor(
                res.getColor(android.R.color.holo_red_light));
        mPaintLines.setStrokeWidth(LINE_THICKNESS);

        // initialise rotationConverter
        mRotationCenter = new Coordinate(0, 0);
        mRotationConverter = new CoordinateRotation(mRotationCenter, 0.0, 1.0);
        mArrowLines.setCoordinateConverter(mRotationConverter);
        mArrowBody.setCoordinateConverter(mRotationConverter);

        double arrowLength = D_80PCT;
        double arrowLengthDivide = -1 * D_10PCT;
        double arrowLengthTail = -1 * D_40PCT;

        // draw arrow
        // left side/outline in lines
        mArrowLines.addCoordinate(arrowLength, 0);
        mArrowLines.addCoordinate(arrowLengthTail, -1 * ARROW_ANGLE);
        mArrowLines.addCoordinate(arrowLengthDivide, 0);
        mArrowLines.addCoordinate(arrowLengthTail, ARROW_ANGLE);

        // right side filled body
        mArrowBody.addCoordinate(arrowLength, 0);
        mArrowBody.addCoordinate(arrowLengthTail, -1 * ARROW_ANGLE);
        mArrowBody.addCoordinate(arrowLengthDivide, 0);
    }
}
