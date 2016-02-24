package com.example.z.zxing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class FloatDrawable extends Drawable {

	private Context mContext;
	private int offset = 50;
	private Paint mLinePaint = new Paint();
	private Paint mLinePaint2 = new Paint();
	{
		mLinePaint.setARGB(200, 50, 50, 50);
		mLinePaint.setStrokeWidth(1F);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.WHITE);
		//
		mLinePaint2.setARGB(200, 50, 50, 50);
		mLinePaint2.setStrokeWidth(7F);
		mLinePaint2.setStyle(Paint.Style.STROKE);
		mLinePaint2.setAntiAlias(true);
		mLinePaint2.setColor(Color.WHITE);
	}

	public FloatDrawable(Context context) {
		super();
		this.mContext = context;

	}

	public int getBorderWidth() {
		return dipTopx(mContext, offset);// 根据dip计算的像素值，做适配用的
	}

	public int getBorderHeight() {
		return dipTopx(mContext, offset);
	}

	@Override
	public void draw(Canvas canvas) {

		int left = getBounds().left;
		int top = getBounds().top;
		int right = getBounds().right;
		int bottom = getBounds().bottom;

		Rect mRect = new Rect(left + dipTopx(mContext, offset) / 2, top
				+ dipTopx(mContext, offset) / 2, right
				- dipTopx(mContext, offset) / 2, bottom
				- dipTopx(mContext, offset) / 2);
		int cl = left + dipTopx(mContext, offset) / 2;
		int ct = top + dipTopx(mContext, offset) / 2;
		int cr = right - dipTopx(mContext, offset) / 2;
		int cb = bottom - dipTopx(mContext, offset) / 2;
		// 画默认的选择框
		canvas.drawRect(mRect, mLinePaint);
		// 画四个角的四个粗拐角、也就是八条粗线
		canvas.drawLine(cl - 3.5F, ct, cl + dipTopx(mContext, offset) / 2, ct,
				mLinePaint2);
		canvas.drawLine(cl, ct, cl, ct + dipTopx(mContext, offset) / 2,
				mLinePaint2);
		canvas.drawLine(cr - dipTopx(mContext, offset) / 2, ct, cr + 3.5F, ct,
				mLinePaint2);
		canvas.drawLine(cr, ct, cr, ct + dipTopx(mContext, offset) / 2,
				mLinePaint2);
		canvas.drawLine(cl, cb - dipTopx(mContext, offset) / 2, cl, cb,
				mLinePaint2);
		canvas.drawLine(cl - 3.5F, cb, cl + dipTopx(mContext, offset) / 2, cb,
				mLinePaint2);
		canvas.drawLine(cr, cb - dipTopx(mContext, offset) / 2, cr, cb,
				mLinePaint2);
		canvas.drawLine(cr - dipTopx(mContext, offset) / 2, cb, cr + 3.5F, cb,
				mLinePaint2);

	}

	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(new Rect(bounds.left - dipTopx(mContext, offset) / 2,
				bounds.top - dipTopx(mContext, offset) / 2, bounds.right
						+ dipTopx(mContext, offset) / 2, bounds.bottom
						+ dipTopx(mContext, offset) / 2));
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}

	public int dipTopx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
