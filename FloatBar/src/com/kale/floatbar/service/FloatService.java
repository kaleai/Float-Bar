package com.kale.floatbar.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.kale.floatbar.R;
import com.kale.floatbar.util.Prefs;
import com.kale.floatbar.util.Util;

/**
 * @author:金凯
 * @tips :简洁风格的悬浮窗 http://my.oschina.net/u/698243/blog/129168
 * @date :2014-1-24
 */
public class FloatService extends AccessibilityService {
	String tag = getClass().getSimpleName();
	// android 4.1 API 16
	private AccessibilityService service;
	// 定义浮动窗口布局
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;
	Window window;
	static ImageButton sampleFloat;

	private Prefs prefs;

	public FloatService() {
		service = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(tag, "onCreate");
		prefs = new Prefs(service);
		createFloatView();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.e(tag, "onStartCommand");
		updateFloatService();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.e(tag, "destroy");

		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		
	}
	
	@Override
	public void onInterrupt() {

	}

	@Override
	protected void onServiceConnected() {
		//Log.e(tag, "onServiceConnected");
		updateFloatService();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	////////////////////////////////////////////////////////////////////////////


	private void createFloatView() {
		wmParams = Util.getParams(wmParams);
		// 悬浮窗默认显示以左上角为起始坐标
		wmParams.gravity = Gravity.RIGHT| Gravity.TOP;
		if (!prefs.isRightMode()) {
			wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		}
		// 以屏幕右上角为原点，设置x、y初始值，确定显示窗口的起始位置
		wmParams.x = 0;
		wmParams.y = 0;
		mWindowManager = (WindowManager) service.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater inflater = LayoutInflater.from(service);
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.floating, null);
		// 添加悬浮窗的视图
		mWindowManager.addView(mFloatLayout, wmParams);
		
		/**
		 * 设置悬浮窗的点击、滑动事件
		 */
		sampleFloat = (ImageButton) mFloatLayout.findViewById(R.id.float_button_id);
		sampleFloat.getBackground().setAlpha(150);
		sampleFloat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prefs.doTouch(service);
			}
		});
		
		/**
		 * 设置有无反馈
		 */
		MyOnGestureListener listener = new MyOnGestureListener();
		@SuppressWarnings("deprecation")
		final GestureDetector mGestureDetector = new GestureDetector(listener);

		sampleFloat.setOnTouchListener(new MyOnTouchListener(mGestureDetector));
	}

	
	private void updateFloatService() {
		/**
		 * 设置悬浮窗靠左/靠右
		 */
		if (!prefs.isRightMode()) {
			wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		}
		else {
			wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
		}
		/**
		 * 悬浮窗的上边距
		 */
		wmParams.y = prefs.getDistance();
		mWindowManager.updateViewLayout(mFloatLayout, wmParams);
		
		/**
		 * 设置悬浮窗是否可用
		 */
		if(prefs.isEnabled()) {
			sampleFloat.setVisibility(View.VISIBLE);
		}
		else {
			sampleFloat.setVisibility(View.GONE);
		}

		/**
		 * 设置悬浮窗的大小
		 */
		sampleFloat.setMinimumWidth(prefs.getWidth());
		sampleFloat.setMinimumHeight(prefs.getHeight());
		
		/**
		 * 悬浮窗的颜色和透明度
		 */
		sampleFloat.setBackgroundColor(prefs.getColor());
		sampleFloat.getBackground().setAlpha(prefs.getAlpha());
		
		
	}
	
	//////////////////////////// 下方是监听器 /////////////////////////////////
	
	/**
	 * @author:Jack Tony
	 * @tips  :设置触摸监听器，处理触摸的事件
	 * @date  :2014-8-13
	 */
	private class MyOnTouchListener implements OnTouchListener{
		private GestureDetector mGestureDetector;
		
		public MyOnTouchListener(GestureDetector mGestureDetector) {
			this.mGestureDetector = mGestureDetector;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (prefs.isFeedback()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundColor(Color.parseColor("#ffd060"));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundColor(prefs.getColor());
					v.getBackground().setAlpha(prefs.getAlpha());
				}
			}
			
			return mGestureDetector.onTouchEvent(event);
		}
	}

	/**
	 * @author:金凯
	 * @tips :自己定义的手势监听类，设置悬浮窗上下左右滑动、双击的动作
	 * @date :2014-3-29
	 */
	class MyOnGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.e(tag, "doubleClick");
			prefs.doDoubleClick(service);
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			Log.e(tag, "longPress");
			prefs.doLongClick(service);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			int dy = (int) (e2.getY() - e1.getY()); // 计算滑动的距离,纵向操作
			int dx = (int) (e2.getX() - e1.getX());

			if (dy < -20 && Math.abs(velocityY) > Math.abs(velocityX)) {
				Log.i("sample", "向上");
				prefs.doSwipUp(service);
			}

			if (dy > 20 && Math.abs(velocityY) > Math.abs(velocityX)) {
				Log.i("sample", "向下");
				prefs.doSwipDown(service);
			}

			if (dx > 20 && Math.abs(velocityX) > Math.abs(velocityY)) {
				Log.i("sample", "向右");
				prefs.doSwipRight(service);
			}
			if (dx < -20 && Math.abs(velocityX) > Math.abs(velocityY)) {
				Log.i("sample", "向左");
				prefs.doSwipLeft(service);

			}
			return false;
		}

	}


	
}
