package com.example.z.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.example.z.app.App;
import com.example.z.customview.CustomDialog;
import com.example.z.customview.CustomDialog.OnConfirmListener;
import com.example.z.utils.ActivityManager;

/**
 * 捕获全局异常,因为有的异常我们捕获不到
 * 
 * @author river
 * 
 */
public class UncaughtException implements UncaughtExceptionHandler {
	private final static String TAG = "UncaughtException";
	private static UncaughtException mUncaughtException;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	private UncaughtException() {
	}

	/**
	 * 同步方法，以免单例多线程环境下出现异常
	 * 
	 * @return
	 */
	public synchronized static UncaughtException getInstance() {
		if (mUncaughtException == null) {
			mUncaughtException = new UncaughtException();
		}
		return mUncaughtException;
	}

	/**
	 * 初始化，把当前对象设置成UncaughtExceptionHandler处理器
	 */
	public void init() {
		Thread.setDefaultUncaughtExceptionHandler(mUncaughtException);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		// 处理异常,我们还可以把异常信息写入文件，以供后来分析。
		saveCrashInfo2File(ex);
		Log.e(TAG, "uncaughtException thread : " + thread + "||name=" + thread.getName() + "||id=" + thread.getId()
				+ "||exception=" + ex);
		/*
		 * Looper.prepare(); Toast.makeText(context, "程序异常，立即退出", 1).show();
		 * System.exit(0); Looper.loop();
		 */

		showDialog();
	}

	private void showDialog() {
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				CustomDialog.excetion(new OnConfirmListener() {
					@Override
					public void onConfirmListener() {
						ActivityManager.getInstance().finishAllActivity();
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(0);
					}
				});
				Looper.loop();
			}
		}.start();
	}

	/**
	 * 保存错误信息到文件中 *
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();

		long timestamp = System.currentTimeMillis();
		String time = formatter.format(new Date());
		sb.append("\n" + time + "----");
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		try {

			String fileName = "exception.log";

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash"
						+ File.separator + App.getApplication().getPackageName() + File.separator;
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName, true);
				fos.write(sb.toString().getBytes());
				fos.close();
			}

			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}

		return null;
	}
}
