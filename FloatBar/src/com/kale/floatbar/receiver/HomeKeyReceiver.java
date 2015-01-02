package com.kale.floatbar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kale.floatbar.service.DrawService;

public class HomeKeyReceiver extends BroadcastReceiver{
	
	static final String SYSTEM_REASON = "reason";
	static final String SYSTEM_HOME_KEY = "homekey";// home key
	static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
			String reason = intent.getStringExtra(SYSTEM_REASON);
			if (reason != null) {
				if (reason.equals(SYSTEM_HOME_KEY)) {
					Log.e("homekey", "home键被点击");
					context.stopService(new Intent(context,DrawService.class));
				} else if (reason.equals(SYSTEM_RECENT_APPS)) {
					 Log.e("homekey", "长按home键");
				}
			}
		}
	}
}
