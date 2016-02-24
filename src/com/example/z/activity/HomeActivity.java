package com.example.z.activity;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.z.R;
import com.example.z.adapter.SearchAutoAdapter;
import com.example.z.customview.MaterialLayout;
import com.example.z.customview.PullPushScrollView;
import com.example.z.customview.PullPushScrollView.OnSlideListenre;
import com.example.z.customview.SimpleActionbar;
import com.example.z.customview.SimpleActionbar.OnRightButton2ClickListener;
import com.example.z.customview.adv.AdvertisementView;
import com.example.z.customview.adv.AdvertisementView.OnAdvertisementItemClickListener;
import com.example.z.http.ABRequestCallBack;
import com.example.z.http.InterfaceServer;
import com.example.z.model.BaseModel;
import com.example.z.model.RequestModel;
import com.example.z.model.SearchAutoData;
import com.example.z.utils.SDToast;
import com.example.z.zxing.CaptureActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeActivity extends BaseActivity implements OnClickListener{

	public static final String SEARCH_HISTORY = "search_history";
	
	@ViewInject(R.id.actionbar)
	private SimpleActionbar mSimpleActionbar;
	@ViewInject(R.id.adv)
	private AdvertisementView mAdvertisementView;
	@ViewInject(R.id.ppsv)
	private PullPushScrollView mPullPushScrollView;
	@ViewInject(R.id.vBg)
	private View mVBg;
	@ViewInject(R.id.ml)
	private MaterialLayout ml;
	@ViewInject(R.id.ivBack)
	private ImageView mIvBack;
	@ViewInject(R.id.ivScan)
	private ImageView mIvScan;
	@ViewInject(R.id.etSearch)
	private EditText mEtSearch;
	@ViewInject(R.id.ivSearchQuery)
	private ImageView mIvSearchQuery;
	@ViewInject(R.id.llHistory)
	private LinearLayout mLlHistory;
	@ViewInject(R.id.lvAuto)
	private ListView mLvAuto;
	@ViewInject(R.id.tvClean)
	private TextView mTvClean;
	@ViewInject(R.id.tab1)
	private LinearLayout mTab1;
	
	private SearchAutoAdapter mSearchAutoAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mIsExitApp = true;
		setContentView(R.layout.activity_home);
		init();
	}

	private void init() {
		initActionbar();
		initSearch();
		initPullPushScrollView();
		initAbSlidingPlayView();
//		initRequest();
	}

	/**
	 * 网络请求
	 */
	private void initRequest() {
		RequestModel model = new RequestModel();
//		model.put(key, value);
		InterfaceServer.getInstance().requestInterface("server_api_url",model, new ABRequestCallBack<BaseModel>(true){

			@Override
			public void onFailure(HttpException error, String msg) {
			}

			@Override
			public void onSuccessModel(BaseModel actModel) {
			}
			
		});
	}

	private void initActionbar() {
		mSimpleActionbar.setTitle("首页");
		mSimpleActionbar.setRight2TextView(R.drawable.ic_toolbar_menu_search, new OnRightButton2ClickListener() {
			
			@Override
			public void onRightBtnClick(View button) {
				clickMenuSearch();
			}
		});
	}

	private void initSearch() {
		mVBg.setOnClickListener(this);
		mIvBack.setOnClickListener(this);
		mIvScan.setOnClickListener(this);
		mIvSearchQuery.setOnClickListener(this);
		mTvClean.setOnClickListener(this);

		mSearchAutoAdapter = new SearchAutoAdapter(this, 5, this);
		mLvAuto.setAdapter(mSearchAutoAdapter);
		mLvAuto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				SearchAutoData data = (SearchAutoData) mSearchAutoAdapter.getItem(position);
				mEtSearch.setText(data.getContent());
				mIvSearchQuery.performClick();
			}
		});

		mEtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mSearchAutoAdapter.performFiltering(s);
				if (s.length() > 0 && mSearchAutoAdapter.getCount() > 0) {
					mLlHistory.setVisibility(View.VISIBLE);
				} else{
					mLlHistory.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private void initPullPushScrollView() {
		mPullPushScrollView.setOnSlideListenre(new OnSlideListenre() {

			@Override
			public void onSlide(float percent) {
			}

			@Override
			public void onRefresh() {
				SDToast.showToast("刷新完成");
			}
		});
	}
	
	private void initAbSlidingPlayView() {
		final ArrayList<String> urisList = new ArrayList<String>();
		urisList.add("http://pic17.nipic.com/20110924/5498863_181701247129_2.jpg");
		urisList.add("http://p0.so.qhimg.com/bdr/_240_/t0162b21f36ddc40e15.jpg");
		mAdvertisementView.setImageUris(urisList);
		mAdvertisementView.setOnAdvertisementItemClickListener(new OnAdvertisementItemClickListener() {

			@Override
			public void OnItem(int position) {
				SDToast.showToast("广告");
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.vBg:
		case R.id.ivBack:// 返回按钮
			clickBack();
			break;
		case R.id.ivScan:
			clickScan();
			break;
		case R.id.tvClean:// 清除历史记录
			clickClean();
			break;
		case R.id.ivSearchQuery:// 搜索按钮
			cliclSearchQuery();
			break;
		case R.id.auto_add:// "+"号按钮
			clickAdd(view);
			break;
		}
	}
	
	public void clickTab1(View view){
		startPreviewActivity(new Intent(HomeActivity.this, Tab1Activity.class),  view);
	}

	private void clickMenuSearch() {
		ml.setVisibility(View.VISIBLE);
		mVBg.setVisibility(View.VISIBLE);
		ml.push(mSimpleActionbar.mTvRight2);
	}
	
	private void clickBack() {
		ml.pop();
		mEtSearch.setText("");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				ml.setVisibility(View.GONE);
				mVBg.setVisibility(View.GONE);
				mLlHistory.setVisibility(View.GONE);
			}
		}, 200);
	}
	
	private void clickScan() {
				Intent intent = new Intent(HomeActivity.this, CaptureActivity.class);
				startActivityForResult(intent, 2);
				clickBack();
	}
	
	private void clickClean() {
		cleanHistory();
		mSearchAutoAdapter.initSearchHistory();
		mSearchAutoAdapter.notifyDataSetChanged();
	}

	private void cliclSearchQuery() {
		saveSearchHistory();
		mSearchAutoAdapter.initSearchHistory();
	}

	private void clickAdd(View v) {
		SearchAutoData data = (SearchAutoData) v.getTag();
		mEtSearch.setText(data.getContent());
	}

	/*
	 * 保存搜索记录
	 */
	private void saveSearchHistory() {
		String text = mEtSearch.getText().toString().trim();
		if (text.length() < 1) {
			return;
		}
		SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
		String longhistory = sp.getString(SEARCH_HISTORY, "");
		ArrayList<String> history = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(longhistory, ",");
		while (token.hasMoreTokens()) {
			if (history.size() > 50) {
				history.remove(0);
			}
			history.add(token.nextToken());
		}
		if (history.size() > 0) {
			int i;
			for (i = 0; i < history.size(); i++) {
				if (text.equals(history.get(i))) {
					history.remove(i);
					break;
				}
			}
			history.add(0, text);
		}
		if (history.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < history.size(); i++) {
				sb.append(history.get(i) + ",");
			}
			sp.edit().putString(SEARCH_HISTORY, sb.toString()).commit();
		} else {
			sp.edit().putString(SEARCH_HISTORY, text + ",").commit();
		}
	}

	// 清除搜索记录
	public void cleanHistory() {
		SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		SDToast.showToast("清除成功");
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == CaptureActivity.CAPTURE_RESULTCODE) {
			if (requestCode == 2) {
				String result = data.getStringExtra("result");
				if (TextUtils.isEmpty(result)) {
					result = "无识别结果，请重新扫描";
				}
				SDToast.showToast(result);
			}
		}
	}
}
