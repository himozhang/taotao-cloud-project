/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taotao.cloud.ip2region.model;

import com.taotao.cloud.common.constant.StrPool;
import com.taotao.cloud.common.utils.lang.StringUtil;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * ip 信息详情
 *
 * @author shuigedeng
 * @version 2022.06
 * @since 2022-06-28 17:43:26
 */
public class IpInfo implements Serializable {

	/**
	 * 国家
	 */
	private String country;
	/**
	 * 区域
	 */
	private String region;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 运营商
	 */
	private String isp;

	/**
	 * 拼接完整的地址
	 *
	 * @return address
	 */
	public String getAddress() {
		Set<String> regionSet = new LinkedHashSet<>();
		regionSet.add(country);
		regionSet.add(region);
		regionSet.add(province);
		regionSet.add(city);
		regionSet.removeIf(Objects::isNull);
		return StringUtil.join(regionSet, StrPool.EMPTY);
	}

	/**
	 * 拼接完整的地址
	 *
	 * @return address
	 */
	public String getAddressAndIsp() {
		Set<String> regionSet = new LinkedHashSet<>();
		regionSet.add(country);
		regionSet.add(region);
		regionSet.add(province);
		regionSet.add(city);
		regionSet.add(isp);
		regionSet.removeIf(Objects::isNull);
		return StringUtil.join(regionSet, StrPool.SPACE);
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}
}
