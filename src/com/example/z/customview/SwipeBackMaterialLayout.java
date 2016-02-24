package com.example.z.customview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * 滑动结束activity
 */
public class SwipeBackMaterialLayout extends RelativeLayout {

	private int radius = 0;
	private int cx = 0;
	private int cy = 0;
	private int mDuration = 0;
	private static int ANIMATION_TIME = 300;
	private View mContentView;
	private int mTouchSlop;
	private int downX;
	private int downY;
	private Scroller mScroller;
	private int viewWidth, viewHeight;
	private boolean isSilding;
	private boolean isSwipeBackLayout = true;
	private boolean isFinish;
	private List<ViewPager> mViewPagers = new LinkedList<ViewPager>();

	private GestureDetectorCompat mGestureDetector;
	private OnGestureListener mGestureListener;
	private boolean isFlingVelocity;

	private List<View> mListIgnoredViews = new ArrayList<View>();
	private int slidingArea = 0;
	
	private Activity mActivity;

	public void setSwipeBackLayout(boolean isSwipeBackLayout) {
		this.isSwipeBackLayout = isSwipeBackLayout;
	}

	public SwipeBackMaterialLayout(Context context) {
		this(context, null);
	}

	public SwipeBackMaterialLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeBackMaterialLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
		mGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				isFlingVelocity = false;
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if (velocityX >= 2500) {
					isFlingVelocity = true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		};
		mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		ViewPager mViewPager = getTouchViewPager(mViewPagers, ev);

		if (mViewPager != null && mViewPager.getCurrentItem() != 0) {
			return super.onInterceptTouchEvent(ev);
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) ev.getRawX();
			downY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getRawX();
			if (!isInIgnoredView(ev) && moveX - downX > mTouchSlop && Math.abs((int) ev.getRawY() - downY) < mTouchSlop
					&& isSwipeBackLayout) {
				return true;
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mGestureDetector.onTouchEvent(event);
			break;
		case MotionEvent.ACTION_MOVE:
			mGestureDetector.onTouchEvent(event);
			int moveX = (int) event.getRawX();

			if (moveX - downX > mTouchSlop && Math.abs((int) event.getRawY() - downY) < mTouchSlop && isSwipeBackLayout) {
				isSilding = true;
			}

			int distance = 0;

			if (moveX - downX >= 20 && isSilding) {
				distance = moveX - downX;
				mContentView.scrollTo(-(moveX - downX), 0);
			} else {
				mContentView.scrollTo(-distance, 0);
			}

			break;
		case MotionEvent.ACTION_UP:
			mGestureDetector.onTouchEvent(event);
			isSilding = false;
			if (mContentView.getScrollX() <= -viewWidth / 3 || isFlingVelocity && isSwipeBackLayout) {
				isFinish = true;
				scrollRight();
			} else {
				scrollOrigin();
				isFinish = false;
			}
			break;
		}

