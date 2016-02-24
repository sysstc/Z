package com.example.z.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UiUtil {

	public static DisplayMetrics getDisplayMetrics(Context context) {
		return context.getResources().getDisplayMetrics();
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		return metrics.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		return metrics.heightPixels;
	}

	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * sp转px
	 * 
	 * @param context
	 * @param sp
	 * @return
	 */
	public static int sp2px(Context context, float sp) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (sp * fontScale + 0.5f);
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * px转dp
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int px2dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/**
	 * 获取字体刻度.
	 * 
	 * @return
	 */
	public static float getScaledDensity(Context context) {
		return context.getResources().getDisplayMetrics().scaledDensity;
	}

	/**
	 * 隐藏输入法
	 * 
	 * @param context
	 */
	public static void hideInputMethod(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 显示输入法
	 * 
	 * @param context
	 * @param view
	 * @param requestFocus
	 */
	public static void showInputMethod(Context context, View view, boolean requestFocus) {
		if (requestFocus) {
			view.requestFocus();
		}
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 截屏
	 * 
	 * @param activity
	 * 
	 * @return
	 */
	public static Bitmap captureScreen(Activity activity) {
		activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
		Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();
		return bmp;
	}

	/**
	 * 截图
	 * 
	 * @param activity
	 * 
	 * @return
	 */
	public static Bitmap captureScreen(View view) {
		view.setDrawingCacheEnabled(true);
		Bitmap bmp = view.getDrawingCache();
		return bmp;
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

}
