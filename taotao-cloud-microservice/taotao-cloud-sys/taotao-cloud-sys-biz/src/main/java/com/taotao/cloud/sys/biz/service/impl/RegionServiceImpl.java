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
package com.taotao.cloud.sys.biz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.taotao.cloud.common.constant.RedisConstant;
import com.taotao.cloud.common.http.HttpRequest;
import com.taotao.cloud.common.utils.common.IdGeneratorUtil;
import com.taotao.cloud.common.utils.common.OrikaUtil;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.core.configuration.OkhttpAutoConfiguration.OkHttpService;
import com.taotao.cloud.disruptor.util.StringUtils;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.taotao.cloud.sys.api.dubbo.IDubboRegionService;
import com.taotao.cloud.sys.api.web.vo.region.RegionParentVO;
import com.taotao.cloud.sys.api.web.vo.region.RegionTreeVO;
import com.taotao.cloud.sys.api.web.vo.region.RegionVO;
import com.taotao.cloud.sys.biz.model.entity.region.Region;
import com.taotao.cloud.sys.biz.mapper.IRegionMapper;
import com.taotao.cloud.sys.biz.mapstruct.IRegionMapStruct;
import com.taotao.cloud.sys.biz.repository.cls.RegionRepository;
import com.taotao.cloud.sys.biz.repository.inf.IRegionRepository;
import com.taotao.cloud.sys.biz.service.IRegionService;
import com.taotao.cloud.web.base.service.BaseSuperServiceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RegionServiceImpl
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2021-10-09 20:37:52
 */
