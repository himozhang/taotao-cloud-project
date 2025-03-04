package com.taotao.cloud.web.sign.util.security;


/**
 * <p>二进制转换工具类</p>
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 14:44:16
 */
public class Hex2Util {

	/**
	 * 二进位组转十六进制字符串
	 *
	 * @param buf 二进位组
	 * @return 十六进制字符串
	 */
	public static String parseByte2HexStr(byte[] buf) {
		StringBuilder sb = new StringBuilder();
		for (byte b : buf) {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 十六进制字符串转二进位组
	 *
	 * @param hexStr 十六进制字符串
	 * @return 二进位组
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1) {
			return new byte[0];
		}
		byte[] result = new byte[hexStr.length() / 2];
		int len = hexStr.length() / 2;
		for (int i = 0; i < len; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

}