		return true;
	}

	private void getAlLViewPager(List<ViewPager> mViewPagers, ViewGroup parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);
			if (child instanceof ViewPager) {
				mViewPagers.add((ViewPager) child);
			} else if (child instanceof ViewGroup) {
				getAlLViewPager(mViewPagers, (ViewGroup) child);
			}
		}
	}

	private ViewPager getTouchViewPager(List<ViewPager> mViewPagers, MotionEvent ev) {
		if (mViewPagers == null || mViewPagers.size() == 0) {
			return null;
		}
		Rect mRect = new Rect();
		for (ViewPager v : mViewPagers) {
			v.getHitRect(mRect);

			if (mRect.contains((int) ev.getX(), (int) ev.getY())) {
				return v;
			}
		}
		return null;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			viewWidth = this.getWidth();
			viewHeight = this.getHeight();
			getAlLViewPager(mViewPagers, this);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		RectF rectF = new RectF(getLeft() - 36, 0, getLeft(), viewHeight);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		LinearGradient linearGradient = new LinearGradient(getLeft() - 30, 0, getLeft(), 0, 0x00000000, 0x2b585e5d,
				TileMode.CLAMP);
		paint.setShader(linearGradient);
		paint.setStyle(Style.FILL);
		canvas.drawRect(rectF, paint);

	}

	private void scrollRight() {
		final int delta = (viewWidth + mContentView.getScrollX());
		mScroller.startScroll(mContentView.getScrollX(), 0, -delta + 1, 0, ANIMATION_TIME);
		postInvalidate();
	}

	private void scrollOrigin() {
		int delta = mContentView.getScrollX();
		mScroller.startScroll(mContentView.getScrollX(), 0, -delta, 0, ANIMATION_TIME);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			mContentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			if (mScroller.isFinished() && isFinish) {
				if (mOnFinishListener != null) {
					mOnFinishListener.onFinish();
				}
			}
		}
	}
	
	private OnFinishListener mOnFinishListener;
	
	public void setOnFinishListener(OnFinishListener f){
		mOnFinishListener = f;
	}
	
	public interface OnFinishListener{
		void  onFinish();
	}

	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : mListIgnoredViews) {
			v.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX() - slidingArea, (int) ev.getY())) {
				return true;
			}
		}
		return false;
	}

	public void addIgnoredView(View v, int slidingArea) {
		this.slidingArea = slidingArea;
		if (!mListIgnoredViews.contains(v)) {
			mListIgnoredViews.add(v);
		}
	}

	public void removeIgnoredView(View v) {
		mListIgnoredViews.remove(v);
	}

	public void clearIgnoredViews() {
		mListIgnoredViews.clear();
	}

	public void attachToActivity(Activity activity) {
		mActivity = activity;
		TypedArray a = activity.getTheme().obtainStyledAttributes(new int[] { android.R.attr.windowBackground });
		int background = a.getResourceId(0, 0);
		a.recycle();

		ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
		ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
		decorChild.setBackgroundResource(background);
		decor.removeView(decorChild);
		addView(decorChild);
		setContentView(decorChild);
		decor.addView(this);
	}

	private void setContentView(View decorChild) {
		mContentView = (View) decorChild.getParent();
	}

	public void push(View view, int duration) {
		if (view != null) {
			cx = view.getLeft() + view.getWidth() / 2;
			cy = view.getTop() + view.getHeight() / 2;
			mDuration = duration;
		}
		int value = Math.max(cx, getWidth() - cx) + Math.max(cy, getHeight() - cy);
		ValueAnimator animator = ValueAnimator.ofInt(0, value);
		animator.setDuration(mDuration);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				radius = (Integer) animation.getAnimatedValue();
				invalidate(new Rect(cx - radius, cy - radius, cx + radius, cy + radius));
			}
		});
		animator.start();
	}

	public void push(int x, int y, int duration) {
		if (x != 0 && y != 0) {
			cx = x;
			cy = y;
			mDuration = duration;
		} else {
			cx = getWidth() / 2;
			cy = getHeight() / 2;
			mDuration = 0;
		}

		int value = Math.max(cx, getWidth() - cx) + Math.max(cy, getHeight() - cy);
		ValueAnimator animator = ValueAnimator.ofInt(0, value);
		animator.setDuration(mDuration);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				radius = (Integer) animation.getAnimatedValue();
				invalidate(new Rect(cx - radius, cy - radius, cx + radius, cy + radius));
			}
		});
		animator.start();
	}

	public void pop() {
		ValueAnimator animator = ValueAnimator.ofInt(radius, 0);
		animator.setDuration(mDuration);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				radius = (Integer) animation.getAnimatedValue();
				invalidate();
			}
		});
		animator.start();
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		Path path = new Path();
		path.addCircle(cx, cy, radius, Path.Direction.CW);
		canvas.clipPath(path);
		return super.drawChild(canvas, child, drawingTime);
	}
}
