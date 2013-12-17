/**
 * DebugLevel class
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
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.ruleant.getback_gps.BuildConfig;

/**
 * Class checking debug level
 *
 * This class will get the current Debug level from SharedPreferences.
 * And checks if the current level matches the required level
 *
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
public class DebugLevel {
    /**
     * Context of the App.
     */
    private Context mContext;

    /**
     * Default debug level (string).
     */
    private static final String DEFAULT_DEBUG_LEVEL = "0";

    /**
     * Name of Debug Level in Shared Preferences.
     */
    public static final String PREF_DEBUG_LEVEL = "debug_level";

    /**
     * Debug level : off.
     */
    public static final int DEBUG_LEVEL_OFF = 0;

    /**
     * Debug level : low (show limited debug messages).
     */
    public static final int DEBUG_LEVEL_LOW = 1;

    /**
     * Debug level : medium (show some debug messages).
     */
    public static final int DEBUG_LEVEL_MEDIUM = 2;

    /**
     * Debug level : high (show all debug messages).
     */
    public static final int DEBUG_LEVEL_HIGH = 3;

    /**
     * Constructor.
     *
     * @param context Context of the App
     */
    public DebugLevel(final Context context) {
        mContext = context;
    }

    /**
     * Get current debug level from SharedPreferences.
     *
     * @return current debugLevel
     */
    public final int getDebugLevel() {
        if (!BuildConfig.DEBUG || mContext == null) {
            return DEBUG_LEVEL_OFF;
        }

        // Get debug level from SharedPreferences
        SharedPreferences sharedPref
                = PreferenceManager.getDefaultSharedPreferences(mContext);
        String prefDebugLevel
                = sharedPref.getString(PREF_DEBUG_LEVEL, DEFAULT_DEBUG_LEVEL);

        return Integer.parseInt(prefDebugLevel);
    }

    /**
     * Check if the current debug level is set to the required level.
     *
     * @param debugLevel Debug level to check
     * @return true if current debugLevel is at least the needed level
     */
    public final boolean checkDebugLevel(final int debugLevel) {
        return BuildConfig.DEBUG && getDebugLevel() >= debugLevel;
    }
}
