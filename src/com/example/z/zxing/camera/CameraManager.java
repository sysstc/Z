/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.z.zxing.camera;

import java.io.IOException;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;

/**
 * 
 * 邮箱: 1076559197@qq.com | tauchen1990@gmail.com
 * 
 * 作者: 陈涛
 * 
 * 日期: 2014年8月20日
 * 
 * 描述: 该类主要负责对相机的操作
 * 
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

	private final CameraConfigurationManager configManager;

	private Camera camera;
	private boolean initialized;

	public CameraManager(Context context) {
		this.configManager = new CameraConfigurationManager(context);
	}

	public synchronized void openDriver() throws IOException {
		if (camera == null) {
			camera = Camera.open();
			if (camera == null) {
				throw new IOException();
			}
		}
		if (!initialized) {
			initialized = true;
			configManager.initFromCameraParameters(camera);
		}
		configManager.setDesiredCameraParameters(camera, false);
	}

	public synchronized boolean isOpen() {
		return camera != null;
	}

	public Camera getCamera() {
		return camera;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	/**
	 * 获取相机分辨率
	 * 
	 * @return
	 */
	public Point getCameraResolution() {
		return configManager.getCameraResolution();
	}
}
