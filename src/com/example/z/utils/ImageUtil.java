package com.example.z.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

public class ImageUtil {

	/**
	 * Drawable转Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitmap转字节数组
	 * 
	 * @param byteArray
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 字节数组转Drawable对象
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Drawable byteToDrawable(byte[] byteArray) {
		ByteArrayInputStream ins = null;
		if (byteArray != null) {
			ins = new ByteArrayInputStream(byteArray);
		}
		return Drawable.createFromStream(ins, null);
	}

	/**
	 * 字节字节数组转Bitmap
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b != null && b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/**
	 * 输输流对象转Bitmap
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static Bitmap inputStreamToBitmap(InputStream inputStream)
			throws Exception {
		return BitmapFactory.decodeStream(inputStream);
	}

	/**
	 * Bitmap转Drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable Bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		return bd;
	}

	@SuppressWarnings("deprecation")
	public static String getImageFilePathFromIntent(Intent intent,
			Activity activity) {
		if (intent != null && activity != null) {
			Uri uri = intent.getData();
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.managedQuery(uri, projection, null, null,
					null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			return path;
		} else {
			return null;
		}
	}

	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率
		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	public static boolean compressImageFileToNewFile(File oldFile,
			File newFile, int quality) {
		if (oldFile != null && newFile != null && oldFile.exists()) {
			try {
				if (!newFile.exists()) {
					newFile.createNewFile();
				}
				if (newFile.exists()) {
					Bitmap bmpOld = compressImageFromFile(oldFile
							.getAbsolutePath());
					bmpOld.compress(Bitmap.CompressFormat.JPEG, quality,
							new FileOutputStream(newFile));
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * 图片角圆化
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            5 10
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 位图圆角处理
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		try {

			Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);

			// 得到画布
			Canvas canvas = new Canvas(targetBitmap);

			// 创建画笔
			Paint paint = new Paint();
			paint.setAntiAlias(true);

			// 值越大角度越明显
			float roundPx = 10;
			float roundPy = 10;

			Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			RectF rectF = new RectF(rect);

			// 绘制
			canvas.drawARGB(0, 0, 0, 0);
			canvas.drawRoundRect(rectF, roundPx, roundPy, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return targetBitmap;

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 检查SD卡是否挂载
	 * 
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 从SD卡中删除指定目录下文件
	 * 
	 * @param context
	 * @param path
	 *            file:///sdcard/temp.jpg
	 */
	public static void deleteAllPhoto(String path) {
		if (checkSDCardAvailable()) {
			File file = new File(path);
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					f.delete();
				}
			} else {
				file.delete();
			}
		}
	}

	public static void deletePhotoAtPathAndName(String path, String fileName) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i].getName());
				if (files[i].getName().equals(fileName)) {
					files[i].delete();
				}
			}
		}
	}

	/**
	 * 根据文件路径+图片名称获得Bitmap对象
	 * 
	 * @param photoName
	 * @return
	 */
	public static Bitmap getPhotoFromSDCard(String path, String photoName) {
		Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName
				+ ".png");
		if (photoBitmap == null) {
			return null;
		} else {
			return photoBitmap;
		}
	}

	/**
	 * 根据给定文件路径+文件名判断SD卡是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean findPhotoFromSDCard(String path, String photoName) {
		boolean flag = false;
		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (dir.exists()) {
				File folders = new File(path);
				File photoFile[] = folders.listFiles();
				for (int i = 0; i < photoFile.length; i++) {
					String fileName = photoFile[i].getName().split("\\.")[0];
					if (fileName.equals(photoName)) {
						flag = true;
					}
				}
			} else {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 将位图存到指定空间
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static void savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File photoFile = new File(path, photoName);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(photoFile);
			if (photoBitmap != null) {
				if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
				}
			}
		} catch (FileNotFoundException e) {
			photoFile.delete();
			e.printStackTrace();
		} catch (IOException e) {
			photoFile.delete();
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Bitmap getRoundedBitmap(Bitmap bitmap) {
		return getRoundedCornerBitmap(bitmap, Integer.MAX_VALUE);
	}

	/**
	 * 将图片存入缓存目录
	 */
	public static void save2Cache(Context context, String cover, Bitmap bitmap) {
		File file = new File(context.getCacheDir(), cover + ".png");
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			if (bitmap != null) {
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
				}
			}
		} catch (FileNotFoundException e) {
			file.delete();
			e.printStackTrace();
		} catch (IOException e) {
			file.delete();
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 重新指定Drawable大小
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawable2Bitmap(drawable);
		Matrix matrix = new Matrix();
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		matrix.postScale(sx, sy);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(newbmp);
	}

	/**
	 * 重新指定图片大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap newbmp;
		try {
			// if (h >= w) {
			// if (height <= h) {
			Matrix matrix = new Matrix();
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleHeight, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			return newbmp;
			// }
			// } else {
			// if (width <= w) {
			// Matrix matrix = new Matrix();
			// float scaleWidth = ((float) width / w);
			// matrix.postScale(scaleWidth, scaleWidth);
			// newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
			// true);
			// return newbmp;
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * view转Bitmap
	 * 
	 * @param context
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view) {
		Bitmap bitmap = null;
		try {
			int width = view.getWidth();
			int height = view.getHeight();
			if (width != 0 && height != 0) {
				bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				view.layout(0, 0, width, height);
				view.setBackgroundColor(Color.WHITE);
				view.draw(canvas);
			}
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}
}
