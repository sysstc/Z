package com.example.z.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.z.R;
import com.example.z.app.App;

/**
 * toast显示类，可以在子线程直接调用
 * 
 */
public class SDToast {
	private static Toast toast;

	public static Handler mHandler = new Handler(Looper.getMainLooper());

	public static void showToast(CharSequence text) {
		showToast(text, Toast.LENGTH_SHORT);
	}

	public static void showToast(final CharSequence text, final int duration) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			show(text, duration);
		} else {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					show(text, duration);
				}
			});
		}
	}

	private static void show(CharSequence text, int duration) {
		if (toast != null) {
			toast.cancel();
		}
		View layout = LayoutInflater.from(App.getApplication()).inflate(R.layout.view_toast, null);
		TextView content = (TextView) layout.findViewById(R.id.view_toast_tv_content);
		content.setText(text);
		toast = new Toast(App.getApplication());
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
		// toast = Toast.makeText(App.getApplication(), text, duration);
		// toast.show();

	}
}
