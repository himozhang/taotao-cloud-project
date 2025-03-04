package com.taotao.cloud.sys.api.web.vo.setting;

import lombok.Data;

import java.io.Serializable;

/**
 * 积分签到设置
 */
@Data
public class PointSettingItemVO implements Comparable<PointSettingItemVO>, Serializable {

	/**
	 * 签到天数
	 */
	private Integer day;

	/**
	 * 赠送积分
	 */
	private Integer point;

	public Integer getPoint() {
		if (point == null || point < 0) {
			return 0;
		}
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	@Override
	public int compareTo(PointSettingItemVO pointSettingItem) {
		return this.day - pointSettingItem.getDay();
	}
}
