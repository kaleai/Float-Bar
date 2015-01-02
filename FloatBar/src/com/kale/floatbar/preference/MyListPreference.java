package com.kale.floatbar.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

public class MyListPreference extends ListPreference{

	public MyListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListPreference(Context context) {
		this(context, null);
	}
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		setSummary(getEntry() == null?getSummary():getEntry());
		//setSummary(getEntry());
	}
}
