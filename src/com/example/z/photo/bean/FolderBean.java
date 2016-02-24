package com.example.z.photo.bean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FolderBean {

	private String dir;
	private String firstImgPath;
	private String name;
	private int count;
	private boolean isSelect;
	private List<PhotoBean> phptpBeanList;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImgPath() {
		return firstImgPath;
	}

	public void setFirstImgPath(String firstImgPath) {
		this.firstImgPath = firstImgPath;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public List<PhotoBean> getPhptpBeanList() {
		return phptpBeanList;
	}

	public void setPhptpBeanList(List<PhotoBean> phptpBeanList) {
		Collections.sort(phptpBeanList, new Comparator<PhotoBean>() {
			@Override
			public int compare(PhotoBean pb1, PhotoBean pb2) {
				long t1 = pb1.getTime();
				long t2 = pb2.getTime();
				if (t1 > t2) {
					return -1;
				} else if (t1 < t2) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		this.phptpBeanList = phptpBeanList;
		this.count = phptpBeanList.size();
	}

}
