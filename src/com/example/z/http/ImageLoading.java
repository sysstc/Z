package com.example.z.http;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.example.z.R;
import com.example.z.common.ImageLoaderManager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoading {

	public static Bitmap loadImage(String uri) {
		return ImageLoaderManager.getImageLoader().loadImageSync(uri);
	}

	public static Bitmap loadImage(String uri, int width, int height) {
		ImageSize targetSize = new ImageSize(width, height);
		return ImageLoaderManager.getImageLoader().loadImageSync(uri,
				targetSize);
	}

	public static void setImageView(final ImageView imageView, final String uri) {
		try {
			imageView.post(new Runnable() {

				@Override
				public void run() {
					ImageLoaderManager.getImageLoader().displayImage(uri,
							imageView);
				}
			});

		} catch (Exception e) {
			imageView.setImageResource(R.drawable.no_pic);
		}
	}

	public static void setImageView(final ImageView imageView,
			final String uri, final ABImageLoadingCallBack callback) {
		try {
			imageView.post(new Runnable() {

				@Override
				public void run() {
					ImageLoaderManager.getImageLoader().displayImage(uri,
							imageView, new ImageLoadingListener() {

								@Override
								public void onLoadingStarted(String imageUri,
										View view) {
									callback.onLoadingStarted(imageUri, view);
								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {
									callback.onLoadingFailed(imageUri, view);
								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									callback.onLoadingComplete(imageUri, view,
											loadedImage);
								}

								@Override
								public void onLoadingCancelled(String imageUri,
										View view) {
									callback.onLoadingCancelled(imageUri, view);
								}
							});
				}
			});

		} catch (Exception e) {
			imageView.setImageResource(R.drawable.no_pic);
		}
	}

}
