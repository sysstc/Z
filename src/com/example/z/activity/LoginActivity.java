package com.example.z.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.z.R;
import com.example.z.customview.biuEditText.BiuEditText;
import com.example.z.utils.LayoutAnimationPlay;
import com.example.z.utils.UiUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LoginActivity extends BaseActivity {

	@ViewInject(R.id.name)
	private BiuEditText name;

	@ViewInject(R.id.pwd)
	private BiuEditText pwd;

	@ViewInject(R.id.btn)
	private TextView btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mIsExitApp = true;
		setContentView(R.layout.activity_login);
		btn.post(new Runnable() {

			@Override
			public void run() {
				LayoutAnimationPlay.from(name).translationY(UiUtil.dp2px(LoginActivity.this, 260)).rotationX(0, 90)
						.startDelay(100).duration(500).start();
				LayoutAnimationPlay.from(pwd).translationY(UiUtil.dp2px(LoginActivity.this, 200)).rotationX(0, 90)
						.startDelay(200).duration(500).start();
				LayoutAnimationPlay.from(btn).translationY(UiUtil.dp2px(LoginActivity.this, 140)).rotationX(0, 90)
						.startDelay(300).duration(500).start();
			}
		});

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startPreviewActivity(new Intent(LoginActivity.this, HomeActivity.class), btn);
				LoginActivity.this.finish();
			}
		});
	}

}
