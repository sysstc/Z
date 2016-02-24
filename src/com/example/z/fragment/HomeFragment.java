package com.example.z.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.z.R;
import com.example.z.photo.PhotoActivity;
import com.example.z.zxing.CaptureActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeFragment extends BaseFragment{

	@ViewInject(R.id.ll)
	private LinearLayout mLl;
	
	@ViewInject(R.id.single)
	private Button single;
	
	@ViewInject(R.id.more)
	private Button more;
	
	@ViewInject(R.id.code)
	private Button code;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		ViewUtils.inject(this, view);
		
		single.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getMainActivity(), PhotoActivity.class);
				intent.putExtra("IsSingle", true);
				getActivity().startActivityForResult(intent, 0);
			}
		});
		
		more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseActivity(), PhotoActivity.class);
				intent.putExtra("IsSingle", false);
				getActivity().startActivityForResult(intent, 1);
			}
		});
		
		code.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseActivity(), CaptureActivity.class);
				getActivity().startActivityForResult(intent, 2);
			}
		});
		
		return view;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mLl.removeAllViews();
		if (resultCode == PhotoActivity.PHOTO_RESULTCODE) {
			if (requestCode == 0) {
				try {
					ImageView iv = new ImageView(getActivity());
					FileInputStream fis = new FileInputStream(new File(data.getStringExtra("result")));
					Bitmap b = BitmapFactory.decodeStream(fis);
					iv.setImageBitmap(b);
					mLl.addView(iv);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (requestCode == 1) {
				List<String> paths = data.getStringArrayListExtra("result");
				for (String string : paths) {
					try {
						ImageView iv = new ImageView(getActivity());
						FileInputStream fis = new FileInputStream(new File(string));
						Bitmap b = BitmapFactory.decodeStream(fis);
						iv.setImageBitmap(b);
						mLl.addView(iv);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (resultCode == CaptureActivity.CAPTURE_RESULTCODE) {
			if (requestCode == 2) {
				TextView tv = new TextView(getActivity());
				String result = data.getStringExtra("result");
				if (TextUtils.isEmpty(result)) {
					result = "无识别结果，请重新扫描";
				}
				tv.setText(result);
				mLl.addView(tv);
			}
		}
	}
	
}
