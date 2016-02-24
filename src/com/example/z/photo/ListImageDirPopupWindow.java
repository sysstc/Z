package com.example.z.photo;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.z.R;
import com.example.z.photo.bean.FolderBean;
import com.example.z.photo.util.ImageLoader;

public class ListImageDirPopupWindow extends PopupWindow {

	private int mWidth;
	private int mHeight;
	private View mConvertView;
	private ListView mListView;

	private List<FolderBean> mDatas;

	private OnDirSelectedListener mOnDirSelectedListener;

	private ListDirAdapter mAdapter;

	public void setOnDirSelectedListener(OnDirSelectedListener onDirSelectedListener) {
		mOnDirSelectedListener = onDirSelectedListener;
	}

	public interface OnDirSelectedListener {
		void onSelected(FolderBean folderBean);
	}

	public ListImageDirPopupWindow(Context context, List<FolderBean> datas) {

		calWidthAndHeight(context);
		mDatas = datas;

		mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_photo, null);

		setContentView(mConvertView);
		setWidth(mWidth);
		setHeight(mHeight);

		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());

		setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});

		initViews(context);
		initEvent();
	}

	private void initEvent() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mOnDirSelectedListener != null) {
					mDatas.get(position).setSelect(true);
					for (int i = 0; i < mDatas.size(); i++) {
						if (i == position) {
							continue;
						}
						mDatas.get(i).setSelect(false);
					}
					mOnDirSelectedListener.onSelected(mDatas.get(position));
				}
			}
		});
	}

	private void initViews(Context context) {
		mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
		mAdapter = new ListDirAdapter(context, mDatas);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * 计算popupWindow的宽度和高度
	 * 
	 * @param context
	 */
	private void calWidthAndHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mWidth = outMetrics.widthPixels;
		mHeight = (int) (outMetrics.heightPixels * 0.7);
	}

	private class ListDirAdapter extends ArrayAdapter<FolderBean> {

		private LayoutInflater mLayoutInflater;

		private List<FolderBean> mDatas;

		public ListDirAdapter(Context context, List<FolderBean> objects) {
			super(context, 0, objects);

			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.item_popup_photo, parent, false);
				holder.mImg = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
				holder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
				holder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);
				holder.mSelect = (ImageView) convertView.findViewById(R.id.id_select);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FolderBean bean = getItem(position);
			// 重置
			holder.mImg.setImageResource(R.drawable.pictures_no);

			if (bean.isSelect()) {
				holder.mSelect.setVisibility(View.VISIBLE);
			} else
				holder.mSelect.setVisibility(View.GONE);
			ImageLoader.getInstance().loadImage(bean.getFirstImgPath(), holder.mImg);
			holder.mDirName.setText(bean.getName());
			holder.mDirCount.setText(bean.getCount() + "");

			return convertView;
		}

		private class ViewHolder {
			ImageView mImg;
			TextView mDirName;
			TextView mDirCount;
			ImageView mSelect;
		}

	}

}
