package com.example.z.zxing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.z.R;
import com.example.z.photo.PhotoActivity;
import com.example.z.zxing.CropPopupWindow.OnConfirmListener;
import com.example.z.zxing.bitmap.SaveBitmap;
import com.example.z.zxing.camera.CameraManager;
import com.example.z.zxing.decode.DecodeUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;

@SuppressWarnings("deprecation")
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	public static int CAPTURE_RESULTCODE = 655;
	private Camera mCamera;
	private CameraManager mCameraManager;

	private BeepManager beepManager;

	private View mV;
	private SurfaceView scanPreview;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;
	private RadioGroup captureModeGroup;
	private TextView photo;
	private ImageView back;

	private int mQrcodeCropWidth = 0;
	private int mQrcodeCropHeight = 0;
	private int mBarcodeCropWidth = 0;
	private int mBarcodeCropHeight = 0;

	private SurfaceHolder mHolder;

	private Rect mCropRect = null;
	private int dataMode = DecodeUtils.DECODE_DATA_MODE_QRCODE;

	public BeepManager getBeepManager() {
		return beepManager;
	}

	private TranslateAnimation mAnimation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		findViewById();
	}

	private void findViewById() {
		mV = (View) findViewById(R.id.id_v);
		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
		captureModeGroup = (RadioGroup) findViewById(R.id.capture_mode_group);
		photo = (TextView) findViewById(R.id.photo);
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CaptureActivity.this, PhotoActivity.class);
				intent.putExtra("IsSingle", true);
				startActivityForResult(intent, 0);
			}
		});
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		captureModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.capture_mode_barcode) {
					setCodeMode(mBarcodeCropWidth, mBarcodeCropHeight, mQrcodeCropWidth, mQrcodeCropHeight,
							DecodeUtils.DECODE_DATA_MODE_BARCODE);
				} else if (checkedId == R.id.capture_mode_qrcode) {
					setCodeMode(mQrcodeCropWidth, mQrcodeCropHeight, mBarcodeCropWidth, mBarcodeCropHeight,
							DecodeUtils.DECODE_DATA_MODE_QRCODE);
				}
			}
		});

		mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.85f);
		mAnimation.setDuration(2500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		scanLine.startAnimation(mAnimation);

		mQrcodeCropWidth = getResources().getDimensionPixelSize(R.dimen.qrcode_crop_width);
		mQrcodeCropHeight = getResources().getDimensionPixelSize(R.dimen.qrcode_crop_height);
		mBarcodeCropWidth = getResources().getDimensionPixelSize(R.dimen.barcode_crop_width);
		mBarcodeCropHeight = getResources().getDimensionPixelSize(R.dimen.barcode_crop_height);
		
		mHolder = scanPreview.getHolder();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHolder.addCallback(CaptureActivity.this);
	}

	private void setCodeMode(int w, int h, final int ow, final int oh, final int dataMode) {
		PropertyValuesHolder qr2barWidthVH = PropertyValuesHolder.ofFloat("width", 1.0f, (float) w / ow);
		PropertyValuesHolder qr2barHeightVH = PropertyValuesHolder.ofFloat("height", 1.0f, (float) h / oh);
		ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(qr2barWidthVH, qr2barHeightVH);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Float fractionW = (Float) animation.getAnimatedValue("width");
				Float fractionH = (Float) animation.getAnimatedValue("height");
				LayoutParams parentLayoutParams = (LayoutParams) scanCropView.getLayoutParams();
				parentLayoutParams.width = (int) (ow * fractionW);
				parentLayoutParams.height = (int) (oh * fractionH);
				scanCropView.setLayoutParams(parentLayoutParams);
			}
		});
		valueAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				initCrop();
				setDataMode(dataMode);
				scanLine.startAnimation(mAnimation);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		valueAnimator.start();
	}

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = mCameraManager.getCameraResolution().y;
		int cameraHeight = mCameraManager.getCameraResolution().x;
		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);
		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();
		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();
		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();
		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;
		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;
		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	public void setDataMode(int dataMode) {
		this.dataMode = dataMode;
	}

	private Camera getCamera() {
		mCameraManager = new CameraManager(this);
		try {
			mCameraManager.openDriver();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mCameraManager.getCamera();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 开始预览相机内容
	 * 
	 * @param camera
	 * @param holder
	 */
	private void setStartPreview(Camera camera, SurfaceHolder holder) {
		try {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPictureFormat(PixelFormat.JPEG);
			if ((android.os.Build.VERSION.SDK_INT) >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
				camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
			} else {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			camera.setParameters(parameters);
			camera.setPreviewDisplay(holder);
			// 将系统Camera预览角度进行调整
			camera.setDisplayOrientation(90);
			camera.startPreview();
			camera.setPreviewCallback(previewCb);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void autoFocus() {
		if (mCamera != null) {
			mCamera.cancelAutoFocus();
			mCamera.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							autoFocus();
						}
					}, 100);
				}
			});
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setStartPreview(mCamera, mHolder);
		autoFocus();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera.stopPreview();
		setStartPreview(mCamera, mHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Size size = camera.getParameters().getPreviewSize();
			// 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
			byte[] rotatedData = new byte[data.length];
			for (int y = 0; y < size.height; y++) {
				for (int x = 0; x < size.width; x++)
					rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
			}
			// 宽高也要调整
			int tmp = size.width;
			size.width = size.height;
			size.height = tmp;
			initCrop();
			String result = new DecodeUtils(dataMode).decodeWithZxing(rotatedData, size.width, size.height, mCropRect);
			if (!TextUtils.isEmpty(result)) {
				new SaveBitmap().save(rotatedData, size, mCropRect);
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				beepManager.playBeepSoundAndVibrate();
				Intent intent = new Intent();
				intent.putExtra("result", result);
				setResult(CaptureActivity.CAPTURE_RESULTCODE, intent);
				finish();
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (mCamera == null) {
			mCamera = getCamera();
			if (mHolder != null) {
				setStartPreview(mCamera, mHolder);
			}
		}
		if (beepManager == null) {
			beepManager = new BeepManager(this);
		}
		beepManager.updatePrefs();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
		beepManager.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();
		beepManager.close();
	}

	public void photo(View view) {
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		Intent intent = new Intent(CaptureActivity.this, PhotoActivity.class);
		intent.putExtra("IsSingle", true);
		startActivityForResult(intent, 0);
	}

	public void again(View view) {
		mCamera.setPreviewCallback(previewCb);
		mCamera.startPreview();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 654) {
			if (requestCode == 0) {
				CropPopupWindow cp = null;
				try {
					FileInputStream fis = new FileInputStream(new File(data.getStringExtra("result")));
					Bitmap bitmap = BitmapFactory.decodeStream(fis);
					cp = new CropPopupWindow(this, bitmap);
					cp.setAnimationStyle(R.style.dir_popupwindow_anim);
					cp.showAsDropDown(mV, 0, 0);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				cp.setOnConfirmListener(new OnConfirmListener() {

					@Override
					public void onConfirm(String s) {
						Intent intent = new Intent();
						intent.putExtra("result", s);
						setResult(CaptureActivity.CAPTURE_RESULTCODE, intent);
						beepManager.playBeepSoundAndVibrate();
						finish();
					}
				});
			}
		}
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
