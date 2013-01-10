package org.ruleant.ariadne;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		String versionName = "";
		String lastUpdated = "";
		PackageInfo packageInfo;
		
		try {
		        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		    	versionName = "v" + packageInfo.versionName;
		 		Date date = new Date(packageInfo.lastUpdateTime);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
				lastUpdated = formatter.format(date);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		TextView tv_version = (TextView) findViewById(R.id.textview_version);
		tv_version.setText(getResources().getString(R.string.version) + ": " + versionName);
		TextView tv_author = (TextView) findViewById(R.id.textview_author);
		tv_author.setText(getResources().getString(R.string.author) + ": " + MainActivity.AUTHOR);
		TextView tv_updated = (TextView) findViewById(R.id.textview_updated);
		tv_updated.setText(getResources().getString(R.string.updated) + ": " + lastUpdated);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
