/**
 * Preference Activity, for settings.
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
 * @author  Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
package com.github.ruleant.getback_gps;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.github.ruleant.getback_gps.lib.DebugLevel;

import java.util.List;

/**
 * A PreferenceActivity that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Key of preference Location Update Distance.
     */
    public static final String KEY_PREF_LOC_UPDATE_DIST = "loc_update_dist";

    /**
     * Default value of preference Location Update Distance.
     */
    public static final String DEFAULT_PREF_LOC_UPDATE_DIST = "10";

    /**
     * Key of preference Location Update Time.
     */
    public static final String KEY_PREF_LOC_UPDATE_TIME = "loc_update_time";

    /**
     * Default value of preference Location Update Time.
     */
    public static final String DEFAULT_PREF_LOC_UPDATE_TIME = "10000";

    /**
     * Key of preference Enable sensors.
     */
    public static final String KEY_PREF_ENABLE_SENSORS = "enable_sensors";

    /**
     * Default value of preference Enable sensors.
     */
    public static final boolean DEFAULT_PREF_ENABLE_SENSORS = true;

    /**
     * Key of preference Default geo orientation sensor.
     */
    public static final String KEY_PREF_GEO_ORIENTATION_SENSOR
            = "geo_orientation_sensor";

    /**
     * Geo orientation sensor value : automatic.
     */
    public static final int GEO_ORIENTATION_SENSOR_AUTO = 0;

    /**
     * Geo orientation sensor value : raw sensors.
     */
    public static final int GEO_ORIENTATION_SENSOR_RAW = 1;

    /**
     * Geo orientation sensor value : calculated value.
     */
    public static final int GEO_ORIENTATION_SENSOR_CALCULATED = 2;

    /**
     * Default value of preference Default geo orientation sensor :
     * automatic.
     */
    public static final String DEFAULT_PREF_GEO_ORIENTATION_SENSOR = "0";

    /**
     * 60 seconds.
     */
    private static final int SIXTY_SECONDS = 60;

    /**
     * 1000 milliseconds.
     */
    private static final int ONEK_MSECONDS = 1000;

    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    @Override
    protected final void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);
        populateLocUpdateDist();
        populateLocUpdateTime();

        // Add 'debug' preferences if DEBUG is enabled.
        if (BuildConfig.DEBUG) {
            PreferenceCategory headerDebug = new PreferenceCategory(this);
            headerDebug.setTitle(R.string.pref_header_debug);
            getPreferenceScreen().addPreference(headerDebug);
            addPreferencesFromResource(R.xml.pref_debug);
        }

        // Bind the summaries of the preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(
                findPreference(KEY_PREF_LOC_UPDATE_DIST));
        bindPreferenceSummaryToValue(
                findPreference(KEY_PREF_LOC_UPDATE_TIME));
        bindPreferenceSummaryToValue(
                findPreference(KEY_PREF_GEO_ORIENTATION_SENSOR));
        if (BuildConfig.DEBUG) {
            bindPreferenceSummaryToValue(
                    findPreference(DebugLevel.PREF_DEBUG_LEVEL));
        }
    }

    /**
     *  Populate visible options in Distance based Location Update Setting.
     */
    private void populateLocUpdateDist() {
        // TODO move to separate class to remove duplicate method
        // in GeneralPreferenceFragment
        ListPreference locUpdateDistPref
                = (ListPreference) findPreference(KEY_PREF_LOC_UPDATE_DIST);
        Resources resources = getResources();
        CharSequence[] values
                = resources.getStringArray(
                R.array.pref_loc_update_dist_list_values);

        Integer optionsLength = values.length;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                && optionsLength >= 1) {
            optionsLength--;
        }

        CharSequence[] options = new CharSequence[optionsLength];
        CharSequence[] captions = new CharSequence[optionsLength];

        for (int i = 0, j = 0; i < values.length; i++) {
            int value = Integer.parseInt(values[i].toString());
            if (value > 0) {
                options[j] = values[i];
                captions[j] = resources.getQuantityString(
                        R.plurals.distance_meter, value, value);
                j++;
            } else if (Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.JELLY_BEAN) {
                /* don't allow to disable distance based updates
                 * before Jelly Bean, because the time based update parameter
                 * is not respected before Jelly Bean,
                 * so the distance based update parameter should have a value.
                 */
                options[j] = values[i];
                captions[j] = resources.getString(R.string.disabled);
                j++;
            }
        }
        locUpdateDistPref.setEntries(captions);
        locUpdateDistPref.setEntryValues(options);
    }

    /**
     *  Populate visible options in Time based Location Update Setting.
     */
    private void populateLocUpdateTime() {
        // TODO move to separate class to remove duplicate method
        // in GeneralPreferenceFragment
        // TODO merge common parts with populateLocUpdateDist
        ListPreference locUpdateTimePref
                = (ListPreference) findPreference(KEY_PREF_LOC_UPDATE_TIME);
        Resources resources = getResources();
        CharSequence[] values
                = resources.getStringArray(
                R.array.pref_loc_update_time_list_values);
        CharSequence[] captions = new CharSequence[values.length];
        for (int i = 0; i < values.length; i++) {
            int value = Integer.parseInt(values[i].toString()) / ONEK_MSECONDS;
            if (value >= SIXTY_SECONDS) {
                value = value / SIXTY_SECONDS;
                captions[i] = resources.getQuantityString(
                        R.plurals.time_minutes, value, value);
            } else {
                captions[i] = resources.getQuantityString(
                        R.plurals.time_seconds, value, value);
            }
        }
        locUpdateTimePref.setEntries(captions);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     *
     * @param context Context of the App
     * @return true if device has an extra-large screen
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static boolean isXLargeTablet(final Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
                && (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     *
     * @param context Context of the App
     * @return true if simplified settings UI should be shown
     */
    private static boolean isSimplePreferences(final Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public final void onBuildHeaders(final List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
            // Add 'debug' preference header if DEBUG is enabled.
            if (BuildConfig.DEBUG) {
                loadHeadersFromResource(R.xml.pref_header_debug, target);
            }
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener
            sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(
                final Preference preference, final Object value) {

            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                if (index >= 0) {
                    preference.setSummary(listPreference.getEntries()[index]);
                } else {
                    preference.setSummary(null);
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @param preference Preference to bind to
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(
            final Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(
                sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(
                        preference.getContext()).getString(preference.getKey(),
                        ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static final class GeneralPreferenceFragment
            extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            populateLocUpdateDist();
            populateLocUpdateTime();

            // Bind the summaries of the preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(
                    findPreference(KEY_PREF_LOC_UPDATE_DIST));
            bindPreferenceSummaryToValue(
                    findPreference(KEY_PREF_LOC_UPDATE_TIME));
            bindPreferenceSummaryToValue(
                    findPreference(KEY_PREF_GEO_ORIENTATION_SENSOR));
        }

        /**
         *  Populate visible options in Distance based Location Update Setting.
         *  Duplicate of same method in parent class.
         */
        private void populateLocUpdateDist() {
            // duplicate of method in GeneralPreferenceFragment
            ListPreference locUpdateDistPref
                    = (ListPreference) findPreference(KEY_PREF_LOC_UPDATE_DIST);
            Resources resources = getResources();
            CharSequence[] values
                    = resources.getStringArray(
                    R.array.pref_loc_update_dist_list_values);
            CharSequence[] captions = new CharSequence[values.length];
            for (int i = 0; i < values.length; i++) {
                int value = Integer.parseInt(values[i].toString());
                if (value > 0) {
                    captions[i] = resources.getQuantityString(
                            R.plurals.distance_meter, value, value);
                } else {
                    captions[i] = resources.getString(R.string.disabled);
                }
            }
            locUpdateDistPref.setEntries(captions);
        }

        /**
         *  Populate visible options in Time based Location Update Setting.
         */
        private void populateLocUpdateTime() {
            // duplicate of method in GeneralPreferenceFragment
            ListPreference locUpdateTimePref
                    = (ListPreference) findPreference(KEY_PREF_LOC_UPDATE_TIME);
            Resources resources = getResources();
            CharSequence[] values
                    = resources.getStringArray(
                    R.array.pref_loc_update_time_list_values);
            CharSequence[] captions = new CharSequence[values.length];
            for (int i = 0; i < values.length; i++) {
                int value = Integer.parseInt(
                        values[i].toString()) / ONEK_MSECONDS;
                if (value >= SIXTY_SECONDS) {
                    value = value / SIXTY_SECONDS;
                    captions[i] = resources.getQuantityString(
                            R.plurals.time_minutes, value, value);
                } else {
                    captions[i] = resources.getQuantityString(
                            R.plurals.time_seconds, value, value);
                }
            }
            locUpdateTimePref.setEntries(captions);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DebugPreferenceFragment extends PreferenceFragment {
        @Override
        public final void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Add 'debug' preferences if DEBUG is enabled.
            if (!BuildConfig.DEBUG) {
                return;
            }

            addPreferencesFromResource(R.xml.pref_debug);

            // Bind the summaries of the preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(
                    findPreference(DebugLevel.PREF_DEBUG_LEVEL));
        }
    }

    @Override
    protected final boolean isValidFragment(final String fragmentName) {
        // check if an allowed fragment is used
        return GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DebugPreferenceFragment.class.getName().equals(fragmentName);
    }
}
