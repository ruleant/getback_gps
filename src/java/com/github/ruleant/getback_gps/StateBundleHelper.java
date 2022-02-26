/**
 * StateBundleHelper
 *
 * Copyright (C) 2022 Jan Scheible
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
 * @author Jan Scheible
 */
package com.github.ruleant.getback_gps;

import android.os.Bundle;

class StateBundleHelper {

    /**
     * Returns a new bundle with all non-null String entries with a key that have the prefix.
     *
     * @param prefix is used for identifying relevant keys
     * @param state is the input state
     * @return a new, filtered bundle
     */
    static Bundle filterStringValuesForPrefix(String prefix, Bundle state) {
        return filterStringValuesForPrefix(prefix, state, new Bundle());
    }

    /**
     * Returns a new bundle with all non-null String entries with a key that have the prefix.
     *
     * @param prefix is used for identifying relevant keys
     * @param state is the input state
     * @param result target bundle
     * @return the target bundle with the filtered values added
     */
    static Bundle filterStringValuesForPrefix(String prefix, Bundle state, Bundle result) {
        for (final String stateKey : state.keySet()) {
            if (stateKey.startsWith(prefix)) {
                Object stateValue = state.get(stateKey);

                if(stateValue instanceof String) {
                    result.putString(stateKey, stateValue.toString());
                }
            }
        }

        return result;
    }
}
