/**
 * Class to format distances, speeds and numbers.
 *
 * Copyright (C) 2010 Peer internet solutions
 * Copyright (C) 2013 Dieter Adriaenssens
 *
 * This file is based on a class that is part of mixare.
 * The original source can be found on :
 * http://www.java2s.com/Code/Android/Date-Type/FormatDistance.htm
 * See commit logs for changes.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 *
 * @package org.ruleant.ariadne
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

class FormatUtils {
  public static String formatDist(float meters) {
    if (meters < 1000) {
      return ((int) meters) + "m";
    } else if (meters < 10000) {
      return formatDec(meters / 1000f, 1) + "km";
    } else {
      return ((int) (meters / 1000f)) + "km";
    }
  }
  static String formatDec(float val, int dec) {
    int factor = (int) Math.pow(10, dec);

    int front = (int) (val);
    int back = (int) Math.abs(val * (factor)) % factor;

    return front + "." + back;
  }
}