package com.example.z.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.z.utils.ViewHolderUtils;

public abstract class BaseAutoAdapter<T> extends BaseAdapter {

	/**
	 * item 的布局文件
	 */
	private int[] mLayoutId;

	private ViewHolderUtils vh;

	public LayoutInflater mInflater;

	private Context mContext;

	protected List<T> mListModel = new ArrayList<T>();

	public BaseAutoAdapter(Context context, List<T> list, int... layoutId) {

		this.mContext = context;
		if (list != null) {
			this.mListModel = list;
		}
		mLayoutId = layoutId;
		this.mInflater = LayoutInflater.from(mContext);
		vh = new ViewHolderUtils();
	}

	private void setData(List<T> listModel) {
		this.mListModel = listModel;
	}

	public void updateListViewData(List<T> listModel) {
		setData(listModel);
		this.notifyDataSetChanged();
	}

	public List<T> getData() {
		return mListModel;
	}

	@Override
	public int getCount() {
		return mListModel != null ? mListModel.size() : 0;
	}

	@Override
	public T getItem(int position) {
		return (mListModel != null && mListModel.size() > 0 && mListModel.size() > position) ? mListModel.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * @Override public int getItemViewType(int position) { return
	 * super.getItemViewType(position); }
	 */

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return mLayoutId.length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutId[type], parent, false);
		}
		getItemView(position, convertView, vh.get(convertView), getItem(position));
		return convertView;
	}

	/**
	 * 通过暴露这个方法,通过操作vh实现将数据的绑定在视图上
	 * 
	 * @param position
	 * @param v
	 * @param vh
	 */
	public abstract void getItemView(int position, View v, ViewHolderUtils.ViewHolder vh, final T model);

}
