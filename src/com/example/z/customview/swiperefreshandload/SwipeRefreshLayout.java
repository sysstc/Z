/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.z.customview.swiperefreshandload;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The SwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The SwipeRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to show just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and
 * progress animation, call setEnabled(false) on the view.
 * <p>
 * This layout should be made the parent of the view that will be refreshed as a
 * result of the gesture and can only support one direct child. This view will
 * also be made the target of the gesture and will be forced to match both the
 * width and the height supplied in this layout. The SwipeRefreshLayout does not
 * provide accessibility events; instead, a menu item must be provided to allow
 * refresh of the content wherever this gesture is used.
 * </p>
 */

public class SwipeRefreshLayout extends ViewGroup {

	private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();
	private static final long RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 500;
	private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
	private static final int PROGRESS_BAR_HEIGHT = 5;
	private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
	private static final int REFRESH_TRIGGER_DISTANCE = 150;
	private static final int INVALID_POINTER = -1;
	/**
	 * 触发控件移动距离
	 */
	private int mTouchSlop;
	/**
	 * 动画持续时间
	 */
	private int mMediumAnimationDuration;
	/**
	 * the thing that shows progress is going
	 */
	private SwipeProgressBar mProgressBar;
	private int mProgressBarHeight;
	private final DecelerateInterpolator mDecelerateInterpolator;
	private final AccelerateInterpolator mAccelerateInterpolator;
	private int mCurrentTargetOffsetTop;
	/**
	 * the content that gets pulled down
	 */
	private View mTarget;
	/**
	 * 初始头部偏移量
	 */
	private int mOriginalOffsetTop;
	private int mBeforeNum = 5;
	private float mDistanceToTriggerSync = -1;
	/**
	 * Target is returning to its start offset because it was cancelled or a
	 * refresh was triggered.
	 */
	private boolean mReturningToStart;
	/**
	 * 初始Y值
	 */
	private float mInitialMotionY;
	private int mActivePointerId = INVALID_POINTER;
	/**
	 * 是否存在拖动
	 */
	private boolean mIsBeingDragged;
	/**
	 * 当前百分比
	 */
	private float mCurrPercentage = 0;
	/**
	 * 当子控件移动到尽头时才开始计算初始点的位置
	 */
	private float mStartPoint;
	private boolean up;
	private boolean down;
	/**
	 * 数据不足一屏时是否打开上拉加载模式
	 */
	private boolean loadNoFull = true;
	private Mode mMode = Mode.PULL_FROM_START;
	/**
	 * 之前手势的方向，为了解决同一个触点前后移动方向不同导致后一个方向会刷新的问题，
	 * 这里Mode.DISABLED无意义，只是一个初始值，和上拉/下拉方向进行区分
	 */
	private Mode mLastDirection = Mode.DISABLED;
	/**
	 * 是否自动加载
	 */
	private boolean mIsAutoLoad = true;
	private int mDirection = 0;
	private boolean mStopLoading = false;
	private float mFromPercentage = 0;
	private int mFrom;
	private boolean mRefreshing = false;
	private boolean mLoading = false;
	private OnRefreshListener mRefreshListener;
	private boolean mIsScroll = true;
	private LinearLayout mFooterView;
	private TextView mTvFooterView;

	public SwipeRefreshLayout(Context context) {
		this(context, null);
	}

