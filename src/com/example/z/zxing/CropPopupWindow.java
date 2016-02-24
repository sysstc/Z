package com.example.z.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.z.R;
import com.example.z.zxing.decode.DecodeUtils;
import com.example.z.zxing.view.CropImageView;

public class CropPopupWindow extends PopupWindow {

	private View mConvertView;
	private CropImageView mView;
	private TextView mConfirm;
	private ImageView mBack;

	public CropPopupWindow(Context context, Bitmap bitmap) {

		mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_crop, null);

		setContentView(mConvertView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);

		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());

		mBack = (ImageView) mConvertView.findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		mConfirm = (TextView) mConvertView.findViewById(R.id.confirm);
		mConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 调用该方法得到剪裁好的图片
				if (mView != null && mOnConfirmListener != null) {
					Bitmap bitmap = mView.getCropImage();
					String result = new DecodeUtils(DecodeUtils.DECODE_DATA_MODE_ALL).decodeWithZxing(bitmap);
					mOnConfirmListener.onConfirm(result);
					dismiss();
				}
			}
		});
		mView = (CropImageView) mConvertView.findViewById(R.id.cropimage);
		// 设置资源和默认长宽
		mView.setDrawable(Bitmap2Drawable(bitmap), 250, 250);
	}

	public static Drawable Bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		return bd;
	}

	private OnConfirmListener mOnConfirmListener;

	public void setOnConfirmListener(OnConfirmListener o) {
		mOnConfirmListener = o;
	}

	public interface OnConfirmListener {
		void onConfirm(String s);
	}
}
