package jp.gr.java_conf.kmj.wifi_notification;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class WifiNotificationActivity extends PreferenceActivity {
	public static final String SHARED_PREF_NAME = "wifi-notification-settings";
	public static final String SHARED_PREF_KEY_ONGOING = "ongoing";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		SharedPreferences pref = getSharedPreferences(
				WifiNotificationActivity.SHARED_PREF_NAME, MODE_PRIVATE);
		boolean currentOngoing = pref.getBoolean(
				WifiNotificationActivity.SHARED_PREF_KEY_ONGOING, false);

		CheckBoxPreference checkOngoing = (CheckBoxPreference) this
				.findPreference("ongoing");
		checkOngoing.setTitle(R.string.check_ongoing);
		checkOngoing.setEnabled(currentOngoing);
		checkOngoing
				.setOnPreferenceChangeListener(this.checkOngoingOnPreferenceChangeListener);

		String aboutTitle = (String) getText(R.string.app_name);
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(
					"jp.gr.java_conf.kmj.wifi_notification",
					PackageManager.GET_META_DATA);
			aboutTitle = aboutTitle + " : " + packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Preference about = (Preference) this.findPreference("about");
		about.setTitle(aboutTitle);

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (!netInfo.isConnected())
			return;
		this.updateNotification(currentOngoing);
	}

	Preference.OnPreferenceChangeListener checkOngoingOnPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			SharedPreferences settings = getSharedPreferences(
					WifiNotificationActivity.SHARED_PREF_NAME, MODE_PRIVATE);

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(WifiNotificationActivity.SHARED_PREF_KEY_ONGOING,
					(Boolean) newValue);
			boolean ret = editor.commit();

			updateNotification((Boolean) newValue);

			return ret;

		}
	};

	private void updateNotification(boolean ongoing) {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (!netInfo.isConnected())
			return;

		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();

		WifiStateChangeReceiver.notifyWifiConfiguration(
				getApplicationContext(), info, ongoing);
	}
}