	public SwipeRefreshLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
		setWillNotDraw(false);
		mProgressBar = new SwipeProgressBar(this);
		mProgressBarHeight = dp2px(PROGRESS_BAR_HEIGHT);
		mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
		mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);
		initFootView(context);
	}

	private void initFootView(Context context) {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(70));
		LayoutParams ap = new LayoutParams(dp2px(35), dp2px(35));
		mFooterView = new LinearLayout(context);
		mFooterView.setLayoutParams(lp);
		mFooterView.setOrientation(LinearLayout.HORIZONTAL);
		mFooterView.setGravity(Gravity.CENTER);
		AVLoadingIndicatorView av = new AVLoadingIndicatorView(context);
		av.setLayoutParams(ap);
		mTvFooterView = new TextView(context);
		mTvFooterView.setText("正在加载...");
		mTvFooterView.setTextColor(0xffB5B5B5);
		mTvFooterView.setTextSize(12);
		mFooterView.addView(av);
		mFooterView.addView(mTvFooterView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		mProgressBar.setBounds(0, 0, width, mProgressBarHeight);
		if (getChildCount() == 0) {
			return;
		}
		final View child = getChildAt(0);
		final int childLeft = getPaddingLeft();
		final int childTop = mCurrentTargetOffsetTop + getPaddingTop();
		final int childWidth = width - getPaddingLeft() - getPaddingRight();
		final int childHeight = height - getPaddingTop() - getPaddingBottom();
		child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getChildCount() > 1 && !isInEditMode()) {
			throw new IllegalStateException("SwipeRefreshLayout can host only one direct child");
		}
		if (getChildCount() > 0) {
			getChildAt(0).measure(
					MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		mProgressBar.draw(canvas);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		removeCallbacks(mCancel);
		removeCallbacks(mReturnToStartPosition);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		removeCallbacks(mReturnToStartPosition);
		removeCallbacks(mCancel);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		ensureTarget();
		final int action = MotionEventCompat.getActionMasked(ev);
		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}
		if (!isEnabled() || mReturningToStart) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			mStartPoint = mInitialMotionY;
			// 这里用up/down记录子控件能否下拉，如果当前子控件不能上下滑动，但当手指按下并移动子控件时，控件就会变得可滑动
			// 后面的一些处理不能直接使用canChildScrollUp/canChildScrollDown
			// 但仍存在问题：当数据不满一屏且设置可以上拉模式后，多次快速上拉会激发上拉加载
			up = canChildScrollUp();
			down = canChildScrollDown();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mActivePointerId == INVALID_POINTER) {
				Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
				return false;
			}
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if (pointerIndex < 0) {
				Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
				return false;
			}
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float yDiff = y - mStartPoint;
			if ((mLastDirection == Mode.PULL_FROM_START && yDiff < 0) || (mLastDirection == Mode.PULL_FROM_END && yDiff > 0)) {// 若上个手势的方向和当前手势方向不一致，返回
				return false;
			}
			// 下拉或上拉时，子控件本身能够滑动时，记录当前手指位置，当其滑动到尽头时，
			// mStartPoint作为下拉刷新或上拉加载的手势起点
			if ((canChildScrollUp() && yDiff > 0) || (canChildScrollDown() && yDiff < 0)) {
				mStartPoint = y;
			}
			if (yDiff > mTouchSlop) {// 下拉
				if (canChildScrollUp() || mLastDirection == Mode.PULL_FROM_END) {// 若当前子控件能向下滑动，或者上个手势为上拉，则返回
					mIsBeingDragged = false;
					return false;
				}
				if (mMode == Mode.PULL_FROM_START) {
					mIsBeingDragged = true;
					mLastDirection = Mode.PULL_FROM_START;
				}
			} else if (-yDiff > mTouchSlop) {// 上拉
				if (canChildScrollDown() || mLastDirection == Mode.PULL_FROM_START) {// 若当前子控件能向上滑动，或者上个手势为下拉，则返回
					mIsBeingDragged = false;
					return false;
				}
				if (!up && !down && !loadNoFull) {// 若子控件不能上下滑动，说明数据不足一屏，若不满屏不加载，返回
					mIsBeingDragged = false;
					return false;
				}
				if (mMode == Mode.PULL_FROM_END) {
					mIsBeingDragged = true;
					mLastDirection = Mode.PULL_FROM_END;
				}
			}
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			mActivePointerId = INVALID_POINTER;
			mLastDirection = Mode.DISABLED;
			break;
		}
		return mIsBeingDragged;
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean b) {// Nope.
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);
		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}
		if (!isEnabled() || mReturningToStart) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			mStartPoint = mInitialMotionY;
			up = canChildScrollUp();
			down = canChildScrollDown();
			break;
		case MotionEvent.ACTION_MOVE:
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if (pointerIndex < 0) {
				Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
				return false;
			}
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float yDiff = y - mStartPoint;
			if ((mLastDirection == Mode.PULL_FROM_START && yDiff < 0) || (mLastDirection == Mode.PULL_FROM_END && yDiff > 0)) {
				return true;
			}
			if (!mIsBeingDragged && (yDiff > 0 && mLastDirection == Mode.PULL_FROM_START)
					|| (yDiff < 0 && mLastDirection == Mode.PULL_FROM_END)) {
				mIsBeingDragged = true;
			}
			if (mIsBeingDragged) {
				// User velocity passed min velocity; trigger a refresh
				if (yDiff > mDistanceToTriggerSync) {
					// User movement passed distance; trigger a refresh
					if (mLastDirection == Mode.PULL_FROM_END) {
						return true;
					}
					if (mMode == Mode.PULL_FROM_START) {
						mLastDirection = Mode.PULL_FROM_START;
						startRefresh();
					}
				} else if (-yDiff > mDistanceToTriggerSync) {
					if ((!up && !down && !loadNoFull) || mLastDirection == Mode.PULL_FROM_START) {
						return true;
					}
					if ((mMode == Mode.PULL_FROM_END) && !mIsAutoLoad && !mStopLoading) {
						mLastDirection = Mode.PULL_FROM_END;
						startLoad();
					}
				} else {
					if (mLastDirection == Mode.PULL_FROM_START) {
						if (!up && !down && yDiff < 0 && !loadNoFull) {
							return true;
						}
						// Just track the user's movement
						// 根据手指移动距离设置进度条显示的百分比
						setTriggerPercentage(mAccelerateInterpolator.getInterpolation(Math.abs(yDiff) / mDistanceToTriggerSync));
						updateContentOffsetTop((int) yDiff);
						if (mTarget.getTop() == getPaddingTop()) {
							// If the user puts the view back at the top, we
							// don't need to. This shouldn't be considered
							// cancelling the gesture as the user can
							// restart
							// from the top.
							removeCallbacks(mCancel);
							mLastDirection = Mode.DISABLED;
						} else {
							mDirection = (yDiff > 0 ? 1 : -1);
							updatePositionTimeout();
						}
					}
				}
			}
			break;
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			mActivePointerId = INVALID_POINTER;
			mLastDirection = Mode.DISABLED;
			return false;
		}
		return true;
	}

	public void setColor(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
		setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4);
	}

	private void setColorSchemeResources(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
		final Resources res = getResources();
		setColorSchemeColors(res.getColor(colorRes1), res.getColor(colorRes2), res.getColor(colorRes3), res.getColor(colorRes4));
	}

	/**
	 * Set the four colors used in the progress animation. The first color will
	 * also be the color of the bar that grows in response to a user swipe
	 * gesture.
	 */
	private void setColorSchemeColors(int color1, int color2, int color3, int color4) {
		ensureTarget();
		mProgressBar.setColorScheme(color1, color2, color3, color4);
	}

	private void ensureTarget() {
		// Don't bother getting the parent height if the parent hasn't been laid
		// out yet.
		if (mTarget == null) {
			if (getChildCount() > 1 && !isInEditMode()) {
				throw new IllegalStateException("SwipeRefreshLayout can host only one direct child");
			}
			mTarget = getChildAt(0);
			mOriginalOffsetTop = mTarget.getTop() + getPaddingTop();
			if (mTarget instanceof AbsListView) {
				final ListView absListView = (ListView) mTarget;
				absListView.addFooterView(mFooterView);
				absListView.setOnScrollListener(new OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						if (view.getLastVisiblePosition() >= (view.getCount() - mBeforeNum)) {
							if (!mLoading && !mRefreshing && !mStopLoading && mIsAutoLoad && mIsScroll) {
								startLoad();
							}
						}
					}

					@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					}
				});
			} else {
				mIsAutoLoad = false;
			}
		}
		if (mDistanceToTriggerSync == -1) {
			if (getParent() != null && ((View) getParent()).getHeight() > 0) {
				final DisplayMetrics metrics = getResources().getDisplayMetrics();
				mDistanceToTriggerSync = (int) Math.min(((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
						REFRESH_TRIGGER_DISTANCE * metrics.density);
			}
		}
	}

	/**
	 * @return Whether it is possible for the child view of this layout to
	 *         scroll up. Override this if the child view is a custom view.
	 */
	public boolean canChildScrollUp() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView
								.getPaddingTop());
			} else {
				return mTarget.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, -1);
		}
	}

	public boolean canChildScrollDown() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);
				if (lastChild != null) {
					return (absListView.getLastVisiblePosition() == (absListView.getCount() - 1))
							&& lastChild.getBottom() > absListView.getPaddingBottom();
				} else {
					return false;
				}
			} else {
				return mTarget.getHeight() - mTarget.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, 1);
		}
	}

	/**
	 * 函数名称 : onSecondaryPointerUp 功能描述 : 根据第二触点的位置得出 mLastMotionY,
	 * mActivePointerId 参数及返回值说明：
	 * 
	 * @param ev
	 */
	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		}
	}

	/**
	 * @return Whether the SwipeRefreshWidget is actively showing refresh
	 *         progress.
	 */
	public boolean isRefreshing() {
		return mRefreshing;
	}

	public boolean isLoading() {
		return mLoading;
	}

	public void startRefresh() {
		if (!mLoading && !mRefreshing) {
			removeCallbacks(mCancel);
			mReturnToStartPosition.run();
			setRefreshing(true);
			mRefreshListener.onRefresh();
			mStopLoading = false;
			mFooterView.setVisibility(View.GONE);
			mTvFooterView.setText("正在加载...");
			if (mTarget instanceof AbsListView) {
				final ListView absListView = (ListView) mTarget;
				absListView.removeFooterView(mFooterView);
				absListView.addFooterView(mFooterView);
			}
		}
	}

	private void startLoad() {
		if (!mLoading && !mRefreshing) {
			removeCallbacks(mCancel);
			mReturnToStartPosition.run();
			setLoading(true);
			mRefreshListener.onLoad();
			mIsScroll = false;
		}
	}

	public void stopLoading() {
		mStopLoading = true;
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				ensureTarget();
				mCurrPercentage = 0;
				mLastDirection = Mode.DISABLED;
				mLoading = false;
				mFooterView.setVisibility(View.VISIBLE);
				mTvFooterView.setText("没有更多了");
				mIsScroll = true;
			}
		});
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mTarget instanceof AbsListView) {
					final ListView absListView = (ListView) mTarget;
					absListView.removeFooterView(mFooterView);
				}
			}
		}, 1000);
	}

	/**
	 * Notify the widget that refresh state has changed. Do not call this when
	 * refresh is triggered by a swipe gesture.
	 * 
	 * @param refreshing
	 *            Whether or not the view should show refresh progress.
	 */
	public void setRefreshing(final boolean refreshing) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				if (mRefreshing != refreshing) {
					ensureTarget();
					mCurrPercentage = 0;
					mRefreshing = refreshing;
					if (mRefreshing) {
						mProgressBar.start();
					} else {
						mLastDirection = Mode.DISABLED;
						mProgressBar.stop();
						if (!canChildScrollDown() && mIsAutoLoad) {
							startLoad();
						}
					}
				}
			}
		});
	}

	public void setLoading(final boolean loading) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				if (mLoading != loading) {
					ensureTarget();
					mCurrPercentage = 0;
					mLoading = loading;
					if (mLoading) {
						mFooterView.setVisibility(View.VISIBLE);
					} else {
						mLastDirection = Mode.DISABLED;
						mFooterView.setVisibility(View.GONE);
						mIsScroll = true;
						if (!canChildScrollDown() && mIsAutoLoad) {
							startLoad();
						}
					}
				}
			}
		});

	}

	/**
	 * 手指移动时更新子控件的位置
	 */
	private void updateContentOffsetTop(int targetTop) {
		final int currentTop = mTarget.getTop();
		if (targetTop > mDistanceToTriggerSync) {
			targetTop = (int) mDistanceToTriggerSync;
		}
		// 注释掉，否则上拉的时候子控件会向下移动
		// else if (targetTop < 0) {
		// targetTop = 0;
		// }
		setTargetOffsetTopAndBottom(targetTop - currentTop);
	}

	/**
	 * 根据偏移量对子控件进行移动
	 */
	private void setTargetOffsetTopAndBottom(int offset) {
		mTarget.offsetTopAndBottom(offset);
		mCurrentTargetOffsetTop = mTarget.getTop();
	}

	private void updatePositionTimeout() {
		removeCallbacks(mCancel);
		postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
	}

	/**
	 * 设置进度条的显示百分比
	 */
	private void setTriggerPercentage(float percent) {
		if (percent == 0f) {
			// No-op. A null trigger means it's uninitialized, and setting it to
			// zero-percent
			// means we're trying to reset state, so there's nothing to reset in
			// this case.
			mCurrPercentage = 0;
			return;
		}
		mCurrPercentage = percent;
		if ((mMode == Mode.PULL_FROM_START) && mLastDirection != Mode.PULL_FROM_END && !mLoading) {
			mProgressBar.setTriggerPercentage(percent);
		}
	}

	/**
	 * Cancel the refresh gesture and animate everything back to its original
	 * state.
	 */
	private final Runnable mCancel = new Runnable() {
		@Override
		public void run() {
			mReturningToStart = true;
			// Timeout fired since the user last moved their finger; animate the
			// trigger to 0 and put the target back at its original position
			if (mProgressBar != null) {
				mFromPercentage = mCurrPercentage;
				if (mDirection > 0 && mMode == Mode.PULL_FROM_START) {
					mShrinkTrigger.setDuration(mMediumAnimationDuration);
					mShrinkTrigger.setAnimationListener(mShrinkAnimationListener);
					mShrinkTrigger.reset();
					mShrinkTrigger.setInterpolator(mDecelerateInterpolator);
					startAnimation(mShrinkTrigger);
				}
			}
			mDirection = 0;
			animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(), mReturnToStartPositionListener);
		}
	};

	/**
	 * 对下拉或上拉进行复位
	 */
	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			if (mFrom != mOriginalOffsetTop) {
				targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
			}
			int offset = targetTop - mTarget.getTop();
			// 注释掉这里，不然上拉后回复原位置会很快，不平滑
			// final int currentTop = mTarget.getTop();
			// if (offset + currentTop < 0) {
			// offset = 0 - currentTop;
			// }
			setTargetOffsetTopAndBottom(offset);
		}
	};

	/**
	 * 对子控件进行移动
	 */
	private void animateOffsetToStartPosition(int from, AnimationListener listener) {
		mFrom = from;
		mAnimateToStartPosition.reset();
		mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
		mAnimateToStartPosition.setAnimationListener(listener);
		mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
		if (mTarget == null) {
			setColor(0x106001b, 0x1060014, 0x1060018, 0x1060016);
		}
		mTarget.startAnimation(mAnimateToStartPosition);
	}

	/**
	 * 监听，回复初始位置
	 */
	private final AnimationListener mReturnToStartPositionListener = new BaseAnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			// Once the target content has returned to its start position, reset
			// the target offset to 0
			mCurrentTargetOffsetTop = 0;
			mLastDirection = Mode.DISABLED;
		}
	};

	/**
	 * 回复初始位置
	 */
	private final Runnable mReturnToStartPosition = new Runnable() {
		@Override
		public void run() {
			mReturningToStart = true;
			animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(), mReturnToStartPositionListener);
		}
	};

	/**
	 * 回复进度条百分比
	 */
	private final AnimationListener mShrinkAnimationListener = new BaseAnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			mCurrPercentage = 0;
		}
	};

	/**
	 * 设置上方进度条的完成度百分比
	 */
	private Animation mShrinkTrigger = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			float percent = mFromPercentage + ((0 - mFromPercentage) * interpolatedTime);
			mProgressBar.setTriggerPercentage(percent);
		}
	};

	public static enum Mode {
		/**
		 * Disable all Pull-to-Refresh gesture and Refreshing handling
		 */
		DISABLED,

		/**
		 * Only allow the user to Pull from the start of the Refreshable View to
		 * refresh. The start is either the Top or Left, depending on the
		 * scrolling direction.
		 */
		PULL_FROM_START,

		/**
		 * Only allow the user to Pull from the end of the Refreshable View to
		 * refresh. The start is either the Bottom or Right, depending on the
		 * scrolling direction.
		 */
		PULL_FROM_END,
	}

	/**
	 * Simple AnimationListener to avoid having to implement unneeded methods in
	 * AnimationListeners.
	 */
	private class BaseAnimationListener implements AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * Set the listener to be notified when a refresh is triggered via the swipe
	 * gesture.
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		mRefreshListener = listener;
	}

	/**
	 * Classes that wish to be notified when the swipe gesture correctly
	 * triggers a refresh should implement this interface.
	 */
	public interface OnRefreshListener {
		public void onRefresh();

		public void onLoad();
	}

	private int dp2px(int dpValue) {
		return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
	}

}
