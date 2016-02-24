package com.example.z.customview.tab;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

public class SimpleTabViewManager {

	private SimpleTabView[] mItems = null;

	private int mCurrentIndex = -1;

	private int mLastIndex = 0;

	private SimpleTabViewManagerListener mListener = null;

	public int getmCurrentIndex() {
		return mCurrentIndex;
	}

	public void setmListener(SimpleTabViewManagerListener mListener) {
		this.mListener = mListener;
	}

	public void setItems(SimpleTabView[] items) {
		if (items != null && items.length > 0) {
			mItems = items;
			for (int i = 0; i < mItems.length; i++) {
				mItems[i].setId(i);
				mItems[i].setOnClickListener(new SimpleTabView_listener());
				mItems[i].normal();
			}
		}
	}

	class SimpleTabView_listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			setSelectIndex(v.getId(), v, true);
		}

	}

	public boolean setSelectIndex(int index, View v, boolean notifyListener) {
		if (mItems != null && mItems.length > 0 && index < mItems.length) {
			if (index != mCurrentIndex) {
				mItems[index].select();
				if (mCurrentIndex != -1) {
					mItems[mCurrentIndex].normal();
				}
				mLastIndex = mCurrentIndex;
				mCurrentIndex = index;
				if (mListener != null && notifyListener) {
					mListener.onItemClick(v, index);
				}
				return true;
			}
		}
		return false;
	}

	public void setViewPager(ViewPager vp) {
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setSelectIndex(arg0, null, false);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	public interface SimpleTabViewManagerListener {
		public void onItemClick(View v, int index);
	}
}
