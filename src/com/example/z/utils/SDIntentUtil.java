package com.example.z.utils;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class SDIntentUtil
{
	/** 打开外置浏览器 */
	public static void showHTML(Activity context, String url)
	{
		if (SDValidateUtil.isUrl(url))
		{
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");

			Uri content_url = Uri.parse(url);
			intent.setData(content_url);
			context.startActivity(intent);
		} else
		{
			SDToast.showToast("您设置的地址有误");
		}
	}

	/**
	 * 获得打开本地图库的intent
	 * 
	 * @return
	 */
	public static Intent getSelectLocalImageIntent()
	{
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// intent.setType("image/*");
		// intent.putExtra("crop", true);
		// intent.putExtra("return-data", true);
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		return intent;
	}

	/**
	 * 获调用拍照的intent
	 * 
	 * @return
	 */
	public static Intent getTakePhotoIntent(File saveFile)
	{
		if (saveFile == null)
		{
			return null;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri = Uri.fromFile(saveFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		return intent;
	}

	public static Intent getCallPhoneIntent(String phoneNumber)
	{
		return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
	}

}
