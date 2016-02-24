package com.example.z.http;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.z.R;
import com.example.z.app.App;
import com.example.z.common.HttpManager;
import com.example.z.constant.MyCookieStore;
import com.example.z.model.RequestModel;
import com.example.z.network.TANetWorkUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 接口请求类
 */
@SuppressLint("HandlerLeak")
public class InterfaceServer {

	private static final String TAG = "InterfaceServer";

	private static final class Holder {
		static final InterfaceServer mInstance = new InterfaceServer();
	}

	public static InterfaceServer getInstance() {
		return Holder.mInstance;
	}

	public void requestInterface(String server_api_url, RequestModel model, RequestCallBack<String> callback) {
		requestInterface(HttpMethod.POST, server_api_url, model, true, callback);
	}

	public void requestInterface(HttpMethod httpMethod, String server_api_url, RequestModel model,
			RequestCallBack<String> callback) {
		requestInterface(httpMethod, server_api_url, model, true, callback);
	}

	public void requestInterface(HttpMethod httpMethod, String server_api_url, RequestModel model, boolean isNeedProxy,
			RequestCallBack<String> callback) {
		if (TANetWorkUtil.isNetworkAvailable(App.getApplication())) {
			if (model != null) {
				String requestUrl = null;
				RequestParams requestParams = null;
				if (httpMethod == HttpMethod.POST) {
					requestUrl = server_api_url;
					requestParams = getRequestParamsNoJson(requestUrl, model);
				} else if (httpMethod == HttpMethod.GET) {
					requestUrl = getRequestUrl(server_api_url, model);
					Log.i(TAG, requestUrl);
				}

				if (model.isCookie()) {
					HttpManager.getHttpUtils().configCookieStore(MyCookieStore.cookieStore);
				}

				HttpManager.getHttpUtils().send(httpMethod, requestUrl, requestParams, callback);
			}
		} else {
			callback.onFailure(null, App.getApplication().getString(R.string.toast_checknet));
		}
	}

	private String getRequestUrl(String server_api_url, RequestModel model) {
		StringBuilder url = new StringBuilder(server_api_url + "?");
		if (model != null && model.getData() != null) {
			Map<String, Object> mapData = (Map<String, Object>) model.getData();

			for (Entry<String, Object> entry : mapData.entrySet()) {
				url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}

			url.setLength(url.length() - 1);
		}

		return url.toString();
	}

	private RequestParams getRequestParamsNoJson(String server_api_url, RequestModel model) {
		
		RequestParams requestParams = new RequestParams();
		Map<String, Object> dataString = model.getData();
		if (dataString != null) {
			Map<String, Object> mapData = dataString;
			for (Entry<String, Object> entry : mapData.entrySet()) {
				String key = (entry.getKey()) == null ? "" : (entry.getKey());
				String value = (entry.getValue()) == null ? "" : (String.valueOf(entry.getValue()));
				requestParams.addQueryStringParameter(key, value);
			}

		}

		Map<String, File> dataFile = model.getDataFile();
		if (dataFile != null) {
			for (Entry<String, File> itemFile : dataFile.entrySet()) {
				if (itemFile != null) {
					requestParams.addBodyParameter(itemFile.getKey(), itemFile.getValue());
				}
			}
		}

		Log.i(TAG, getRequestUrl(server_api_url, model));

		return requestParams;
	}

}
