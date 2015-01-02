package com.kale.floatbar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kale.floatbar.R;
import com.kale.floatbar.adapter.AppAdapter;
import com.kale.floatbar.receiver.HomeKeyReceiver;
import com.kale.floatbar.util.Prefs;
import com.kale.floatbar.util.Util;

/**
 * @author:Jack Tony
 * @tips  :
 * @date  :2014-8-13
 */
/**
 * @author:Jack Tony
 * @tips :
 * @date :2014-8-13
 */
public class DrawService extends Service {
	/**
	 * 定义浮动窗口布局
	 */
	LinearLayout layout;

	/**
	 * 悬浮窗的布局
	 */
	WindowManager.LayoutParams wmParams;
	LayoutInflater inflater;
	/**
	 * 创建浮动窗口设置布局参数的对象
	 */
	WindowManager mWindowManager;

	/**
	 * 抽屉控件
	 */
	DrawerLayout mDrawerLayout;
	
	/**
	 * 抽屉内的布局 
	 */
	LinearLayout drawContent;
	
	/**
	 * 监听HOME键的广播接受者
	 */
	HomeKeyReceiver receiver;

	/**
	 * 用来存放每一个recentApplication的信息，我们这里存放应用程序名，应用程序图标和intent。
	 */
	private List<HashMap<String, Object>> appInfos = new ArrayList<HashMap<String, Object>>();

	//////////////////////////////////////////////////////////////////////////
	/**
	 * 得到存储的对象
	 */
	private Prefs prefs;
	/**
	 * 抽屉的方向，是从左开的还是从右开的
	 */
	private boolean RIGHT_MODE;
	
	private int DRAW_COLOR;
	/**
	 * 设置抽屉背景图的透明度 
	 */
	private int ALPHA;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = new Prefs(this);
		// 注册监听home键广播
		receiver = new HomeKeyReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		initDrawFloating();
		updateUi();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (layout != null) {
			// 取消监听home键
			unregisterReceiver(receiver);
			// 移除悬浮窗口
			try {
				mWindowManager.removeView(layout);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * 初始化windowsmanager
	 */
	private void initDrawFloating() {
		mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
		wmParams = Util.getParams(wmParams);
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

		wmParams.x = 0;
		wmParams.y = 0;

		inflater = LayoutInflater.from(getApplication());
	}

	/**
	 * 更新悬浮窗布局等
	 */
	private void updateUi() {
		RIGHT_MODE = !prefs.getDrawMode();
		DRAW_COLOR = prefs.getDrawColor();
		ALPHA = prefs.getDrawAlpha();
		
		
		// 获取浮动窗口视图所在布局
		layout = (LinearLayout) inflater.inflate(
				RIGHT_MODE ? R.layout.draw_right : R.layout.draw_left, null);
		// 添加悬浮窗的视图
		mWindowManager.addView(layout, wmParams);
		
		/**
		 * 设置抽屉控件的打开方向和监听器
		 */
		mDrawerLayout = (DrawerLayout) layout.findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerListener(new MyDrawListener());
		mDrawerLayout.openDrawer(RIGHT_MODE ? Gravity.RIGHT : Gravity.LEFT);
		
		/**
		 * 设置上方的home键
		 */
		Button home = (Button)layout.findViewById(R.id.home_key);
		home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.virtualHome(getBaseContext());
				stopSelf();
			}
		});
		
		/**
		 * 设置抽屉控件内的背景
		 */
		drawContent = (LinearLayout)layout.findViewById(R.id.drawer_content);
		drawContent.setBackgroundColor(DRAW_COLOR);
		drawContent.getBackground().setAlpha(ALPHA);

		/**
		 * 设置最近任务list中item的个数：20
		 */
		Util.reloadButtons(this, appInfos, 20);
		ListView listView = (ListView) layout.findViewById(R.id.drawer_list);
		listView.setAdapter(new AppAdapter(this,mWindowManager,layout,
				mDrawerLayout, appInfos));
		
		// 悬浮窗显示确定右上角为起始坐标
		wmParams.gravity = RIGHT_MODE ? Gravity.RIGHT : Gravity.LEFT
				| Gravity.TOP;
		// 以屏幕右上角为原点，设置x、y初始值，确定显示窗口的起始位置
		// 添加动画。参考自：http://bbs.9ria.com/thread-242912-1-1.html
		wmParams.windowAnimations = (RIGHT_MODE) ? R.style.right_anim : R.style.left_anim;

		mWindowManager.updateViewLayout(layout, wmParams);
	}

	/**
	 * @author:Jack Tony
	 * @tips :设置抽屉的监听器。抽屉关闭后直接结束service
	 * @date :2014-8-11
	 */
	private class MyDrawListener implements DrawerListener {

		@Override
		public void onDrawerStateChanged(int arg0) {
		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
		}

		@Override
		public void onDrawerOpened(View arg0) {
		}

		@Override
		public void onDrawerClosed(View drawerLayout) {
			stopSelf();
		}
	}

}
