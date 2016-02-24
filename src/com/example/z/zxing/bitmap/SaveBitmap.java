package com.example.z.zxing.bitmap;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera.Size;
import android.os.Environment;

import com.google.zxing.PlanarYUVLuminanceSource;

public class SaveBitmap {

	public void save(byte[] data, Size size, Rect cropRect) {
		// 生成bitmap
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, size.width, size.height, cropRect.left,
				cropRect.top, cropRect.width(), cropRect.height(), false);
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		int inputOffset = cropRect.top * size.width + cropRect.left;
		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			for (int x = 0; x < width; x++) {
				int grey = data[inputOffset + x] & 0xff;
				pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
			}
			inputOffset += size.width;
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		try {
			String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Z/";
			File root = new File(rootPath);
			if (!root.exists()) {
				root.mkdirs();
			}
			File f = new File(rootPath + "Qrcode.jpg");
			if (f.exists()) { 
				f.delete();
			}
			f.createNewFile();

			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
