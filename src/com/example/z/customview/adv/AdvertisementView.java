package com.example.z.customview.adv;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.z.R;
import com.example.z.utils.ABViewBinder;

public class AdvertisementView extends RelativeLayout {

	private Context mContext;

	public LoopViewPager mLoopViewPager;

	private LinearLayout mNavLayoutParent;

	private Bitmap mDisplayImage, mHideImage;

	private int mCount;

	public AdvertisementView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public AdvertisementView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public AdvertisementView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {

		mLoopViewPager = new LoopViewPager(mContext);
		RelativeLayout.LayoutParams vplp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		vplp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		addView(mLoopViewPager, vplp);

		mNavLayoutParent = new LinearLayout(mContext);
		mNavLayoutParent.setGravity(Gravity.CENTER_HORIZONTAL);
		mNavLayoutParent.setOrientation(LinearLayout.HORIZONTAL);
		RelativeLayout.LayoutParams lllp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lllp.setMargins(10, 10, 10, 45);
		addView(mNavLayoutParent, lllp);

		mDisplayImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_homefrag_dot_foused);
		mHideImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_homefrag_dot_normal);

	}

	public void setImageUris(ArrayList<String> uris) {
		mNavLayoutParent.removeAllViews();
		mCount = uris.size();
		for (int i = 0; i < mCount; i++) {
			ImageView imageView = new ImageView(mContext);
			imageView.setPadding(10, 10, 10, 10);
			imageView.setImageBitmap(i == 0 ? mHideImage : mDisplayImage);
			mNavLayoutParent.addView(imageView, i);
		}

		mLoopViewPager.setAdapter(new MyAdapter(mContext, uris));
		mLoopViewPager.setBoundaryCaching(true);
		mLoopViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				makesurePosition();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	public void makesurePosition() {
		int position = mLoopViewPager.getCurrentItem();
		for (int j = 0; j < mCount; j++) {
			if (position % mCount == j) {
				((ImageView) mNavLayoutParent.getChildAt(position % mCount)).setImageBitmap(mHideImage);
			} else {
				((ImageView) mNavLayoutParent.getChildAt(j)).setImageBitmap(mDisplayImage);
			}
		}
	}

	private class MyAdapter extends PagerAdapter {

		private ArrayList<String> mAdList = new ArrayList<String>();
		private ArrayList<View> mAdView = new ArrayList<View>();
		private Context mContext;

		public MyAdapter(Context context, ArrayList<String> adList) {
			this.mContext = context;
			this.mAdList = adList;
		}

		@Override
		public int getCount() {
			return mAdList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final String imageUrl = mAdList.get(position);
			ImageView view = new ImageView(mContext);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mOnItemClickListener != null){
						mOnItemClickListener.OnItem(position);
					}
				}
			});
			view.setScaleType(ScaleType.CENTER_CROP);
			ABViewBinder.setImageView(view, imageUrl);
			mAdView.add(view);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		}

	}

	private OnAdvertisementItemClickListener mOnItemClickListener;

	public void setOnAdvertisementItemClickListener(OnAdvertisementItemClickListener i) {
		mOnItemClickListener = i;
	}

	public interface OnAdvertisementItemClickListener {
		void OnItem(int position);
	}

	public void onResume() {
		if (mLoopViewPager != null) {
			mLoopViewPager.startImageTimerTask();
		}
	}

	public void onStop() {
		if (mLoopViewPager != null) {
			mLoopViewPager.stopImageTimerTask();
		}
	}
}
