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

    @Override
    public final void onDraw(final Canvas canvas) {
        // scale View if it is not square
        if (getWidth() != getHeight()) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();

            layoutParams.height = getWidth();
            setLayoutParams(layoutParams);
        }

        int maxHeight = canvas.getHeight();
        int maxWidth = canvas.getWidth();
        int centerX = maxWidth / 2;
        int centerY = maxHeight / 2;

        // draw arrow to destination
        double arrowLength = (maxHeight / 2) * .8;
        double direction = 0;
        long arrowX = centerX + Math.round(Math.sin(direction) * arrowLength);
        long arrowY = centerY + Math.round(Math.cos(direction) * arrowLength);

        canvas.drawLine(centerX, centerY, arrowX, arrowY, mPaint);
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
}
