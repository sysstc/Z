package com.example.z.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.example.z.R;
import com.example.z.app.App;
import com.example.z.customview.CustomDialog;
import com.example.z.customview.CustomDialog.OnConfirmListener;
import com.example.z.customview.SwipeBackMaterialLayout;
import com.example.z.customview.SwipeBackMaterialLayout.OnFinishListener;
import com.example.z.fragment.BaseFragment;
import com.example.z.network.TANetChangeObserver;
import com.example.z.network.TANetWorkUtil.netType;
import com.example.z.network.TANetworkStateReceiver;
import com.example.z.utils.ActivityManager;
import com.example.z.utils.FragmentUtils;
import com.example.z.utils.Position;
import com.example.z.utils.SDToast;
import com.example.z.utils.UiUtil;
import com.lidroid.xutils.ViewUtils;

public class BaseActivity extends FragmentActivity implements TANetChangeObserver {

	/** ActivityMaterial动画时间 */
	private static final int mDuration = 300;
	/** 触摸返回键是否退出App */
	protected boolean mIsExitApp = false;
	public static final String PREVIEW_IMAGE = "preview_image";
	private long mExitTime = 0;

	public SwipeBackMaterialLayout mLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().onCreate(this);
		TANetworkStateReceiver.registerObserver(this);
	}

	@Override
	public void setContentView(int resViewId) {
		// 初始化SwipeBackMaterialLayout
		mLayout = (SwipeBackMaterialLayout) LayoutInflater.from(this).inflate(R.layout.swipe_back_material, null);
		mLayout.attachToActivity(this);
		mLayout.setSwipeBackLayout(!mIsExitApp);
		mLayout.setOnFinishListener(new OnFinishListener() {
			
			@Override
			public void onFinish() {
				BaseActivity.super.finish();
			}
		});
		super.setContentView(resViewId);
		overridePendingTransition(0, 0);// 设置activity动画
		ViewUtils.inject(this);// 注解
		mLayout.post(new Runnable() {
			@Override
			public void run() {
				mLayout.push(getIntent().getIntExtra("cx", 0), getIntent().getIntExtra("cy", 0), mDuration);
			}
		});
	}

	public void startPreviewActivity(Intent intent, int x, int y) {
		startPreviewActivity(intent, x, y, -654);
	}

	public void startPreviewActivity(Intent intent, int x, int y, int requestCode) {
		intent.putExtra("cx", x);
		intent.putExtra("cy", y + UiUtil.getStatusBarHeight(this));
		startPreviewActivity(intent, requestCode);
	}

	public void startPreviewActivity(Intent intent, View view) {
		startPreviewActivity(intent, view ,-654);
	}
	
	public void startPreviewActivity(Intent intent, View view, int requestCode) {
		if (view != null) {
			Rect bounds = new Rect();
			bounds.set(Position.getGlobalVisibleRect(view));
			intent.putExtra("cx", bounds.centerX());
			intent.putExtra("cy", bounds.centerY());
		}
		startPreviewActivity(intent, requestCode);
	}

	private void startPreviewActivity(final Intent intent, final int requestCode) {
		if (requestCode == -654) {
			startActivity(intent);
		} else {
			startActivityForResult(intent, requestCode);
		}
	}
	public void addFragment(int containerId, BaseFragment fragment) {
		getSupportFragmentManager().beginTransaction().add(containerId, fragment).commitAllowingStateLoss();
	}

	public Fragment replaceFragment(int containerId, Class<? extends Fragment> newFragment) {
		return FragmentUtils.replaceFragment(getSupportFragmentManager(), containerId, newFragment, null);
	}

	public Fragment switchFragment(int containerId, Class<? extends Fragment> newFragment, Bundle args) {
		return FragmentUtils.switchFragment(getSupportFragmentManager(), containerId, newFragment, args, false);
	}

	@Override
	public void onConnect(netType type) {
	}

	@Override
	public void onDisConnect() {
		CustomDialog.confirm(getString(R.string.dialog_checknet), getString(R.string.confirm), getString(R.string.cancel),
				new OnConfirmListener() {
					@Override
					public void onConfirmListener() {
						if ((android.os.Build.VERSION.SDK_INT) >= Build.VERSION_CODES.HONEYCOMB) {
							Intent intent = new Intent("android.settings.SETTINGS");
							startActivity(intent);
						} else {
							Intent intentToNetwork = new Intent("/");
							ComponentName componentName = new ComponentName("com.android.settings",
									"com.android.settings.WirelessSettings");
							intentToNetwork.setComponent(componentName);
							intentToNetwork.setAction("android.intent.action.VIEW");
							startActivity(intentToNetwork);
						}
					}
				}, null);
	}

	@Override
	protected void onDestroy() {
		ActivityManager.getInstance().onDestroy(this);
		super.onDestroy();
	}

	@Override
	public void finish() {
		if (!mIsExitApp && mLayout != null && getIntent().hasExtra("cx")) {
			mLayout.pop();
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				BaseActivity.super.finish();
			}
		}, mDuration);
	}

	/**
	 * 返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mIsExitApp) {
				exitApp();
			} else
				finish();
		}
		return false;
	}

	/**
	 * 两次触摸返回键间隔小于2s，完全退出APP
	 */
	private void exitApp() {
		if (System.currentTimeMillis() - mExitTime > 2000) {
			SDToast.showToast(getString(R.string.dialog_exitapp));
		} else {
			App.getApplication().exitApp(true);
		}
		mExitTime = System.currentTimeMillis();
	}

}
