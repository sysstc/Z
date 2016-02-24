package com.example.z.photo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.z.R;
import com.example.z.photo.ListImageDirPopupWindow.OnDirSelectedListener;
import com.example.z.photo.bean.FolderBean;
import com.example.z.photo.bean.PhotoBean;
import com.example.z.photo.util.TimeUtils;

public class PhotoActivity extends FragmentActivity {

	public static String PHOTO_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + "temp.png";
	public static int PHOTO_RESULTCODE = 654;
	private static final int DATA_LOADED = 0X110;
	private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();
	private ProgressDialog mProgressDialog;
	private ListImageDirPopupWindow mDirPopupWindow;
	private boolean mIsSingle;

	private ImageView mBack;
	private TextView mConfirm;

	private GridView mGridView;
	private ImageAdapter mImageAdapter;

	private RelativeLayout mBottomLy;
	private TextView mDirName;
	private TextView mDirCount;
	private TextView mTimeLineText;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == DATA_LOADED) {
				mProgressDialog.dismiss();
				// 绑定数据到View中
				data2View();

				initDirPopupWindow();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		initView();
		getIntentData();
		initDatas();
		initEvent();
	}

	private void getIntentData() {
		mIsSingle = getIntent().getBooleanExtra("IsSingle", true);
		if (mIsSingle) {
			mConfirm.setVisibility(View.GONE);
		}
	}

	private void initDirPopupWindow() {
		mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
		mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);

		mDirPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				lightOn();
			}
		});

		mDirPopupWindow.setOnDirSelectedListener(new OnDirSelectedListener() {
			@Override
			public void onSelected(FolderBean folderBean) {
				mImageAdapter.update(folderBean);
				mDirCount.setText(folderBean.getCount() + "");
				mDirName.setText(folderBean.getName());
				mDirPopupWindow.dismiss();
			}
		});

	}

	/**
	 * 内容区域变亮
	 */
	private void lightOn() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	/**
	 * 内容区域变暗
	 */
	private void lightOff() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.3f;
		getWindow().setAttributes(lp);
	}

	private void data2View() {
		mTimeLineText.setVisibility(View.GONE);

		if (mFolderBeans.size() == 0) {
			Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
			return;
		}

		mImageAdapter = new ImageAdapter(this, mFolderBeans.get(0), mIsSingle);
		mGridView.setAdapter(mImageAdapter);

		mDirName.setText(mFolderBeans.get(0).getName());
		mDirCount.setText(mFolderBeans.get(0).getCount() + "");

		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int state) {

				if (state == SCROLL_STATE_IDLE) {
					// 停止滑动，日期指示器消失
					mTimeLineText.setVisibility(View.GONE);
				} else if (state == SCROLL_STATE_FLING) {
					mTimeLineText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (mTimeLineText.getVisibility() == View.VISIBLE) {
					int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1
							: firstVisibleItem + 1;
					String path = mImageAdapter.getItem(index);
					if (path != null) {
						mTimeLineText.setText(TimeUtils.formatPhotoDate(path));
					}
				}
			}
		});
	}

	private void initEvent() {
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> selectedImgPaths = mImageAdapter.getSelectedImgPaths();
				Intent intent = new Intent();
				intent.putStringArrayListExtra("result", (ArrayList<String>) selectedImgPaths);
				setResult(PhotoActivity.PHOTO_RESULTCODE, intent);
				finish();
			}
		});
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDirPopupWindow.showAsDropDown(mBottomLy, 0, -mBottomLy.getHeight());
				lightOff();
			}
		});
	}

	private static final String[] projectionPhotos = { MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN,
			MediaStore.Images.Media.ORIENTATION };

	/**
	 * 利用ContentProvider扫描手机中的所有图片
	 */
	private void initDatas() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = PhotoActivity.this.getContentResolver();
				Cursor cursor = MediaStore.Images.Media.query(cr, mImgUri, projectionPhotos, "", null,
						MediaStore.Images.Media.DATE_ADDED + " DESC");
				Set<String> mDirPaths = new HashSet<String>();
				List<PhotoBean> photoBeanList = new ArrayList<PhotoBean>();
				PhotoBean photoBean = new PhotoBean();
				photoBean.setPath("R.drawable.pic_camera");
				photoBean.setTime(System.currentTimeMillis());
				photoBeanList.add(photoBean);
				while (cursor.moveToNext()) {
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					long time = TimeUtils.photoDate(path);
					PhotoBean pb = new PhotoBean();
					pb.setPath(path);
					pb.setTime(time);
					photoBeanList.add(pb);
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					FolderBean folderBean = null;
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						folderBean = new FolderBean();
						folderBean.setDir(dirPath);
						folderBean.setFirstImgPath(path);
					}
					if (parentFile.list() == null)
						continue;
					List<String> imgs = Arrays.asList(parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
								return true;
							return false;
						}
					}));
					List<PhotoBean> pList = new ArrayList<PhotoBean>();
					for (String string : imgs) {
						String imgPath = parentFile.getAbsolutePath() + "/" + string;
						PhotoBean p = new PhotoBean();
						p.setPath(imgPath);
						p.setTime(TimeUtils.photoDate(imgPath));
						pList.add(p);
					}
					folderBean.setPhptpBeanList(pList);
					mFolderBeans.add(folderBean);
				}
				FolderBean zero = new FolderBean();
				zero.setDir("/所有照片");
				zero.setFirstImgPath(photoBeanList.size() > 1 ? photoBeanList.get(1).getPath() : "");
				zero.setPhptpBeanList(photoBeanList);
				zero.setSelect(true);
				mFolderBeans.add(0, zero);
				cursor.close();
				// 通知handler扫描图片完成
				mHandler.sendEmptyMessage(DATA_LOADED);
			}
		}).start();

	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.id_back);
		mConfirm = (TextView) findViewById(R.id.id_confirm);
		mGridView = (GridView) findViewById(R.id.id_gridView);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		mDirName = (TextView) findViewById(R.id.id_dir_name);
		mDirCount = (TextView) findViewById(R.id.id_dir_count);
		mTimeLineText = (TextView) findViewById(R.id.timeline_area);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
				Intent intent = new Intent();
				intent.putExtra("result", PHOTO_PATH);
				setResult(PhotoActivity.PHOTO_RESULTCODE, intent);
				finish();
			} else if (requestCode == 1) {
				List<String> selectedImgPaths = mImageAdapter.getSelectedImgPaths();
				selectedImgPaths.add(PHOTO_PATH);
				Intent intent = new Intent();
				intent.putStringArrayListExtra("result", (ArrayList<String>) selectedImgPaths);
				setResult(PhotoActivity.PHOTO_RESULTCODE, intent);
				finish();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
