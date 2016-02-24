package com.example.z.common;

import com.example.z.app.App;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;

public class DbManager {

	private static final String DB_DIR = null;

	private static final String DB_NAME = "Tab.db";

	private static final int DB_VERSION = 1;

	private static DbUtils mDbUtils = null;

	private DbManager() {

	}

	public static DbUtils getDbUtils() {
		if (mDbUtils == null) {
			mDbUtils = DbUtils.create(App.getApplication(), DB_DIR, DB_NAME, DB_VERSION, new ABDbUpgradeListener());
		}
		mDbUtils.configAllowTransaction(true);
		return mDbUtils;
	}

	static class ABDbUpgradeListener implements DbUpgradeListener {

		@Override
		public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {

		}

	}

}
