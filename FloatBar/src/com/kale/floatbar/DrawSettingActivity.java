package com.kale.floatbar;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.SeekBar;

import com.kale.floatbar.preference.MyListPreference;
import com.kale.floatbar.preference.MySeekBarPreference;
import com.kale.floatbar.preference.MySeekBarPreference.OnSeekBarPrefsChangeListener;
import com.kale.floatbar.service.DrawService;
import com.kale.floatbar.util.Prefs;

public class DrawSettingActivity extends PreferenceActivity implements OnPreferenceChangeListener, OnSeekBarPrefsChangeListener {
	CheckBoxPreference checkbox;
	MyListPreference colorList, textColorList;
	MySeekBarPreference seekbar;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs_list_content);
		addPreferencesFromResource(R.xml.draw_preference);

		Prefs prefs = new Prefs(this);

		checkbox = (CheckBoxPreference) findPreference("drawMode");
		checkbox.setOnPreferenceChangeListener(this);
		colorList = (MyListPreference) findPreference("drawColor");
		colorList.setOnPreferenceChangeListener(this);
		textColorList = (MyListPreference) findPreference("drawTextColor");
		textColorList.setOnPreferenceChangeListener(this);

		seekbar = (MySeekBarPreference) findPreference("drawAlpha");
		seekbar.setDefaultProgressValue(prefs.getDrawAlpha());
		seekbar.setMax(255);
		seekbar.setOnSeekBarPrefsChangeListener(this);

		startService(new Intent(DrawSettingActivity.this, DrawService.class));
	}

	/*
	 * 
	 * @see
	 * android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange
	 * (android.preference.Preference, java.lang.Object) seekbar改变时触发
	 */
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Intent intent = new Intent(DrawSettingActivity.this, DrawService.class);
		stopService(intent);
		startService(intent);
		return true;
	}

	/*
	 * @see
	 * com.kale.floatbar.preference.MySeekBarPreference.OnSeekBarPrefsChangeListener
	 * #onStopTrackingTouch(java.lang.String, android.widget.SeekBar)
	 * seekbar结束选择后触发的动作
	 */
	@Override
	public void onStopTrackingTouch(String key, SeekBar seekBar) {
		Intent intent = new Intent(DrawSettingActivity.this, DrawService.class);
		stopService(intent);
		startService(intent);
	}

	@Override
	public void onStartTrackingTouch(String key, SeekBar seekBar) {
	}

	@Override
	public void onProgressChanged(String key, SeekBar seekBar, int progress, boolean fromUser) {
	}

}