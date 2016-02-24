package com.example.z.activity;

import android.os.Bundle;

import com.example.z.R;
import com.example.z.customview.SimpleActionbar;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Tab1Activity extends BaseActivity{

	@ViewInject(R.id.actionbar)
	private SimpleActionbar mSimpleActionbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_1);
		init();
	}

	private void init() {
		initActionbar();
	}
	
	private void initActionbar() {
		mSimpleActionbar.setTitle("预览");
	}
}
