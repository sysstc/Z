package com.example.z.photo.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 图片加载类
 * 
 * @author Administrator
 * 
 */
public class ImageLoader {

	private static ImageLoader mImageLoader;
	/**
	 * 图片缓存的核心类
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static final int DEAFULT_THREAD_COUNT = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 后台轮询线程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;
	/**
	 * UI线程中的Handler
	 */
	private Handler mUIHandler;
	/**
	 * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
	 */
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	/**
	 * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
	 */
	private Semaphore mSemaphoreThreadPool;

	public enum Type {
		FIFO, LIFO
	}

	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static ImageLoader getInstance() {
		if (mImageLoader == null) {
			synchronized (ImageLoader.class) {
				if (mImageLoader == null) {
					mImageLoader = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
				}
			}
		}
		return mImageLoader;
	}

	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	/**
	 * 初始化
	 * 
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type) {
		// 后台轮询线程
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						try {
							// 线程池去取出一个任务进行执行
							mThreadPool.execute(getTask());
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				};
				// 释放一个信号量
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			}
		};
		mPoolThread.start();

		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;

		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

		// 创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;

		mSemaphoreThreadPool = new Semaphore(threadCount);
	}

	/**
	 * 根据path为imageview设置图片
	 * 
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView) {
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// 获取得到的图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageView = holder.imageview;
					String paths = holder.path;
					// 将path与getTag存储的路径进行比较
					if (imageView.getTag().toString().equals(paths)) {
						imageView.setImageBitmap(bm);
					}
				}
			};
		}

		// 根据path在缓存中获取bitmap
		Bitmap bm = getBitmapFormLruCache(path);

		if (bm != null) {
			refreashBitmap(path, imageView, bm);
		} else {
			addTask(new Runnable() {
				@Override
				public void run() {
					// 加载图片
					// 图片压缩
					// 1、获取图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					// 2、压缩图片
					Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
					// 3、把图片加入到缓存
					addBitmapToLruCache(path, bm);
					refreashBitmap(path, imageView, getBitmapFormLruCache(path));
					mSemaphoreThreadPool.release();
				}
			});
		}
	}

	/**
	 * 从任务队列取出一个方法
	 * 
	 * @return
	 */
	private Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	public static ImageLoader getInstance(int threadCount, Type type) {
		if (mImageLoader == null) {
			synchronized (ImageLoader.class) {
				if (mImageLoader == null) {
					mImageLoader = new ImageLoader(threadCount, type);
				}
			}
		}
		return mImageLoader;
	}

	private void refreashBitmap(final String path, final ImageView imageView, Bitmap bitmap) {
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bitmap;
		holder.imageview = imageView;
		holder.path = path;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	/**
	 * 将图片加入到LruCache
	 * 
	 * @param path
	 * @param bm
	 */
	private void addBitmapToLruCache(String path, Bitmap bm) {
		if (getBitmapFormLruCache(path) == null) {
			if (bm != null) {
				mLruCache.put(path, bm);
			}
		}
	}

	/**
	 * 根据图片需要的宽和高对图片进行压缩
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
		// 获取图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = caculateInSampleSize(options, width, height);

		// 使用获取到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
	 * 
	 * @param options
	 * @param width
	 * @param height
	 * @return
	 */
	private int caculateInSampleSize(Options options, int reqWidth, int reqHeight) {

		int width = options.outWidth;
		int height = options.outHeight;

		int inSampleSize = 1;

		if (width > reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width * 1.0f / reqWidth);
			int heightRadio = Math.round(height * 1.0f / reqHeight);

			inSampleSize = Math.max(widthRadio, heightRadio);
		}

		return inSampleSize;
	}

	/**
	 * 根据ImageView获取适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();

		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

		LayoutParams lp = imageView.getLayoutParams();
		int width = imageView.getWidth();// 获取imageview的实际宽度
		if (width <= 0) {
			width = lp.width;// 获取imageview在layout中声明的宽度
		}
		if (width <= 0) {
			// width = imageView.getMaxWidth();// 检查最大值
			width = getImageViewFieldValue(imageView, "mMaxWidth");// 检查最大值
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}

		int height = imageView.getHeight();// 获取imageview的实际高度
		if (height <= 0) {
			height = lp.height;// 获取imageview在layout中声明的高度
		}
		if (height <= 0) {
			// height = imageView.getMaxHeight();// 检查最大值
			height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}

		imageSize.width = width;
		imageSize.height = height;

		return imageSize;
	}

	/**
	 * 通过反射获取imageview的某个属性值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;

		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);

			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	private synchronized void addTask(Runnable runnable) {
		try {
			if (mPoolThreadHandler == null)
				mSemaphorePoolThreadHandler.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mTaskQueue.add(runnable);
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 根据path在缓存中获取bitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFormLruCache(String key) {
		return mLruCache.get(key);
	}

	private class ImageSize {
		int width;
		int height;
	}

	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageview;
		String path;
	}
}
