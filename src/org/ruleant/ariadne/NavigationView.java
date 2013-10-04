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
 * @package org.ruleant.ariadne
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.ruleant.ariadne.lib.FormatUtils;

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
    public NavigationView(final Context context, final AttributeSet attributes) {
        super(context, attributes);

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

            layoutParams.height = getWidth();
            setLayoutParams(layoutParams);
        }

        // draw arrow to destination
        double arrowLength = (getHeight() / 2) * .8;

        long[] startCoordinate = polarToCartesian(0, 0);
        long[] endCoordinate = polarToCartesian(getDirection(), arrowLength);

        canvas.drawLine(startCoordinate[X], startCoordinate[Y],
                endCoordinate[X], endCoordinate[Y], mPaint);
    }

    /**
     * Initialise NavigationView.
     */
    private void init() {
        // set Background
        setBackgroundResource(R.drawable.custom_grid);

        // initialise paint
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4);
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
    private long[] polarToCartesian(final double angleDegrees, final double distance) {
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
