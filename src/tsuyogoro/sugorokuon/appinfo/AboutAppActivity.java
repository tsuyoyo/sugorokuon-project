/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.appinfo;

import java.util.Formatter;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/**
 * このアプリについての情報を表示。
 *
 */
public class AboutAppActivity extends ListActivity implements OnItemClickListener {

	private static final int INDEX_COPY_RIGHT = 0;
	private static final int INDEX_APP_VERSION = 1;
	private static final int INDEX_FEEDBACK = 2;
	private static final int INDEX_APP_HISTORY = 3;
	
	private static final String EMAIL_ADDRESS = "mailto:tsuyogoro@gmail.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(createAdapter());
		getListView().setOnItemClickListener(this);
	}
	
	private ArrayAdapter<String> createAdapter() {
		// For version info
		StringBuilder versionInfoBuilder = new StringBuilder();
		Formatter formatter = new Formatter(versionInfoBuilder, Locale.US);
		formatter.format(getString(R.string.version_info), getVersionName());
		formatter.close();
		
		String[] data = {
				getString(R.string.copyright).toString(),
				versionInfoBuilder.toString(),
				getString(R.string.contact_title).toString(),
				getString(R.string.version_log)				
		};
		
		return new ArrayAdapter<String>(this, R.layout.aboutapp_listitem_layout, data);
	}
	
	private String getVersionName() {
		String versionName = "";
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_ACTIVITIES);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			Log.e(SugorokuonConst.LOGTAG, e.getMessage());
		}
		return versionName;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch(position) {
		case INDEX_APP_VERSION:
			break;
		case INDEX_COPY_RIGHT:
			break;
		case INDEX_FEEDBACK:
			sendMail();
			break;
		case INDEX_APP_HISTORY:
			break;
		}
	}
	
	private void sendMail() {
		Uri uri = Uri.parse(EMAIL_ADDRESS);
		Intent mailIntent = new Intent(Intent.ACTION_SENDTO, uri);
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
		startActivity(mailIntent);
	}
	
}
