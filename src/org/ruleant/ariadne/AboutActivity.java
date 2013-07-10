package org.ruleant.ariadne;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

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

        DateFormat formatter = SimpleDateFormat.getDateInstance();

        // Display time when in debug mode
        DebugLevel debug = new DebugLevel(this);
        if (debug.checkDebugLevel(DebugLevel.DEBUG_LEVEL_LOW)) {
            formatter = SimpleDateFormat.getDateTimeInstance();
        }
        String versionInfo = res.getString(R.string.app_name);
        PackageInfo packageInfo;

        try {
            packageInfo
                = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionInfo += " v" + packageInfo.versionName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                Date date = new Date(packageInfo.lastUpdateTime);
                versionInfo += " (" + formatter.format(date) + ")";
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // Version text view
        TextView tvVersion = (TextView) findViewById(R.id.textview_version);
        tvVersion.setText(versionInfo);

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
}
