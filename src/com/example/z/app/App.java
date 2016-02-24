package com.example.z.app;

import android.app.Activity;
import android.app.Application;

import com.example.z.listener.UncaughtException;
import com.example.z.utils.ActivityManager;
import com.example.z.utils.FontUtils;

public class App extends Application {

	private static App mInstance;

	public static App getApplication() {
		return mInstance;
	}

	/**
	 * 获取当前的Activity
	 * 
	 * @return
	 */
	public static Activity getCurrentActivity() {
		return ActivityManager.getInstance().getLastActivity();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		init();
	}

	private void init() {
//		initUncaughtException();
		FontUtils.loadFont(this, "iconfont/iconfont.ttf");
	}

	/** 全局异常捕获 */
	private void initUncaughtException() {
		UncaughtException mUncaughtException = UncaughtException.getInstance();
		mUncaughtException.init();
	}

	public void exitApp(boolean isBackground) {
		if (isBackground) {
			ActivityManager.getInstance().finishAllActivity();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

}
