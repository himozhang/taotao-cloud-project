package com.taotao.cloud.demo.utils;

import com.taotao.cloud.common.utils.location.GeoUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * geo 测试
 *
 */
public class GeoUtilTest {

	@Test
	public void test() {
		// 坐标来自高德地图： https://lbs.amap.com/api/javascript-api/example/calcutation/calculate-distance-between-two-markers
		double distance = GeoUtil.getDistance(116.368904, 39.923423, 116.387271, 39.922501);
		// 距离为 1571
		Assert.assertEquals(1571, (long) distance);
	}

}
