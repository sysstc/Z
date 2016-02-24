package com.example.z.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.z.R;


public class SimpleActionbar extends FrameLayout implements View.OnClickListener {

	public TextView mTvLeft;
	public TextView mTvRight1;
	public TextView mTvRight2;
	private TextView mTvCenter;

	private OnLeftButtonClickListener mOnLeftButtonClickListener;
	private OnRightButton1ClickListener mOnRightButton1ClickListener;
	private OnRightButton2ClickListener mOnRightButton2ClickListener;

	private int background;
	private int text_color;

	private boolean isActivityFinish;

	public SimpleActionbar(Context context) {
		this(context, null);
	}

	public SimpleActionbar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimpleActionbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SimpleActionbar, 0, 0);
		try {
			background = typedArray.getColor(R.styleable.SimpleActionbar_background,
					getResources().getColor(R.color.actionbar_background));
			text_color = typedArray.getColor(R.styleable.SimpleActionbar_text_color,
					getResources().getColor(R.color.actionbar_text_color));
		} finally {
			typedArray.recycle();
		}
		init(context);
	}

	private void init(Context context) {
		this.setBackgroundColor(background);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_simple_actionbar, this, true);
		mTvLeft = (TextView) findViewById(R.id.tvLeft);
		mTvCenter = (TextView) findViewById(R.id.tvCenter);
		mTvRight1 = (TextView) findViewById(R.id.tvRight1);
		mTvRight2 = (TextView) findViewById(R.id.tvRight2);
		mTvLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_header_left, 0, 0, 0);
		mTvLeft.setTextColor(text_color);
		mTvCenter.setTextColor(text_color);
		mTvRight1.setTextColor(text_color);
		mTvRight2.setTextColor(text_color);
		mTvLeft.setOnClickListener(this);
		mTvRight1.setOnClickListener(this);
		mTvRight2.setOnClickListener(this);
	}

	public void setTitle(String title) {
		mTvCenter.setText(title);
	}

	public void leftBlank() {
		mTvLeft.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		isActivityFinish = true;
	}

	public void setLeftTextView(int left, OnLeftButtonClickListener o) {
		mTvLeft.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
		mOnLeftButtonClickListener = o;
		isActivityFinish = true;
	}

	public void setRight1TextView(int right1, OnRightButton1ClickListener o) {
		mTvRight1.setCompoundDrawablesWithIntrinsicBounds(right1, 0, 0, 0);
		mOnRightButton1ClickListener = o;
	}

	public void setRight2TextView(int right2, OnRightButton2ClickListener o) {
		mTvRight2.setCompoundDrawablesWithIntrinsicBounds(right2, 0, 0, 0);
		mOnRightButton2ClickListener = o;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvLeft:
			if (mOnLeftButtonClickListener != null) {
				mOnLeftButtonClickListener.onLeftBtnClick(v);
			}
			if (!isActivityFinish) {
				((Activity) getContext()).finish();
			}
			break;
		case R.id.tvRight1:
			if (mOnRightButton1ClickListener != null)
				mOnRightButton1ClickListener.onRightBtnClick(v);
			break;
		case R.id.tvRight2:
			if (mOnRightButton2ClickListener != null)
				mOnRightButton2ClickListener.onRightBtnClick(v);
			break;
		}
	}

	public interface OnLeftButtonClickListener {
		public void onLeftBtnClick(View button);
	}

	public interface OnRightButton1ClickListener {
		public void onRightBtnClick(View button);
	}

	public interface OnRightButton2ClickListener {
		public void onRightBtnClick(View button);
	}
}
