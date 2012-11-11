package jp.gr.java_conf.kmj.wifi_notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;

public class WifiStateChangeReceiver extends BroadcastReceiver {
	private static int NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			WifiInfo info = intent
					.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
			if (info == null) {
				WifiStateChangeReceiver.cancelWifiConfiguration(context);
				return;
			}

			SharedPreferences pref = context.getSharedPreferences(
					WifiNotificationActivity.SHARED_PREF_NAME,
					Context.MODE_PRIVATE);
			WifiStateChangeReceiver.notifyWifiConfiguration(context, info, pref
					.getBoolean(
							WifiNotificationActivity.SHARED_PREF_KEY_ONGOING,
							true));
			return;
		}

	}

	public static void notifyWifiConfiguration(Context context, WifiInfo info,
			boolean ongoing) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setContentTitle(context.getText(R.string.app_name));
		int ip = info.getIpAddress();
		String strIP = ((ip >> 0) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
		builder.setContentText(info.getSSID() + ":" + strIP);
		builder.setTicker(info.getSSID()
				+ context.getText(R.string.ticker_connected));
		builder.setAutoCancel(true);
		builder.setOngoing(ongoing);
		builder.setSmallIcon(android.R.drawable.stat_notify_chat);
		notificationManager.notify(NOTIFICATION_ID, builder.build());
	}

	public static void cancelWifiConfiguration(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}
}
