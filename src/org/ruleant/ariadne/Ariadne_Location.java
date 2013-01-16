/**
 * Custom Ariadne Location object
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
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package org.ruleant.ariadne;

import android.location.Location;

/**
 * Custom Ariadne Location object
 * 
 * This object inherits from a Location object, but overrides the getString() method
 * 
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class Ariadne_Location extends Location {

	public Ariadne_Location(String provider){
		super (provider);
	}
	
	public Ariadne_Location(Location location){
		super (location);
	}
	
	public String toString() {
		return "Ariadne:" + super.toString();
	}
}
