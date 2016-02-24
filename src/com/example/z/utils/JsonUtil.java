package com.example.z.utils;

import com.alibaba.fastjson.JSON;

public class JsonUtil
{

	private JsonUtil()
	{
	}

	public static <T> T json2Object(String json, Class<T> clazz)
	{
		// return new Gson().fromJson(json, clazz);
		return JSON.parseObject(json, clazz);
	}

	public static String object2Json(Object obj)
	{
		// return new Gson().toJson(obj);
		return JSON.toJSONString(obj);
	}

}
