package com.example.z.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 版权所有 (c)2012, 福州惟实信息科技有限公司
 * <p>
 * 文件名称 ：SHA1Utils.java
 * <p>
 * 内容摘要 ：
 * <p>
 * 作者 ：
 * <p>
 * 创建时间 ：2015-7-28上午10:02:03
 * <p>
 * 当前版本号：v1.0
 * <p>
 * 历史记录 :
 * <p>
 * 日期 : 2015-7-28上午10:02:03
 * <p>
 * 修改人：陈洪景
 * <p>
 * 描述 :随机数工具类
 */
public class SHA1Utils {

	public static String buildRandomCode() {
		String[] readomWord = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "a", "b", "c",
				"d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p" };
		String str = "";
		for (int i = 0; i < 8; i++) {
			int readomWordIndex = (int) (Math.random() * 32);
			String readom = readomWord[readomWordIndex];
			str = str + readom;
		}
		return str;
	}

	// SHA1 加密实例
	public static String encryptToSHA(String info) {
		byte[] digesta = null;
		try {
			// 得到一个SHA-1的消息摘要
			MessageDigest alga = MessageDigest.getInstance("SHA-1");
			// 添加要进行计算摘要的信息
			alga.update(info.getBytes());
			// 得到该摘要
			digesta = alga.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 将摘要转为字符串
		String rs = byte2hex(digesta);
		return rs;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs;
	}
}
