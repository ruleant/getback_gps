/**
 * About activity, shows app information.
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
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.github.ruleant.getback_gps.lib.DebugLevel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This activity displays information about the App : version, build date,
 * author and license info.
 *
 * @author Dieter Adriaenssens <ruleant@users.sourceforge.net>
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AboutActivity extends Activity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Resources res = getResources();
        DebugLevel debug = new DebugLevel(this);

        DateFormat formatter;
        if (debug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_LOW)) {
            formatter = SimpleDateFormat.getDateTimeInstance();
        } else {
            formatter = SimpleDateFormat.getDateInstance();
        }

        String versionInfo = res.getString(R.string.app_name);
        String buildTime = "";

        PackageInfo packageInfo = getPackageInfo();
        if (packageInfo != null) {
            versionInfo += " v" + packageInfo.versionName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                Date date = new Date(packageInfo.lastUpdateTime);
                buildTime = String.format(res.getString(R.string.build_time),
                        formatter.format(date));
            }
        }

        // Version text view
        TextView tvVersion = (TextView) findViewById(R.id.textview_version);
        tvVersion.setText(versionInfo);

        // Build time text view
        TextView tvBuildTime = (TextView) findViewById(R.id.textview_buildtime);
        tvBuildTime.setText(buildTime);

        // Version text view
        TextView tvWebsite = (TextView) findViewById(R.id.textview_website);
        // enable HTML links
        tvWebsite.setMovementMethod(LinkMovementMethod.getInstance());
        // set text
        String websiteText
                = String.format(res.getString(R.string.app_website),
                res.getString(R.string.url_website));
        // Html.fromHtml will turn escaped HTML characters back
        // into regular characters.
        // The escaping is needed because Resource.getString()
        // removes style information
        tvWebsite.setText(Html.fromHtml(websiteText));

        // Copyright text view
        TextView tvCopyright = (TextView) findViewById(R.id.textview_copyright);
        // enable HTML links
        tvCopyright.setMovementMethod(LinkMovementMethod.getInstance());
        // set text
        String copyrightText = res.getString(R.string.copyright)
                + " " + res.getText(R.string.copyright_ruleant);
        // Html.fromHtml will turn escaped HTML characters back
        // into regular characters.
        // The escaping is needed because Resource.getString()
        // removes style information
        tvCopyright.setText(Html.fromHtml(copyrightText));

        // License text view
        TextView tvLicense = (TextView) findViewById(R.id.textview_license);
        // enable HTML links
        tvLicense.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Retrieve Package info (build timestamp, version, ...).
     *
     * @return Package info
     */
    private PackageInfo getPackageInfo() {
        PackageManager pm = getPackageManager();
        if (pm == null) {
            return null;
        }

        try {
            return pm.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
