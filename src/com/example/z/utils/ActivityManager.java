package com.example.z.utils;

import java.util.Stack;

import android.app.Activity;

/**
 * Activity栈
 */
public class ActivityManager {
	private static Stack<Activity> mStackActivity;
	private static ActivityManager mInstance;

	private ActivityManager() {
		mStackActivity = new Stack<Activity>();
	}

	public static ActivityManager getInstance() {
		if (mInstance == null) {
			syncInit();
		}
		return mInstance;
	}

	private synchronized static void syncInit() {
		if (mInstance == null) {
			mInstance = new ActivityManager();
		}
	}

	// ----------------------------activity life method

	public void onCreate(Activity activity) {
		addActivity(activity);
	}

	public void onDestroy(Activity activity) {
		removeActivity(activity);
	}

	public Activity getLastActivity() {
		Activity activity = mStackActivity.lastElement();
		return activity;
	}

	public void addActivity(Activity activity) {
		if (mStackActivity.contains(activity)) {
			removeActivity(activity);
		}
		mStackActivity.add(activity);
	}

	/**
	 * 移除指定的Activity
	 */
	public void removeActivity(Activity activity) {
		if (activity != null) {
			mStackActivity.remove(activity);
		}
	}

	public void finishLastActivity() {
		Activity activity = mStackActivity.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			mStackActivity.remove(activity);
			activity.finish();
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : mStackActivity) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	public void finishAllActivity() {
		for (Activity activity : mStackActivity) {
			if (activity != null) {
				activity.finish();
			}
		}
		mStackActivity.clear();
	}

}