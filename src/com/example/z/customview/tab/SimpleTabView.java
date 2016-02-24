package com.example.z.customview.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.z.R;
import com.example.z.utils.FontUtils;

public class SimpleTabView extends LinearLayout {

	private TextView tvIcon;

	private TextView tvText;

	private int selected_color;

	private int normal_color;

	private String selected_icon;

	private String normal_icon;

	private String text;

	public SimpleTabView(Context context) {
		this(context, null);
	}

	public SimpleTabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimpleTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TabView, 0, 0);
		try {
			selected_color = typedArray.getColor(R.styleable.TabView_selected_color, -1);
			normal_color = typedArray.getColor(R.styleable.TabView_normal_color, -1);
			selected_icon = typedArray.getString(R.styleable.TabView_selected_icon);
			normal_icon = typedArray.getString(R.styleable.TabView_normal_icon);
			text = typedArray.getString(R.styleable.TabView_text);
		} finally {
			typedArray.recycle();
		}
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_tab, this, true);

		tvIcon = (TextView) findViewById(R.id.tvIcon);
		 FontUtils.setFont(tvIcon);
		tvText = (TextView) findViewById(R.id.tvText);
		tvText.setText(text);
		normal();
	}

	public void select() {
		tvIcon.setText(selected_icon);
		tvIcon.setTextColor(selected_color);
		tvText.setTextColor(selected_color);
	}

	public void normal() {
		tvIcon.setText(normal_icon);
		tvIcon.setTextColor(normal_color);
		tvText.setTextColor(normal_color);
	}

}
