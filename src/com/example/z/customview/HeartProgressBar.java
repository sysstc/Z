package com.example.z.customview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.example.z.R;

public class HeartProgressBar extends View {

	private int width;
	private int height;
	private int heartColor;

	private boolean isStopped;
	private ValueAnimator animatorLeftHeart;
	private ValueAnimator animatorRightHeart;
	private final static String POSITION_X = "positionX";
	private final static String POSITION_Y = "positionY";

	private float leftHeartX;
	private float leftHeartY;
	private float rightHeartX;
	private float rightHeartY;

	public HeartProgressBar(Context context) {
		super(context);
		init(context, Color.parseColor("#FF4351"));
	}

	public HeartProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HeartProgressBar, 0, 0);
		try {
			int heartColor = typedArray.getColor(R.styleable.HeartProgressBar_heartColor, Color.parseColor("#FF4351"));
			init(context, heartColor);
		} finally {
			typedArray.recycle();
		}
	}

	private void init(Context context, int heartColor) {
		this.heartColor = heartColor;
		this.isStopped = true;
	}

	public void start() {
		this.isStopped = false;

		leftHeartX = width / 4 + width / 8;
		leftHeartY = height / 4 + height / 8;

		PropertyValuesHolder widthPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_X, width / 4 + width / 8, width / 2
				+ width / 8);
		PropertyValuesHolder heightPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_Y, height / 4 + height / 8, height / 2
				+ height / 8);
		animatorLeftHeart = ValueAnimator.ofPropertyValuesHolder(widthPropertyHolder, heightPropertyHolder);
		animatorLeftHeart.setDuration(2000);
		animatorLeftHeart.setStartDelay(1000);
		animatorLeftHeart.setInterpolator(new AnticipateOvershootInterpolator());
		animatorLeftHeart.addUpdateListener(leftHeartAnimationUpdateListener);
		animatorLeftHeart.setRepeatMode(ValueAnimator.REVERSE);
		animatorLeftHeart.setRepeatCount(ValueAnimator.INFINITE);

		widthPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_X, width / 2 + width / 8, width / 4 + width / 8);
		heightPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_Y, height / 4 + height / 8, height / 2 + height / 8);
		animatorRightHeart = ValueAnimator.ofPropertyValuesHolder(widthPropertyHolder, heightPropertyHolder);
		animatorRightHeart.setDuration(2000);
		animatorRightHeart.setInterpolator(new AnticipateOvershootInterpolator());
		animatorRightHeart.addUpdateListener(rightHeartAnimationUpdateListener);
		animatorRightHeart.setRepeatMode(ValueAnimator.REVERSE);
		animatorRightHeart.setRepeatCount(ValueAnimator.INFINITE);

		animatorRightHeart.start();
		animatorLeftHeart.start();

		invalidate();
	}

	public void dismiss() {
		this.isStopped = true;
		animatorLeftHeart.cancel();
		animatorRightHeart.cancel();
		invalidate();
	}

	public boolean isStopped() {
		return this.isStopped;
	}

	public void setHeartColor(int color) {
		this.heartColor = color;
		invalidate();
	}

	private int measureWidth(int widthMeasureSpec) {
		int result = 0;

		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = 200;
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		this.width = result;
		return result;
	}

	private int measureHeight(int heightMeasureSpec) {
		int result = 0;

		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = 200;
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		this.height = result;
		return result;
	}

	private float measureCircleRadius(int width, int height) {
		float radius = (float) Math.min(width, height) / 4;
		return radius;
	}

	ValueAnimator.AnimatorUpdateListener leftHeartAnimationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			if (!isStopped) {
				leftHeartX = (Float) animation.getAnimatedValue(POSITION_X);
				leftHeartY = (Float) animation.getAnimatedValue(POSITION_Y);
				invalidate();
			}
		}
	};

	ValueAnimator.AnimatorUpdateListener rightHeartAnimationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			if (!isStopped) {
				rightHeartX = (Float) animation.getAnimatedValue(POSITION_X);
				rightHeartY = (Float) animation.getAnimatedValue(POSITION_Y);
				invalidate();
			}
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		if (this.width != this.height) {
			return;
		}
		if (!this.isStopped) {
			drawRhombus(canvas);
			drawCircle(canvas, rightHeartX, rightHeartY);
			drawCircle(canvas, leftHeartX, leftHeartY);
		}
		canvas.restore();
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		this.width = width;
		this.height = height;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private void drawRhombus(Canvas canvas) {
		Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectPaint.setColor(this.heartColor);
		RectF rect = new RectF();
		rect.set(width / 4, height / 4, width / 4 * 3, height / 4 * 3);
		canvas.rotate(-45f, rect.centerX(), rect.centerY());
		canvas.drawRect(rect, rectPaint);
		canvas.rotate(45f, rect.centerX(), rect.centerY());

		// Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// paint.setColor(this.heartColor);
		// paint.setStyle(Paint.Style.FILL_AND_STROKE);
		// paint.setDither(true);
		// paint.setStrokeJoin(Paint.Join.ROUND);
		// paint.setStrokeCap(Paint.Cap.ROUND);
		// paint.setStrokeWidth(3);
		//
		// Path path = new Path();
		// path.setFillType(Path.FillType.EVEN_ODD);
		// path.moveTo(width/2, height/4);
		// path.lineTo(width/4, height/2);
		// path.moveTo(width/4, height/2);
		// path.lineTo(width/2, height - height/4);
		// path.moveTo(width/2, height - height/4);
		// path.lineTo(width - width/4, height/2);
		// path.moveTo(width - width/4, height/2);
		// path.lineTo(width/2, height/4);
		// path.close();
		// canvas.drawPath(path, paint);
	}

	private void drawCircle(Canvas canvas, float x, float y) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(this.heartColor);
		float circleRadius = measureCircleRadius(this.width, this.height);
		canvas.drawCircle(x, y, circleRadius, paint);
	}

}
