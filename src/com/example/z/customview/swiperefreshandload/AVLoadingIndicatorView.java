package com.example.z.customview.swiperefreshandload;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jack on 2015/10/15
 * 
 * .BallPulse, .BallGridPulse, .BallClipRotate, .BallClipRotatePulse,
 * .SquareSpin, .BallClipRotateMultiple, .BallPulseRise, .BallRotate,
 * .CubeTransition, .BallZigZag, .BallZigZagDeflect, .BallTrianglePath,
 * .BallScale, .LineScale, .LineScaleParty, .BallScaleMultiple, .BallPulseSync,
 * .BallBeat, .LineScalePulseOut, .LineScalePulseOutRapid, .BallScaleRipple,
 * .BallScaleRippleMultiple, .BallSpinFadeLoader, .LineSpinFadeLoader,
 * .TriangleSkewSpin, .Pacman, .BallGridBeat, .SemiCircleSpin
 * 
 */
public class AVLoadingIndicatorView extends View {

	// Sizes (with defaults in DP)
	public static final int DEFAULT_SIZE = 30;
	Paint mPaint;
	BaseIndicatorController mIndicatorController;
	private boolean mHasAnimation;

	public AVLoadingIndicatorView(Context context) {
		super(context);
		init(null, 0);
	}

	public AVLoadingIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public AVLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs, defStyleAttr);
	}

	private void init(AttributeSet attrs, int defStyle) {
		mPaint = new Paint();
		mPaint.setColor(0xffB5B5B5);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		applyIndicator();
	}

	public void setIndicatorColor(int color) {
		mPaint.setColor(color);
		this.invalidate();
	}

	private void applyIndicator() {
		mIndicatorController = new PacmanIndicator();
		mIndicatorController.setTarget(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
		int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private int measureDimension(int defaultSize, int measureSpec) {
		int result = defaultSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			result = Math.min(defaultSize, specSize);
		} else {
			result = defaultSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawIndicator(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (!mHasAnimation) {
			mHasAnimation = true;
			applyAnimation();
		}
	}

	@Override
	public void setVisibility(int v) {
		if (getVisibility() != v) {
			super.setVisibility(v);
			if (v == GONE || v == INVISIBLE) {
				mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.END);
			} else {
				mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
			}
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.CANCEL);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
	}

	void drawIndicator(Canvas canvas) {
		mIndicatorController.draw(canvas, mPaint);
	}

	void applyAnimation() {
		mIndicatorController.initAnimation();
	}

	private int dp2px(int dpValue) {
		return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
	}

}
