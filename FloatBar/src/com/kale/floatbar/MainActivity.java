package com.kale.floatbar;

import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.view.accessibility.AccessibilityManager;

import com.kale.floatbar.service.FloatService;
import com.kale.floatbar.util.Prefs;
import com.kale.floatbar.util.Util;

public class MainActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	boolean isEnabled = false;
	private CheckBoxPreference startcBox;
	private Preference sendMail, returnDefault;
	private Prefs prefs;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.prefs_list_content);
		addPreferencesFromResource(R.xml.float_preference);

		prefs = new Prefs(MainActivity.this);

		startcBox = (CheckBoxPreference) findPreference("started");
		startcBox.setOnPreferenceChangeListener(this);

		sendMail = (Preference) findPreference("mail");
		sendMail.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Util.sendMail(MainActivity.this, "联系作者");
				return false;
			}
		});

		returnDefault = (Preference) findPreference("returnDefaultSetting");
		returnDefault.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(MainActivity.this, "恢复默认设置", "确认后悬浮窗将会采用默认的设置", "确定", "取消");
				return false;
			}
		});

		// startActivity(new Intent(MainActivity.this,TestActivity.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
		List<AccessibilityServiceInfo> list = AccessibilityManagerCompat.getEnabledAccessibilityServiceList(manager,
				AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
		System.out.println("list.size = " + list.size());
		for (int i = 0; i < list.size(); i++) {
			System.out.println("已经可用的服务列表 = " + list.get(i).getId());
			if ("com.kale.floatbar/.service.FloatService".equals(list.get(i).getId())) {
				System.out.println("已启用");
				isEnabled = true;
				break;
			}
		}
		if (!isEnabled) {
			showDialog(this, "激活悬浮窗", "您还没有激活悬浮窗。" + "在设置中：系统 → 辅助功能 → 服务 中激活" + getResources().getString(R.string.app_name)
					+ "后，便可安全稳定的使用悬浮窗啦~", "去激活", "取消");
		}
	}

	/**
	 * 恢复到默认设置
	 * 
	 * @param title
	 * @param msg
	 */
	public void showDialog(Context mContext, String title, String msg, String positiveMsg, String cancelMsg) {

		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(mContext);
		
		builder.setTitle(title)
		.setPositiveButton(positiveMsg, new positiveListener())
		.setNegativeButton(cancelMsg, null)
		.setCancelable(false);// 设置点击空白处，不能消除该对话框
		
		builder.setMessage(msg).create().show();
	}

	/**
	 * @author:Jack Tony
	 * @tips : 监听器
	 * @date :2014-7-25
	 */
	private class positiveListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (isEnabled) {
				prefs.clearPrefs();
				finish();
				startService(new Intent(MainActivity.this, FloatService.class));
				startActivity(new Intent(MainActivity.this, MainActivity.class));
			} else {
				startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Intent intent = new Intent(MainActivity.this, FloatService.class);
		startService(intent);

		return true;
	}

}
