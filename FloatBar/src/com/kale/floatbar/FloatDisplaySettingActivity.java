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
import com.kale.floatbar.service.FloatService;
import com.kale.floatbar.util.Prefs;

public class FloatDisplaySettingActivity extends PreferenceActivity implements
		OnPreferenceChangeListener, OnSeekBarPrefsChangeListener {

	private CheckBoxPreference rightCBox;
	private MyListPreference colorList;
	private MySeekBarPreference alphaSb, widthSb, heightSb, distanceSb;
	private Prefs prefs;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs_list_content);
		addPreferencesFromResource(R.xml.float_display_preference);

		prefs = new Prefs(this);

		colorList = (MyListPreference) findPreference("color");
		colorList.setOnPreferenceChangeListener(this);

		alphaSb = (MySeekBarPreference) findPreference("alpha");// 找到preference
		alphaSb.setDefaultProgressValue(prefs.getAlpha());// 设置起始时的进度
		alphaSb.setMax(255);// 设置最大的数值，不超过10000。如果超过了请在seekbarPreference源码中进行修改max值
		alphaSb.setOnSeekBarPrefsChangeListener(this);// 设置监听器

		rightCBox = (CheckBoxPreference) findPreference("rightMode");
		rightCBox.setOnPreferenceChangeListener(this);

		widthSb = (MySeekBarPreference) findPreference("width");
		widthSb.setMax(80);
		widthSb.setDefaultProgressValue(prefs.getWidth());
		widthSb.setOnSeekBarPrefsChangeListener(this);

		heightSb = (MySeekBarPreference) findPreference("height");
		heightSb.setMax(1000);
		heightSb.setDefaultProgressValue(prefs.getHeight());
		heightSb.setOnSeekBarPrefsChangeListener(this);

		distanceSb = (MySeekBarPreference) findPreference("distance");
		distanceSb.setMax(1000);
		distanceSb.setDefaultProgressValue(prefs.getDistance());
		distanceSb.setOnSeekBarPrefsChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Intent intent = new Intent(FloatDisplaySettingActivity.this, FloatService.class);
		startService(intent);
		return true;
	}

	@Override
	public void onStopTrackingTouch(String key, SeekBar seekBar) {
	}

	@Override
	public void onStartTrackingTouch(String key, SeekBar seekBar) {
	}

	@Override
	public void onProgressChanged(String key, SeekBar seekBar, int progress, boolean fromUser) {
		startService(new Intent(FloatDisplaySettingActivity.this, FloatService.class));
	}

}
