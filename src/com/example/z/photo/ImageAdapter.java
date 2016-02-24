package com.example.z.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.z.R;
import com.example.z.photo.bean.FolderBean;
import com.example.z.photo.util.ImageLoader;
import com.example.z.photo.util.ImageLoader.Type;

public class ImageAdapter extends BaseAdapter {

	private final List<String> mSelectImg = new ArrayList<String>();

	private LayoutInflater mLayoutInflater;
	private Activity mActivity;
	private FolderBean mFolderBean;
	private boolean mIsSingle;
	private int itemHeight;

	public ImageAdapter(Activity activity, FolderBean folderBean, boolean isSingle) {
		this.mActivity = activity;
		this.mFolderBean = folderBean;
		this.mIsSingle = isSingle;
		mLayoutInflater = LayoutInflater.from(activity);
		WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		itemHeight = (width - dp2px(activity, 10)) / 3;
	}

	public void update(FolderBean folderBean) {
		this.mFolderBean = folderBean;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mFolderBean.getPhptpBeanList().size();
	}

	@Override
	public String getItem(int position) {
		return mFolderBean.getPhptpBeanList().get(position).getPath();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_gridview, parent, false);
			holder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
			holder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 重置状态
		holder.mImg.setImageResource(R.drawable.pictures_no);
		holder.mImg.setColorFilter(null);
		LayoutParams lp = holder.mImg.getLayoutParams();
		lp.height = itemHeight;
		holder.mSelect.setImageResource(R.drawable.picture_unselected);

		final String filePath = getItem(position);
		if ("R.drawable.pic_camera".equals(filePath)) {
			holder.mImg.setImageResource(R.drawable.pic_camera);
			holder.mImg.setColorFilter(Color.parseColor("#77000000"));
			holder.mSelect.setVisibility(View.GONE);
		} else {
			if (mIsSingle) {
				holder.mSelect.setVisibility(View.GONE);
			} else {
				holder.mSelect.setVisibility(View.VISIBLE);
			}
			ImageLoader.getInstance(10, Type.LIFO).loadImage(filePath, holder.mImg);
		}

		holder.mImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mIsSingle) {
					if ("R.drawable.pic_camera".equals(filePath)) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						Uri uri = Uri.fromFile(new File(PhotoActivity.PHOTO_PATH));
						intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
						mActivity.startActivityForResult(intent, 0);
					} else {
						holder.mImg.setColorFilter(Color.parseColor("#77000000"));
						holder.mSelect.setImageResource(R.drawable.pictures_selected);
						Intent intent = new Intent();
						intent.putExtra("result", filePath);
						mActivity.setResult(PhotoActivity.PHOTO_RESULTCODE, intent);
						mActivity.finish();
					}
				} else {
					// 已经被选择
					if (mSelectImg.contains(filePath)) {
						mSelectImg.remove(filePath);
						holder.mImg.setColorFilter(null);
						holder.mSelect.setImageResource(R.drawable.picture_unselected);
					} else {// 未被选择
						if ("R.drawable.pic_camera".equals(filePath)) {
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							Uri uri = Uri.fromFile(new File(PhotoActivity.PHOTO_PATH));
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							mActivity.startActivityForResult(intent, 1);
						} else {
							mSelectImg.add(filePath);
							holder.mImg.setColorFilter(Color.parseColor("#77000000"));
							holder.mSelect.setImageResource(R.drawable.pictures_selected);
						}
					}
				}

			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectImg.contains(filePath)) {
			holder.mSelect.setImageResource(R.drawable.pictures_selected);
			holder.mImg.setColorFilter(Color.parseColor("#77000000"));
		}
		return convertView;
	}

	private static class ViewHolder {
		ImageView mImg;
		ImageButton mSelect;
	}

	public List<String> getSelectedImgPaths() {
		return mSelectImg;
	}

	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}