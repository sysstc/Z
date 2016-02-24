package com.example.z.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.z.R;
import com.example.z.constant.Constant.TabItemIndex;
import com.example.z.customview.tab.SimpleTabView;
import com.example.z.customview.tab.SimpleTabViewManager;
import com.example.z.customview.tab.SimpleTabViewManager.SimpleTabViewManagerListener;
import com.example.z.fragment.HomeFragment;
import com.example.z.utils.FragmentUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainActivity extends BaseActivity {

	@ViewInject(R.id.tabOne)
	private SimpleTabView mStvOne;
	@ViewInject(R.id.tabTwo)
	private SimpleTabView mStvTwo;
	@ViewInject(R.id.tabThree)
	private SimpleTabView mStvThree;
	@ViewInject(R.id.tabFour)
	private SimpleTabView mStvFour;

	private SimpleTabViewManager mSimpleTabViewManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mIsExitApp = true;
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		initView();
	}

	private void initView() {
		mSimpleTabViewManager = new SimpleTabViewManager();
		SimpleTabView[] items = new SimpleTabView[] { mStvOne, mStvTwo, mStvThree, mStvFour };
		mSimpleTabViewManager.setItems(items);
		mSimpleTabViewManager.setmListener(new SimpleTabViewManagerListener() {

			@Override
			public void onItemClick(View v, int index) {
				switch (index) {
				case TabItemIndex.TAB_ONE:
					switchFragment(R.id.fl, HomeFragment.class, null);
					break;
				case TabItemIndex.TAB_TEO:

					break;
				case TabItemIndex.TAB_THREE:

					break;
				case TabItemIndex.TAB_FOUR:

					break;
				}
				mSimpleTabViewManager.setSelectIndex(index, null, false);
			}
		});
		mSimpleTabViewManager.setSelectIndex(TabItemIndex.TAB_ONE, null, true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(null != FragmentUtils.getCurrentFragment()){
			if(FragmentUtils.getCurrentFragment() instanceof HomeFragment){
				((HomeFragment)FragmentUtils.getCurrentFragment()).onActivityResult(requestCode, resultCode, data);
			}
		}
	}
	
}
