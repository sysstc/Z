package com.example.z.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SDValidateUtil
{

	private static boolean match(String regex, String str)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/** url地址验证 */
	public static boolean isUrl(String url)
	{
		String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
		return match(regex, url);
	}

	/** 判断email格式是否正确 */
	public static boolean isEmail(String email)
	{
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return match(str, email);
	}

}
