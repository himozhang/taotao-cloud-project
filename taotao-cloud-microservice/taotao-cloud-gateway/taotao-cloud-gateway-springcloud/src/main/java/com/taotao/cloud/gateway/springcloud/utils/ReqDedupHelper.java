package com.taotao.cloud.gateway.springcloud.utils;

import com.alibaba.fastjson2.JSON;
import com.taotao.cloud.common.utils.log.LogUtil;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import javax.xml.bind.DatatypeConverter;

public class ReqDedupHelper {

	/**
	 * @param reqJSON     请求的参数，这里通常是JSON
	 * @param excludeKeys 请求参数里面要去除哪些字段再求摘要
	 * @return 去除参数的MD5摘要
	 */
	public String dedupParamMD5(final String reqJSON, String... excludeKeys) {
		String decreptParam = reqJSON;

		TreeMap paramTreeMap = JSON.parseObject(decreptParam, TreeMap.class);
		if (excludeKeys != null) {
			List<String> dedupExcludeKeys = Arrays.asList(excludeKeys);
			if (!dedupExcludeKeys.isEmpty()) {
				for (String dedupExcludeKey : dedupExcludeKeys) {
					paramTreeMap.remove(dedupExcludeKey);
				}
			}
		}

		String paramTreeMapJSON = JSON.toJSONString(paramTreeMap);
		String md5deDupParam = jdkMD5(paramTreeMapJSON);
		LogUtil.debug("md5deDupParam = {}, excludeKeys = {} {}", md5deDupParam,
			Arrays.deepToString(excludeKeys), paramTreeMapJSON);
		return md5deDupParam;
	}

	private static String jdkMD5(String src) {
		String res = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] mdBytes = messageDigest.digest(src.getBytes());
			res = DatatypeConverter.printHexBinary(mdBytes);
		} catch (Exception e) {
			LogUtil.error("", e);
		}
		return res;
	}

	/**
	 * String userId= "12345678";//用户
	 * String method = "pay";//接口名
	 * String dedupMD5 = new ReqDedupHelper().dedupParamMD5(req,"requestTime");//计算请求参数摘要，其中剔除里面请求时间的干扰
	 * String KEY = "dedup:U=" + userId + "M=" + method + "P=" + dedupMD5;
	 *
	 * long expireTime =  1000;// 1000毫秒过期，1000ms内的重复请求会认为重复
	 * long expireAt = System.currentTimeMillis() + expireTime;
	 * String val = "expireAt@" + expireAt;
	 *
	 * // NOTE:直接SETNX不支持带过期时间，所以设置+过期不是原子操作，极端情况下可能设置了就不过期了，后面相同请求可能会误以为需要去重，所以这里使用底层API，保证SETNX+过期时间是原子操作
	 * Boolean firstSet = stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(KEY.getBytes(), val.getBytes(), Expiration.milliseconds(expireTime),
	 *         RedisStringCommands.SetOption.SET_IF_ABSENT));
	 *
	 * final boolean isConsiderDup;
	 * if (firstSet != null && firstSet) {
	 *     isConsiderDup = false;
	 * } else {
	 *     isConsiderDup = true;
	 * }
	 */
}
