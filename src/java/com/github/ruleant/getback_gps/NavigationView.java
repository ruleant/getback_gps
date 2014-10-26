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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ruleant.getback_gps.lib.Coordinate;
import com.github.ruleant.getback_gps.lib.CoordinateRotation;
import com.github.ruleant.getback_gps.lib.Coordinates;
import com.github.ruleant.getback_gps.lib.FormatUtils;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Navigation view is used to indicate the direction to the destination.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class NavigationView extends ImageView {
    /**
     * Paint used for drawing compass rose lines.
     */
    private final Paint mPaintRoseLines = new Paint();

    /**
     * Paint used for drawing compass rose solids.
     */
    private final Paint mPaintRoseSolids = new Paint();

    /**
     * Paint used for drawing compass rose North solid.
     */
    private final Paint mPaintRoseSolidNorth = new Paint();

    /**
     * Paint used for drawing lines.
     */
    private final Paint mPaintLines = new Paint();

    /**
     * Paint used for drawing solids.
     */
    private final Paint mPaintSolids = new Paint();

    /**
     * Paint used for drawing white part of the arrow solid.
     */
    private final Paint mPaintArrowSolidWhite = new Paint();

    /**
     * Arrow indicating direction (lines).
     */
    private final Coordinates mArrowLines = new Coordinates();

    /**
     * Arrow indicating direction (solid right part).
     */
    private final Coordinates mArrowBodyRight = new Coordinates();

    /**
     * Arrow indicating direction (solid left part).
     */
    private final Coordinates mArrowBodyLeft = new Coordinates();

    /**
     * Compass rose.
     */
    private final Coordinates mCompassRose = new Coordinates();

    /**
     * Compass rose solid body.
     */
    private final Coordinates mCompassRoseBody = new Coordinates();

    /**
     * Compass rose rotation converter.
     */
    private CoordinateRotation mRoseRotationConverter;

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
     * Direction to azimuth.
     */
    private double mAzimuth = 0;

    /**
     * Navigation mode enum.
     */
    public enum Mode {
        /**
         * Mode disabled.
         */
        Disabled,
        /**
         * Mode inaccurate.
         */
        Inaccurate,
        /**
         * Mode accurate.
         */
        Accurate
    }

    /**
     * Navigation mode.
     */
    private Mode mNavigationMode = Mode.Disabled;

    /**
     * Orientation mode.
     */
    private Mode mOrientationMode = Mode.Disabled;

    /**
     * Attribute layout_height.
     */
    private int mAttributeLayoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * Line thickness.
     */
    private static final float LINE_THICKNESS = 1;

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
     * Compass rose arm length.
     */
    private static final double ROSE_LENGTH = 0.95;

    /**
     * Compass rose intersection length.
     */
    private static final double ROSE_INTER_LENGTH = 0.25;

    /**
     * arrow side angle.
     */
    private static final double ARROW_ANGLE = 35.0;

    /**
     * Compass rose intersection angle.
     */
    private static final double INTERSECTION_ANGLE = 45.0;

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

        // Retrieving the values of a few parameters as defined
        // in the xml configuration.
        int[] lookForAttributes = new int[] {android.R.attr.layout_height};

        TypedArray foundAttributes = context.getTheme().obtainStyledAttributes(
                attributes,
                lookForAttributes,
                0, 0);

        try {
            mAttributeLayoutHeight = foundAttributes.getInteger(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        this.mDirection = FormatUtils.normalizeAngle(direction);
    }

    /**
     * Get direction, return nothing if mode is DISABLED.
     *
     * @return Direction to destination (0-360°)
     */
    public final double getDirection() {
        if (getNavigationMode() == Mode.Disabled) {
            return 0;
        } else {
            return mDirection;
        }
    }

    /**
     * Sets Azimuth.
     *
     * @param azimuth Angle to azimuth (0-360°).
     */
    public final void setAzimuth(final double azimuth) {
        this.mAzimuth = FormatUtils.normalizeAngle(-1 * azimuth);
    }

    /**
     * Get azimuth, return nothing if mode is DISABLED.
     *
     * @return Angle to azimuth (0-360°)
     */
    public final double getAzimuth() {
        if (getOrientationMode() == Mode.Disabled) {
            return 0;
        } else {
            return mAzimuth;
        }
    }

    /**
     * Sets navigation mode.
     *
     * @param mode Navigation mode : DISABLED, INACCURATE, ACCURATE
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public final void setNavigationMode(final Mode mode) {
        Resources res = getResources();

        switch (mode) {
            default:
            case Disabled:
                this.mNavigationMode = Mode.Disabled;
                mPaintLines.setColor(Color.GRAY);
                mPaintSolids.setColor(Color.LTGRAY);
                break;
            case Inaccurate:
                this.mNavigationMode = mode;
                if (Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mPaintLines.setColor(
                            res.getColor(android.R.color.holo_blue_dark));
                    mPaintSolids.setColor(
                            res.getColor(android.R.color.holo_blue_light));
                } else {
                    mPaintLines.setColor(Style.holoBlueLight);
                    mPaintSolids.setColor(Style.holoBlueLight);
                }
                break;
            case Accurate:
                this.mNavigationMode = mode;
                if (Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mPaintLines.setColor(
                            res.getColor(android.R.color.holo_green_dark));
                    mPaintSolids.setColor(
                            res.getColor(android.R.color.holo_green_light));
                } else {
                    mPaintLines.setColor(Style.holoGreenLight);
                    mPaintSolids.setColor(Style.holoGreenLight);
                }
                break;
        }
    }

    /**
     * Get navigation mode.
     *
     * @return Navigation mode : DISABLED, INACCURATE, ACCURATE
     */
    public final Mode getNavigationMode() {
        return mNavigationMode;
    }

    /**
     * Sets orientation mode.
     *
     * @param mode Orientation mode : DISABLED, INACCURATE, ACCURATE
     */
    public final void setOrientationMode(final Mode mode) {
        switch (mode) {
            default:
            case Disabled:
                this.mOrientationMode = Mode.Disabled;
                break;
            case Inaccurate:
                this.mOrientationMode = mode;
                break;
            case Accurate:
                this.mOrientationMode = mode;
                break;
        }
    }

    /**
     * Get orientation mode.
     *
     * @return Orientation mode : DISABLED, INACCURATE, ACCURATE
     */
    public final Mode getOrientationMode() {
        return mOrientationMode;
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
            } else {
                layoutParams.width = getHeight();
            }

            setLayoutParams(layoutParams);
        }

        // Set up rotation converter
        mRotationCenter.setCartesianCoordinate(getWidth() / 2, getHeight() / 2);
        double mRoseRotation = getAzimuth();
        mRoseRotationConverter.setScaleRadius((double) getHeight() / 2);
        mRotationConverter.setRotationAngle(getDirection());
        mRotationConverter.setScaleRadius((double) getHeight() / 2);
        // no need to reassign mRotationCenter to mRotationConverter,
        // and mRotationConverter to mArrowLines, mArrowBodyLeft/Right,
        // the instances were assigned in init().

        // draw circle with diameter scaled to length of compass rose arrow
        canvas.drawCircle(
                mRotationCenter.getCartesianX(),
                mRotationCenter.getCartesianY(),
                ((float) getHeight() / 2) * (float) ROSE_LENGTH,
                mPaintRoseLines);

        // draw compass rose
        if (getOrientationMode() == Mode.Accurate) {
            mRoseRotationConverter.setRotationAngle(mRoseRotation);
            canvas.drawPath(mCompassRoseBody.toPath(), mPaintRoseSolidNorth);
            canvas.drawLines(mCompassRose.toLinesArray(), mPaintRoseLines);

            mRoseRotationConverter.setRotationAngle(
                    FormatUtils.CIRCLE_1Q + mRoseRotation);
            canvas.drawPath(mCompassRoseBody.toPath(), mPaintRoseSolids);
            canvas.drawLines(mCompassRose.toLinesArray(), mPaintRoseLines);

            mRoseRotationConverter.setRotationAngle(
                    FormatUtils.CIRCLE_HALF + mRoseRotation);
            canvas.drawPath(mCompassRoseBody.toPath(), mPaintRoseSolids);
            canvas.drawLines(mCompassRose.toLinesArray(), mPaintRoseLines);

            mRoseRotationConverter.setRotationAngle(
                    FormatUtils.CIRCLE_3Q + mRoseRotation);
            canvas.drawPath(mCompassRoseBody.toPath(), mPaintRoseSolids);
            canvas.drawLines(mCompassRose.toLinesArray(), mPaintRoseLines);
        }

        // draw arrow to destination
        canvas.drawPath(mArrowBodyRight.toPath(), mPaintSolids);
        canvas.drawPath(mArrowBodyLeft.toPath(), mPaintArrowSolidWhite);
        canvas.drawLines(mArrowLines.toLinesArray(), mPaintLines);
    }

    /**
     * Initialise NavigationView.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void init() {
        Resources res = getResources();

        // Get the screen's density scale
        final float scale = res.getDisplayMetrics().density;

        // Convert the line thickness to pixels, based on density scale
        mPaintRoseLines.setStrokeWidth(Math.round(LINE_THICKNESS * scale));
        mPaintRoseLines.setStyle(Paint.Style.STROKE);
        mPaintLines.setStrokeWidth(Math.round(LINE_THICKNESS * scale));

        // initialise paint color
        mPaintRoseLines.setColor(Color.DKGRAY);
        mPaintRoseSolids.setColor(Color.LTGRAY);
        mPaintRoseSolidNorth.setColor(Color.GRAY);
        mPaintArrowSolidWhite.setColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mPaintLines.setColor(
                    res.getColor(android.R.color.holo_red_dark));
            mPaintSolids.setColor(
                    res.getColor(android.R.color.holo_red_light));
        } else {
            mPaintLines.setColor(Color.RED);
            mPaintSolids.setColor(Color.RED);
        }

        // initialise rotationConverter
        mRotationCenter = new Coordinate(0, 0);
        mRotationConverter = new CoordinateRotation(mRotationCenter, 0.0, 1.0);
        mRoseRotationConverter
                = new CoordinateRotation(mRotationCenter, 0.0, 1.0);
        mCompassRose.setCoordinateConverter(mRoseRotationConverter);
        mCompassRoseBody.setCoordinateConverter(mRoseRotationConverter);
        mArrowLines.setCoordinateConverter(mRotationConverter);
        mArrowBodyRight.setCoordinateConverter(mRotationConverter);
        mArrowBodyLeft.setCoordinateConverter(mRotationConverter);

        // draw compass rose

        // left side/outline in lines
        mCompassRose.addCoordinate(ROSE_LENGTH, 0);
        mCompassRose.addCoordinate(0, 0);
        mCompassRose.addCoordinate(ROSE_INTER_LENGTH, INTERSECTION_ANGLE);
        mCompassRose.addCoordinate(ROSE_LENGTH, 0);
        mCompassRose.addCoordinate(ROSE_INTER_LENGTH, -1 * INTERSECTION_ANGLE);
        // don't close line
        mCompassRose.setCloseLine(false);

        // right side filled body
        mCompassRoseBody.addCoordinate(0, 0);
        mCompassRoseBody.addCoordinate(ROSE_LENGTH, 0);
        mCompassRoseBody.addCoordinate(ROSE_INTER_LENGTH,
                -1 * INTERSECTION_ANGLE);

        // draw arrow

        double arrowLength = D_80PCT;
        double arrowLengthDivide = -1 * D_10PCT;
        double arrowLengthTail = -1 * D_40PCT;

        // left side/outline in lines
        mArrowLines.addCoordinate(arrowLength, 0);
        mArrowLines.addCoordinate(arrowLengthTail, -1 * ARROW_ANGLE);
        mArrowLines.addCoordinate(arrowLengthDivide, 0);
        mArrowLines.addCoordinate(arrowLengthTail, ARROW_ANGLE);
        mArrowLines.addCoordinate(arrowLength, 0);
        mArrowLines.addCoordinate(arrowLengthDivide, 0);
        // don't close line
        mArrowLines.setCloseLine(false);

        // right side of the filled arrow body
        mArrowBodyRight.addCoordinate(arrowLength, 0);
        mArrowBodyRight.addCoordinate(arrowLengthTail, -1 * ARROW_ANGLE);
        mArrowBodyRight.addCoordinate(arrowLengthDivide, 0);

        // left side of the filled arrow body
        mArrowBodyLeft.addCoordinate(arrowLength, 0);
        mArrowBodyLeft.addCoordinate(arrowLengthTail, ARROW_ANGLE);
        mArrowBodyLeft.addCoordinate(arrowLengthDivide, 0);
    }
}
