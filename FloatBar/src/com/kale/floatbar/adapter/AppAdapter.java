package com.kale.floatbar.adapter;

import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kale.floatbar.R;
import com.kale.floatbar.util.Prefs;

/**
 * @author:Jack Tony
 * @tips :显示最近任务的适配器
 * @date :2014-10-26
 */
public class AppAdapter implements ListAdapter {

	List<HashMap<String, Object>> mAppInfos;
	Context mContext;
	WindowManager mWindowManager;
	LinearLayout mLayout;
	DrawerLayout mDrawerLayout;
	Prefs prefs;

	public AppAdapter(Context context, WindowManager windowManager, LinearLayout layout, DrawerLayout drawerLayout,
			List<HashMap<String, Object>> appInfos) {
		mAppInfos = appInfos;
		mContext = context;
		mWindowManager = windowManager;
		mLayout = layout;
		mDrawerLayout = drawerLayout;
		prefs = new Prefs(mContext);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public int getCount() {
		return mAppInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	/**
	 * 自定义view
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View infoView = mInflater.inflate(R.layout.app_item, null);
		ImageView mImageView = (ImageView) infoView.findViewById(R.id.icon);
		TextView mTextView = (TextView) infoView.findViewById(R.id.title);
		mTextView.setTextColor(prefs.getDrawTextColor());

		String title = (String) mAppInfos.get(position).get("title");
		Drawable icon = (Drawable) mAppInfos.get(position).get("icon");
		Intent singleIntent = (Intent) mAppInfos.get(position).get("tag");

		infoView.setTag(singleIntent);
		mImageView.setImageDrawable(icon);
		mTextView.setText(title);

		// 绑定点击事件，用来进行应用间的跳转
		infoView.setOnClickListener(new SingleAppClickListener());
		return infoView;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	// 点击应用的图标启动应用程序
	class SingleAppClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = (Intent) v.getTag();
			if (intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				try {
					mContext.startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Log.w("Recent", "Unable to launch recent task", e);
				} catch (SecurityException e) {
					// e.printStackTrace();
					Toast.makeText(mContext, "该应用不支持快速启动", 0).show();
				}
			}
			mDrawerLayout.closeDrawers();
			((Service) mContext).stopSelf();
			mWindowManager.removeView(mLayout);

		}
	}
}
