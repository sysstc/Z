package com.example.z.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.z.R;
import com.example.z.app.App;
import com.example.z.common.HttpManager;
import com.example.z.constant.MyCookieStore;
import com.example.z.customview.CustomDialog;
import com.example.z.model.BaseModel;
import com.example.z.utils.SDToast;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class ABRequestCallBack<E extends BaseModel> extends RequestCallBack<String> {

	private boolean isShowDialog;
	private Dialog mDialog;

	public ABRequestCallBack() {

	}

	public ABRequestCallBack(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(ResponseInfo<String> responseInfo) {
		Class<E> clazz = null;
		// 获得泛型class，并将json解析成实体
		Type[] types = getType(ABRequestCallBack.this.getClass());
		if (types != null && types.length == 1) {
			clazz = (Class<E>) types[0];
		}
		E actModel = parseObject(clazz, responseInfo);
		onSuccessModel(actModel);
		onFinish();

		DefaultHttpClient dh = (DefaultHttpClient) HttpManager.getHttpUtils().getHttpClient();
		MyCookieStore.cookieStore = dh.getCookieStore();
		// CookieStore cs = dh.getCookieStore();
		// List<Cookie> cookies = cs.getCookies();
		// String aa = null;
		// for (int i = 0; i < cookies.size(); i++) {
		// if
		// ("JSESSIONID".equals(cookies.get(i).getName()))
		// {
		// aa = cookies.get(i).getValue();
		// break;
		// }
		// }
		// Log.d("jack", "比较sessionid" + aa);
	}

	@Override
	public void onFailure(HttpException error, String msg) {
		onFinish();
		SDToast.showToast(App.getApplication().getString(R.string.toast_again_request));
	}

	public void onSuccessModel(E actModel) {

	}

	@SuppressWarnings("hiding")
	private <E> E parseObject(Class<E> clazz, ResponseInfo<String> responseInfo) {
		if (responseInfo != null && clazz != null) {
			// fastJson不能直接解析服务端返回的Json，只需要进行以下处理就可以解决
			String result = responseInfo.result;
			result = result.replaceAll("\\\\", "");
			result = result.replaceAll(":\"\\{", ":\\{");
			result = result.replaceAll("\\}\"", "\\}");
			// String s = notice.getContent().toString().replaceAll("&quot;",
			// "\"")
			// .replaceAll("&lt;", "<").replaceAll("&gt;",
			// ">").replaceAll("&amp;", "&");
			Log.i("ABRequestCallBack", result);
			try {
				return JSON.parseObject(result, clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Type[] getType(Class<?> clazz) {
		Type[] types = null;
		if (clazz != null) {
			types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		}
		return types;
	}

	@Override
	public void onCancelled() {
		onFinish();
	}

	@Override
	public void onStart() {
		if ((!this.isShowDialog) && mDialog != null) {
			mDialog = CustomDialog.heartProgressBar();
		}

	}

	public void onFinish() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

}