@Service
public class RegionServiceImpl extends
	BaseSuperServiceImpl<IRegionMapper, Region, RegionRepository, IRegionRepository, Long>
	implements IRegionService {

	@Autowired
	private OkHttpService okHttpService;
	@Autowired
	private RedisRepository redisRepository;

	/**
	 * 同步请求地址
	 */
	private String syncUrl = "https://restapi.amap.com/v3/config/district?subdistrict=4&key=xxxxx";

	@Override
	public List<RegionParentVO> queryRegionByParentId(Long parentId) {
		LambdaQueryWrapper<Region> query = new LambdaQueryWrapper<>();
		query.eq(Region::getParentId, parentId);
		List<Region> sysRegions = getBaseMapper().selectList(query);
		List<RegionParentVO> result = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(sysRegions)) {
			sysRegions.forEach(sysRegion -> {
				RegionParentVO vo = new RegionParentVO(sysRegion.getId(),
					sysRegion.getName(),
					sysRegion.getCode(),
					new ArrayList<>());
				result.add(vo);
			});
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Region> getItem(Long parentId) {
		Object o = redisRepository.get(RedisConstant.REGIONS_PARENT_ID_KEY + parentId);
		if (Objects.nonNull(o)) {
			return (List<Region>) o;
		}

		LambdaQueryWrapper<Region> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(Region::getParentId, parentId);
		List<Region> regions = this.list(lambdaQueryWrapper);
		regions.sort(Comparator.comparing(Region::getOrderNum));

		redisRepository.setEx(RedisConstant.REGIONS_PARENT_ID_KEY + parentId, regions, 5 * 60);

		return regions;
	}

	@Override
	public Map<String, Object> getRegion(String cityCode, String townName) {
		//获取地址信息
		Region region = this.baseMapper.selectOne(new QueryWrapper<Region>()
			.eq("city_code", cityCode)
			.eq("name", townName));
		if (region != null) {
			//获取它的层级关系
			String path = region.getPath();
			String[] result = path.split(",");
			//因为有无用数据 所以先删除前两个
			result = ArrayUtils.remove(result, 0);
			result = ArrayUtils.remove(result, 0);
			//地址id
			StringBuilder regionIds = new StringBuilder();
			//地址名称
			StringBuilder regionNames = new StringBuilder();
			//循环构建新的数据
			for (String regionId : result) {
				Region reg = this.baseMapper.selectById(regionId);
				if (reg != null) {
					regionIds.append(regionId).append(",");
					regionNames.append(reg.getName()).append(",");
				}
			}
			regionIds.append(region.getId());
			regionNames.append(region.getName());
			//构建返回数据
			Map<String, Object> obj = new HashMap<>(2);
			obj.put("id", regionIds.toString());
			obj.put("name", regionNames.toString());

			return obj;
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RegionVO> getAllCity() {
		Object o = redisRepository.get(RedisConstant.REGIONS_ALL_CITY_KEY);
		if (Objects.nonNull(o)) {
			return (List<RegionVO>) o;
		}

		LambdaQueryWrapper<Region> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		//查询所有省市
		lambdaQueryWrapper.in(Region::getLevel, "city", "province");
		List<RegionVO> regionVOS = regionTree(this.list(lambdaQueryWrapper));

		redisRepository.setEx(RedisConstant.REGIONS_ALL_CITY_KEY, regionVOS, 5 * 60);

		return regionVOS;
	}

	private List<RegionVO> regionTree(List<Region> regions) {
		List<RegionVO> regionVOS = new ArrayList<>();
		regions.stream().filter(region -> ("province").equals(region.getLevel())).forEach(item -> {
			RegionVO vo = new RegionVO();
			OrikaUtil.copy(item, vo);
			regionVOS.add(vo);
		});

		regions.stream().filter(region -> ("city").equals(region.getLevel())).forEach(item -> {
			for (RegionVO region : regionVOS) {
				if (region.getId().equals(item.getParentId())) {
					RegionVO vo = new RegionVO();
					OrikaUtil.copy(item, vo);
					region.getChildren().add(vo);
				}
			}
		});
		return regionVOS;
	}

	@Override
	public List<RegionParentVO> tree() {
		LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Region::getParentId, 1);

		// 得到一级节点菜单列表
		List<Region> sysRegions = getBaseMapper().selectList(wrapper);
		List<RegionParentVO> vos = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(sysRegions)) {
			sysRegions.forEach(sysRegion -> {
				RegionParentVO vo = new RegionParentVO(sysRegion.getId(),
					sysRegion.getName(),
					sysRegion.getCode(),
					new ArrayList<>());
				vos.add(vo);
			});
		}

		if (vos.size() > 0) {
			vos.forEach(this::findAllChild);
		}
		return vos;
	}

	@Override
	public List<RegionTreeVO> treeOther() {
		LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.orderByDesc(Region::getCreateTime);
		List<Region> list = list(queryWrapper);

		return IRegionMapStruct.INSTANCE.regionListToVoList(list)
			.stream()
			.filter(Objects::nonNull)
			.peek(e -> {
				e.setKey(e.getId());
				e.setValue(e.getId());
				e.setTitle(e.getName());
			})
			.collect(Collectors.toList());
	}

	public void findAllChild(RegionParentVO vo) {
		LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Region::getParentId, vo.getId());
		List<Region> sysRegions = getBaseMapper().selectList(wrapper);
		List<RegionParentVO> regions = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(sysRegions)) {
			sysRegions.forEach(sysRegion -> {
				RegionParentVO region = new RegionParentVO(sysRegion.getId(),
					sysRegion.getName(),
					sysRegion.getCode(),
					new ArrayList<>());
				regions.add(region);
			});
		}

		vo.setChildren(regions);
		if (regions.size() > 0) {
			regions.forEach(this::findAllChild);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@SuppressWarnings("unchecked")
	public void synchronizationData(String url) {
		try {
			//读取数据
			String jsonString;
			if (Objects.nonNull(okHttpService)) {
				jsonString = okHttpService.url(StringUtils.isBlank(url) ? syncUrl : url)
					.get()
					.sync();
			} else {
				jsonString = HttpRequest.get(StringUtils.isBlank(url) ? syncUrl : url)
					.useConsoleLog()
					.executeAsync()
					.join()
					.asString();
			}

			if (StrUtil.isNotBlank(jsonString)) {
				//清空数据
				QueryWrapper<Region> queryWrapper = new QueryWrapper();
				queryWrapper.eq("version", "1");
				this.remove(queryWrapper);

				//清空缓存的地区数据
				redisRepository.del(
					redisRepository.keys(RedisConstant.REGIONS_PATTERN).toArray(new String[0]));

				// 构造存储数据库的对象集合
				List<Region> regions = this.initData(jsonString);
				for (int i = 0; i < (regions.size() / 100 + (regions.size() % 100 == 0 ? 0 : 1));
					i++) {
					int endPoint = Math.min((100 + (i * 100)), regions.size());
					this.saveOrUpdateBatch(regions.subList(i * 100, endPoint));
				}

				//重新设置缓存
				redisRepository.setEx(RedisConstant.REGIONS_KEY, jsonString, 30 * 24 * 60 * 60);
			}
		} catch (Exception e) {
			LogUtil.error("同步行政数据错误", e);
		}
	}

	/**
	 * 构造数据模型
	 *
	 * @param jsonString jsonString
	 */
	private List<Region> initData(String jsonString) {
		//最终数据承载对象
		List<Region> regions = new ArrayList<>();
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		//获取到国家及下面所有的信息 开始循环插入，这里可以写成递归调用，但是不如这样方便查看、理解
		JSONArray countryAll = jsonObject.getJSONArray("districts");
		for (int i = 0; i < countryAll.size(); i++) {
			JSONObject contry = countryAll.getJSONObject(i);
			String contryCode = contry.getString("citycode");
			String contryAdCode = contry.getString("adcode");
			String contryName = contry.getString("name");
			String contryCenter = contry.getString("center");
			String contryLevel = contry.getString("level");
			//1.插入国家
			Long id1 = insert(regions, null, contryCode, contryAdCode, contryName, contryCenter,
				contryLevel, i);
			JSONArray provinceAll = contry.getJSONArray("districts");

			for (int j = 0; j < provinceAll.size(); j++) {
				JSONObject province = provinceAll.getJSONObject(j);
				String citycode1 = province.getString("citycode");
				String adcode1 = province.getString("adcode");
				String name1 = province.getString("name");
				String center1 = province.getString("center");
				String level1 = province.getString("level");
				//1.插入省
				Long id2 = insert(regions, id1, citycode1, adcode1, name1, center1, level1, j,
					id1);
				JSONArray cityAll = province.getJSONArray("districts");

				for (int z = 0; z < cityAll.size(); z++) {
					JSONObject city = cityAll.getJSONObject(z);
					String citycode2 = city.getString("citycode");
					String adcode2 = city.getString("adcode");
					String name2 = city.getString("name");
					String center2 = city.getString("center");
					String level2 = city.getString("level");
					//2.插入市
					Long id3 = insert(regions, id2, citycode2, adcode2, name2, center2, level2, z,
						id1, id2);
					JSONArray districtAll = city.getJSONArray("districts");
					for (int w = 0; w < districtAll.size(); w++) {
						JSONObject district = districtAll.getJSONObject(w);
						String citycode3 = district.getString("citycode");
						String adcode3 = district.getString("adcode");
						String name3 = district.getString("name");
						String center3 = district.getString("center");
						String level3 = district.getString("level");
						//3.插入区县
						Long id4 = insert(regions, id3, citycode3, adcode3, name3, center3,
							level3, w, id1, id2, id3);
						//有需要可以继续向下遍历
						JSONArray streetAll = district.getJSONArray("districts");
						for (int r = 0; r < streetAll.size(); r++) {
							JSONObject street = streetAll.getJSONObject(r);
							String citycode4 = street.getString("citycode");
							String adcode4 = street.getString("adcode");
							String name4 = street.getString("name");
							String center4 = street.getString("center");
							String level4 = street.getString("level");
							//4.插入街道
							insert(regions, id4, citycode4, adcode4, name4, center4, level4, r, id1,
								id2, id3, id4);
						}
					}
				}
			}
		}
		return regions;
	}

	/**
	 * 公共的插入方法
	 *
	 * @param parentId 父id
	 * @param cityCode 城市编码
	 * @param code     区域编码  街道没有独有的code，均继承父类（区县）的code
	 * @param name     城市名称 （行政区名称）
	 * @param center   地理坐标
	 * @param level    country:国家 province:省份（直辖市会在province和city显示） city:市（直辖市会在province和city显示）
	 *                 district:区县 street:街道
	 * @param ids      地区id集合
	 */
	public Long insert(List<Region> regions, Long parentId, String cityCode, String code,
		String name, String center, String level, Integer order, Long... ids) {
		//  \"citycode\": [],\n" +
		//         "        \"adcode\": \"100000\",\n" +
		//         "        \"name\": \"中华人民共和国\",\n" +
		//         "        \"center\": \"116.3683244,39.915085\",\n" +
		//         "        \"level\": \"country\",\n" +
		Region record = new Region();
		if (!("[]").equals(code)) {
			record.setCode(code);
		}
		if (!("[]").equals(cityCode)) {
			record.setCityCode(cityCode);
		}
		String[] split = center.split(",");
		record.setLng(split[0]);
		record.setLat(split[1]);
		record.setLevel(level);
		record.setName(name);
		record.setParentId(parentId);
		record.setOrderNum(order);
		if ("100000".equals(code) && "country".equals(level)) {
			record.setId(1L);
		} else {
			record.setId(IdGeneratorUtil.getId());
		}

		StringBuilder megName = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			megName.append(ids[i]);
			if (i < ids.length - 1) {
				megName.append(",");
			}
		}
		record.setPath(megName.toString());
		regions.add(record);
		return record.getId();
	}

}
