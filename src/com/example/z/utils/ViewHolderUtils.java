package com.example.z.utils;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 这是一个生产 ViewHolder的工具类
 */
public class ViewHolderUtils {

	/**
	 * 得到视图为 view 的 viewHodler
	 * 
	 * @param view
	 * @return
	 */
	public ViewHolder get(View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		}

		return viewHolder;
	}

	/**
	 * viewhodler 存储 view的子 view 的索引
	 * 
	 * @author zzz40500
	 */
	public static class ViewHolder {

		private SparseArray<View> viewHolder;

		private View view;

		public ViewHolder(View view) {
			this.view = view;
			viewHolder = new SparseArray<View>();
		}

		@SuppressWarnings("unchecked")
		public <T extends View> T get(int id) {

			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;

		}

		/**
		 * 根据id获得TextView (这里没有做类型的判断所以你要保证 控件确实为TextView类型)
		 * 
		 * @param id
		 * @return
		 */
		public TextView getTextView(int id) {
			return get(id);
		}

		/**
		 * 根据id获得ImageView (这里没有做类型的判断所以你要保证 控件确实为ImageView类型)
		 * 
		 * @param id
		 * @return
		 */
		public ImageView getImageView(int id) {
			return get(id);
		}

		/**
		 * 根据id获得View
		 * 
		 * @param id
		 * @return
		 */
		public View getView(int id) {
			return get(id);
		}

		/**
		 * 设置text
		 * 
		 * @param id
		 * @param text
		 */
		public void setTextView(int id, String text) {
			getTextView(id).setText(!TextUtils.isEmpty(text) ? text : "");
		}

		/**
		 * 设置image
		 * 
		 * @param id
		 * @param resId
		 */
		public void setImageView(int id, int resId) {
			getImageView(id).setImageResource(resId);
		}

	}
}
