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
import android.view.View;

/**
 * Navigation view is used to indicate the direction to the destination.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class NavigationView extends View {
    /**
     * Paint used for drawing
     */
    private Paint mPaint = new Paint();

    /**
     * Constructor.
     *
     * @param context App context
     */
    public NavigationView(Context context) {
        super(context);

        initPaint();
    }

    public NavigationView(Context context, AttributeSet attributes) {
        super(context, attributes);

        initPaint();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int maxHeight = canvas.getHeight();
        int maxWidth = canvas.getWidth();

        canvas.drawLine(0, 0, maxWidth, maxHeight, mPaint);
        canvas.drawLine(0, maxHeight, maxWidth, 0, mPaint);
    }

    /**
     * Initialise paint used for drawing.
     */
    private final void initPaint() {
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4);
    }
}
