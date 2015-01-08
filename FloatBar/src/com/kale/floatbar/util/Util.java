package com.kale.floatbar.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import com.kale.floatbar.R;
import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class Util {

	/**
	 * 对windowManager进行设置
	 * 
	 * @param wmParams
	 * @return
	 */
	public static WindowManager.LayoutParams getParams(WindowManager.LayoutParams wmParams) {
		wmParams = new WindowManager.LayoutParams();
		// 设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
		// wmParams.type = LayoutParams.TYPE_PHONE;
		// wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		// wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// 设置可以显示在状态栏上
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		return wmParams;
	}

	/**
	 * 核心方法，加载最近启动的应用程序 注意：这里我们取出的最近任务为 MAX_RECENT_TASKS +
	 * 1个，因为有可能最近任务中包好Launcher2。 这样可以保证我们展示出来的 最近任务 为 MAX_RECENT_TASKS 个
	 * 通过以下步骤，可以获得近期任务列表，并将其存放在了appInfos这个list中，接下来就是展示这个list的工作了。
	 */
	public static void reloadButtons(Service service, List<HashMap<String, Object>> appInfos, int appNumber) {
		int MAX_RECENT_TASKS = appNumber; // allow for some discards
		int repeatCount = appNumber;// 保证上面两个值相等,设定存放的程序个数

		/* 每次加载必须清空list中的内容 */
		appInfos.removeAll(appInfos);

		// 得到包管理器和activity管理器
		final Context context = service.getApplication();
		final PackageManager pm = context.getPackageManager();
		final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		// 从ActivityManager中取出用户最近launch过的 MAX_RECENT_TASKS + 1 个，以从早到晚的时间排序，
		// 注意这个 0x0002,它的值在launcher中是用ActivityManager.RECENT_IGNORE_UNAVAILABLE
		// 但是这是一个隐藏域，因此我把它的值直接拷贝到这里
		@SuppressWarnings("deprecation")
		final List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(MAX_RECENT_TASKS + 1, 0x0002);

		// 这个activity的信息是我们的launcher
		ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);
		int numTasks = recentTasks.size();
		for (int i = 0; i < numTasks && (i < MAX_RECENT_TASKS); i++) {
			HashMap<String, Object> singleAppInfo = new HashMap<String, Object>();// 当个启动过的应用程序的信息
			final ActivityManager.RecentTaskInfo info = recentTasks.get(i);

			Intent intent = new Intent(info.baseIntent);
			if (info.origActivity != null) {
				intent.setComponent(info.origActivity);
			}
			/**
			 * 如果找到是launcher，直接continue，后面的appInfos.add操作就不会发生了
			 */
			if (homeInfo != null) {
				if (homeInfo.packageName.equals(intent.getComponent().getPackageName()) && homeInfo.name.equals(intent.getComponent().getClassName())) {
					MAX_RECENT_TASKS = MAX_RECENT_TASKS + 1;
					continue;
				}
			}
			// 设置intent的启动方式为 创建新task()【并不一定会创建】
			intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK);
			// 获取指定应用程序activity的信息(按我的理解是：某一个应用程序的最后一个在前台出现过的activity。)
			final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
			if (resolveInfo != null) {
				final ActivityInfo activityInfo = resolveInfo.activityInfo;
				final String title = activityInfo.loadLabel(pm).toString();
				Drawable icon = activityInfo.loadIcon(pm);

				if (title != null && title.length() > 0 && icon != null) {
					singleAppInfo.put("title", title);
					singleAppInfo.put("icon", icon);
					singleAppInfo.put("tag", intent);
					singleAppInfo.put("packageName", activityInfo.packageName);
					appInfos.add(singleAppInfo);
				}
			}
		}
		MAX_RECENT_TASKS = repeatCount;
	}

	/**
	 * 虚拟home键
	 */
	public static void virtualHome(Context mContext) {
		// 模拟HOME键
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 如果是服务里调用，必须加入new task标识
		i.addCategory(Intent.CATEGORY_HOME);
		mContext.startActivity(i);
	}

	/**
	 * 模拟返回键 API 16(Android 4.1)及以上才能用
	 * 
	 * @param service
	 */
	public static void virtualBack(AccessibilityService service) {
		if (VERSION.SDK_INT < 16) {
			Toast.makeText(service, "Android 4.1及以上系统才支持此功能，请升级后重试", 1).show();
		} else {
			service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
		}
	}

	/**
	 * 模拟最近任务
	 * 
	 * @param service
	 */
	public static void recentApps(AccessibilityService service) {
		if (VERSION.SDK_INT < 16) {
			Toast.makeText(service, "Android 4.1及以上系统才支持此功能，请升级后重试", 1).show();
		} else {
			service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
		}
	}

	private static void doInStatusBar(Context mContext, String methodName) {
		try {
			Object service = mContext.getSystemService("statusbar");
			Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
			Method expand = statusBarManager.getMethod(methodName);
			expand.invoke(service);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示消息中心
	 */
	public static void openStatusBar(Context mContext) {
		// 判断系统版本号
		String methodName = (VERSION.SDK_INT <= 16) ? "expand" : "expandNotificationsPanel";
		doInStatusBar(mContext, methodName);
	}

	/**
	 * 关闭消息中心
	 */
	public static void closeStatusBar(Context mContext) {
		// 判断系统版本号
		String methodName = (VERSION.SDK_INT <= 16) ? "collapse" : "collapsePanels";
		doInStatusBar(mContext, methodName);
	}

	/**
	 * @return 手机当前的activity
	 */
	@SuppressWarnings("deprecation")
	public static String getRunningActivityName(Context mContext) {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		System.out.println("running Activity = " + runningActivity);
		return runningActivity;
	}

	/**
	 * 启动系统相机界面
	 */
	public static void openCamera(Context mContext) {
		Intent intentCamera = new Intent();
		intentCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentCamera.setAction("android.media.action.STILL_IMAGE_CAMERA");
		mContext.startActivity(intentCamera);
	}

	/**
	 * @param mySbuject
	 *            给开发者发送邮件
	 */
	public static void sendMail(Context mContext, String mySbuject) {
		String[] reciver = new String[] { "developer_kale@qq.com" };
		// 标题
		String myCc = "cc";
		// 内容
		String phoneName = android.os.Build.MODEL;
		String mybody = "【" + mContext.getResources().getString(R.string.app_name) + "】" + "[来自" + phoneName + "用户的反馈]";

		Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
		myIntent.setType("plain/text");
		myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
		myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
		myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
		myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);
		mContext.startActivity(Intent.createChooser(myIntent, mySbuject));
	}
}
