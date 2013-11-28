/**
 * Unit Conversion class interface
 *
 * Copyright (C) 2013 Dieter Adriaenssens
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
 * @package com.github.ruleant.getback_gps.unitconversion
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.unitconversion;

/**
 * Unit Conversion class interface.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public interface UnitConversionInterface {
    /**
     * It will use the default unit of the conversion class,
     * so no conversion is needed.
     */
    int UNIT_DEFAULT = 0;

    /**
     * Sets value, which will be converted to class unit, if necessary.
     *
     * @param value New value
     * @param unit Unit of the new value
     */
    void setValue(double value, int unit);

    /**
     * Sets the unit that will be used when formatting a value.
     *
     * @param unit Unit of the formatted value.
     */
    void setOutputUnit(int unit);

    /**
     * Returns a localized string of the current output unit.
     *
     * @return localized unit string
     */
    String getUnit();

    /**
     * Converts the current value to the output unit, if necessary.
     *
     * @return value converted to selected unit
     */
    double getConvertedValue();

    /**
     * Converts the value to the set output unit, scales and formats it,
     * and adds a localized unit.
     *
     * @return formatted value
     */
    String getFormattedValue();
}
