package com.milan.inzaghi09.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

	public static String encoder(String psd) {
		try {
			// 0加盐
			psd = psd + "kaka&modric";

			// 1指定算法
			MessageDigest digest = MessageDigest.getInstance("MD5");

			// 2将要加密的字符串转为字符数组,然后进行随机哈希
			byte[] bys = digest.digest(psd.getBytes());

			// 3遍历字符数组，拼接成md5码
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bys) {

				// 将与运算后的数字num转为16进制字符
				int num = b & 0xff;
				String hexString = Integer.toHexString(num);
				// 如果长度小于2，前面加0保证md5码为32位
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				// 拼接
				stringBuffer.append(hexString);

			}

			return stringBuffer.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
}
