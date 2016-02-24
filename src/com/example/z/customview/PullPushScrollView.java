package com.example.z.customview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class PullPushScrollView extends ScrollView {

	private ViewGroup mChild;
	private ViewGroup mChildHeader;
	private WaveLayout mWaveLayout;

	private int mOriginalHeaderHeight;

	private float mLastY = 0;
	private float deltaY = -1;
	
	private ObjectAnimator oa;

	private OnSlideListenre mOnSlideListenre;
	
	public PullPushScrollView(Context context) {
		this(context, null);
	}

	public PullPushScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PullPushScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		setVerticalScrollBarEnabled(false);
		initView();
	}

	private void initView() {
		mChild = (ViewGroup) getChildAt(0);
		mChildHeader = (ViewGroup) mChild.getChildAt(0);
		mChildHeader.post(new Runnable() {

			@Override
			public void run() {
				mOriginalHeaderHeight = mChildHeader.getHeight();
			}
		});
		mWaveLayout = (WaveLayout) mChild.getChildAt(1);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getY();
			deltaY = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			if (getScrollY() != 0) {
				deltaY = 0;
				mLastY = ev.getY();
			} else {
				deltaY = ev.getY() - mLastY;
				// 下滑
				setDown((int) -deltaY / 5);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mWaveLayout != null) {
						mWaveLayout.stopWave();
					}
				}
			}, 150);
			if(deltaY >= 1f){
				reset();
				if (mOnSlideListenre != null && deltaY>mOriginalHeaderHeight/2) {
					mOnSlideListenre.onRefresh();
				}
				return false;
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (t > mOriginalHeaderHeight) {
			return;
		}
		ViewHelper.setTranslationY(mChildHeader, t / 2);

		float percent = (float) t / mOriginalHeaderHeight;
		if (percent > 1) {
			percent = 1;
		}

		if (mOnSlideListenre != null) {
			mOnSlideListenre.onSlide(percent);
		}
	}

	public void setDown(int t) {
		scrollTo(0, t);
		if (t < 0) {
			mChildHeader.getLayoutParams().height = mOriginalHeaderHeight - t;
			mChild.getLayoutParams().height = mOriginalHeaderHeight - t;
			mChild.requestLayout();
			if (mWaveLayout != null && !mWaveLayout.isStopWave) {
				mWaveLayout.startWave();
			}
		}
	}

	private void reset() {
		if (oa != null && oa.isRunning()) {
			return;
		}
		oa = ObjectAnimator.ofInt(this, "down", (int) -deltaY / 5, 0);
		oa.setDuration(150);
		oa.start();
		
	}

	public void setOnSlideListenre(OnSlideListenre l) {
		mOnSlideListenre = l;
	}

	public interface OnSlideListenre {
		public void onSlide(float percent);

		public void onRefresh();
	}
}
