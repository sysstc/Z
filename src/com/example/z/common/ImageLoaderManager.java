package com.example.z.common;

import android.graphics.Bitmap;

import com.example.z.R;
import com.example.z.app.App;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoaderManager {

	private static ImageLoader mImageLoader = ImageLoader.getInstance();
	private static DisplayImageOptions options = null;
	private static DisplayImageOptions noCacheOptions = null;

	private ImageLoaderManager() {
	}

	public static ImageLoader getImageLoader() {
		initImageLoader();
		return mImageLoader;
	}

	private static DisplayImageOptions getOptions() {
		if (options == null) {
			options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.no_pic)
					.showImageOnFail(R.drawable.no_pic).resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
					.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300))
					.build();
		}
		return options;
	}

	private static void initImageLoader() {
		if (!mImageLoader.isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(App.getApplication())
					.denyCacheImageMultipleSizesInMemory().memoryCacheSize(2 * 1024 * 1024)
					.discCacheFileNameGenerator(new Md5FileNameGenerator()).defaultDisplayImageOptions(getOptions()).build();
			mImageLoader.init(config);
		}
	}

	public static DisplayImageOptions getNoCacheOptions() {
		if (noCacheOptions == null) {
			noCacheOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.no_pic)
					.showImageOnFail(R.drawable.no_pic).resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(false)
					.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300))
					.build();
		}
		return noCacheOptions;
	}

}
