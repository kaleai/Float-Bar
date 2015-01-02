package com.kale.floatbar.util;

import com.kale.floatbar.service.DrawService;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * @author:Jack Tony
 * @tips  :这里是得到设置好的数据的工具类，之所以只有get方法，没有set方法。是因为set方法在preference控件中进行了设置
 * @date  :2014-10-8
 */
public class Prefs {
	private Context mContext;
    private static SharedPreferences sharedPreferences;
	private String preferencesName = "com.kale.floatbar_preferences";
	
	public Prefs(Context mContext) {
		this.mContext = mContext;
		sharedPreferences = mContext.getSharedPreferences(preferencesName,Context.MODE_PRIVATE); 
	}
	
	/**
	 * 设置是否是第一次打开，如果是第一次那么就展示没有开启辅助功能
	 * @param isFirstTime
	 */
	public void setIsFirstTime(boolean isFirstTime) {
		sharedPreferences.edit().putBoolean("isFristTime", isFirstTime);
	}
	
	public boolean isFirstTime() {
		return sharedPreferences.getBoolean("isFristTime", false);
	}
	
	/**
	 * 悬浮窗是否打开了，如果打开了就可用
	 * @return
	 */
	public boolean isEnabled() {
		return sharedPreferences.getBoolean("enabled", true);
	}
	
	/**
	 * 清除所有设置数据
	 */
	public void clearPrefs() {
		sharedPreferences.edit().clear().commit();
		Toast.makeText(mContext, "已恢复到默认设置", 0).show();
	}
	
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * 得到悬浮窗的color颜色
	 * @return
	 */
	public int getColor() {
		String color = sharedPreferences.getString("color", "green");
		int colorInt = 0xff000000;
		if (color.equals("black")) {
			colorInt = 0xff000000;
		}
		else if(color.equals("white")){
			colorInt = 0xffffffff;
		}
		else if (color.equals("blue")) {
			colorInt = 0xff6dcaec;
		}
		else if (color.equals("green")) {
			colorInt = 0xffb9e3d9;
		}
		else if (color.equals("red")) {
			colorInt = 0xffff7979;
		}
		else if (color.equals("orange")) {
			colorInt = 0xffffd060;
		}
		return colorInt;
	}
	
	/**
	 * 得到透明度
	 * @return
	 */
	public int getAlpha() {
		return sharedPreferences.getInt("alpha", 200);
	}
	
	/**
	 * 设置有无点击反馈
	 * @return
	 */
	public boolean isFeedback() {
		return sharedPreferences.getBoolean("feedback", true);
	}
	
	///////////////////////////////下面是后台任务抽屉的设置//////////////////////////////////////
	
	/**
	 * @return 是不是右手模式
	 */
	public boolean isRightMode() {
		return sharedPreferences.getBoolean("rightMode", true);
	}
	
	/**
	 * @return 得到悬浮条宽度
	 */
	public int getWidth() {
		return sharedPreferences.getInt("width", 30);
	}
	
	/**
	 * @return 得到悬浮条高度
	 */
	public int getHeight() {
		return sharedPreferences.getInt("height", 200);
	}
	
	/**
	 * @return 得到悬浮条位置
	 */
	public int getDistance() {
		return sharedPreferences.getInt("distance", 200);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	public void doSwipUp(AccessibilityService service) {
		String swip = sharedPreferences.getString("swipUp", "closeBar");
		doOperation(service, swip);
		/*String activity = Util.getRunningActivityName(service);
		System.out.println(activity);
		Toast.makeText(mContext, activity, 1).show();*/
	}
	
	public void doSwipDown(AccessibilityService service) {
		String swip = sharedPreferences.getString("swipDown", "openBar");
		doOperation(service, swip);
	}
	
	public void doSwipLeft(AccessibilityService service) {
		String swip = sharedPreferences.getString("swipLeft", "openDraw");
		doOperation(service, swip);
	}
	
	public void doSwipRight(AccessibilityService service) {
		String swip = sharedPreferences.getString("swipRight", "openDraw");
		doOperation(service, swip);
	}
	
	public void doTouch(AccessibilityService service) {
		String event = sharedPreferences.getString("onTouch", "nothing");
		doOperation(service, event);
	}
	
	public void doDoubleClick(AccessibilityService service) {
		String event = sharedPreferences.getString("doubleClick", "back");
		doOperation(service, event);
	}
	
	public void doLongClick(AccessibilityService service) {
		String event = sharedPreferences.getString("longClick", "home");
		doOperation(service, event);
	}
	
	
	/**
	 * 执行操作的方法，根据传来的不同值执行不同的操作
	 * @param service
	 * @param event
	 */
	private void doOperation(AccessibilityService service,String event){
		if (event.equals("openBar")) {
			Util.openStatusBar(mContext);
		}
		else if (event.equals("closeBar")) {
			Util.closeStatusBar(mContext);
		}
		else if (event.equals("openDraw")) {
			mContext.startService(new Intent(mContext,DrawService.class));
		}
		else if(event.equals("recents")) {
			Util.recentApps(service);
		}
		else if (event.equals("back")) {
			Util.virtualBack(service);
		}
		else if(event.equals("home")){
			Util.virtualHome(mContext);
		}
		else if (event.equals("camera")) {
			Util.openCamera(mContext);
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	
	public boolean getDrawMode(){
		return sharedPreferences.getBoolean("drawMode", false);
	}
	
	public int getDrawColor() {
		String color = sharedPreferences.getString("drawColor", "black");
		int colorInt = 0xff000000;
		if (color.equals("black")) {
			colorInt = 0xff000000;
		}
		else if(color.equals("white")){
			colorInt = 0xffffffff;
		}
		else if (color.equals("blue")) {
			colorInt = 0xff6dcaec;
		}
		else if (color.equals("green")) {
			colorInt = 0xffb6db49;
		}
		else if (color.equals("red")) {
			colorInt = 0xffff7979;
		}
		else if (color.equals("orange")) {
			colorInt = 0xffffd060;
		}
		return colorInt;
	}
	
	public int getDrawAlpha() {
		int alpha = sharedPreferences.getInt("drawAlpha", 120);
		return alpha;
	}
	
	public int getDrawTextColor() {
		String color = sharedPreferences.getString("drawTextColor", "white");
		return color.equals("white")?0xFFFFFFFF:0xFF000000;
	}
}
