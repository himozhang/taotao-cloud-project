package com.taotao.cloud.goods.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.goods.api.web.dto.GoodsParamsDTO;
import com.taotao.cloud.goods.api.web.vo.ParameterGroupVO;
import com.taotao.cloud.goods.biz.model.entity.CategoryParameterGroup;
import com.taotao.cloud.goods.biz.model.entity.Goods;
import com.taotao.cloud.goods.biz.model.entity.Parameters;
import com.taotao.cloud.goods.biz.mapper.ICategoryParameterGroupMapper;
import com.taotao.cloud.goods.biz.mapstruct.IParametersMapStruct;
import com.taotao.cloud.goods.biz.service.ICategoryParameterGroupService;
import com.taotao.cloud.goods.biz.service.IGoodsService;
import com.taotao.cloud.goods.biz.service.IParametersService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类绑定参数组接口实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 17:02:04
 */
@AllArgsConstructor
@Service
public class CategoryParameterGroupServiceImpl extends
	ServiceImpl<ICategoryParameterGroupMapper, CategoryParameterGroup> implements
	ICategoryParameterGroupService {

	/**
	 * 商品参数服务
	 */
	private final IParametersService parametersService;
	/**
	 * 商品服务
	 */
	private final IGoodsService goodsService;

	@Override
	public List<ParameterGroupVO> getCategoryParams(Long categoryId) {
		//根据id查询参数组
		List<CategoryParameterGroup> groups = this.getCategoryGroup(categoryId);
		//查询参数
		List<Parameters> params = parametersService.list(
			new QueryWrapper<Parameters>().eq("category_id", categoryId));
		//组合参数vo
		return convertParamList(groups, params);
	}

	@Override
	public List<CategoryParameterGroup> getCategoryGroup(Long categoryId) {
		return this.list(new QueryWrapper<CategoryParameterGroup>().eq("category_id", categoryId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean updateCategoryGroup(CategoryParameterGroup categoryParameterGroup) {
		CategoryParameterGroup origin = this.getById(categoryParameterGroup.getId());
		if (origin == null) {
			throw new BusinessException(ResultEnum.CATEGORY_PARAMETER_NOT_EXIST);
		}

		LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.select(Goods::getId, Goods::getParams);
		queryWrapper.like(Goods::getParams, origin.getId());
		List<Map<String, Object>> goodsList = this.goodsService.listMaps(queryWrapper);

		for (Map<String, Object> goods : goodsList) {
			String params = (String) goods.get("params");
			List<GoodsParamsDTO> goodsParamsDTOS = JSONUtil.toList(params, GoodsParamsDTO.class);
			List<GoodsParamsDTO> goodsParamsDTOList = goodsParamsDTOS.stream()
				.filter(i -> i.getGroupId() != null && i.getGroupId().equals(origin.getId()))
				.toList();
			for (GoodsParamsDTO goodsParamsDTO : goodsParamsDTOList) {
				goodsParamsDTO.setGroupName(categoryParameterGroup.getGroupName());
			}

			this.goodsService.updateGoodsParams(Long.valueOf(goods.get("id").toString()),
				JSONUtil.toJsonStr(goodsParamsDTOS));
		}

		return this.updateById(categoryParameterGroup);
	}

	@Override
	public Boolean deleteByCategoryId(Long categoryId) {
		return this.baseMapper.delete(new LambdaUpdateWrapper<CategoryParameterGroup>().eq(
			CategoryParameterGroup::getCategoryId, categoryId)) > 0;
	}

	/**
	 * 拼装参数组和参数的返回值
	 *
	 * @param groupList 参数组list
	 * @param paramList 商品参数list
	 * @return 参数组和参数的返回值
	 */
	public List<ParameterGroupVO> convertParamList(List<CategoryParameterGroup> groupList,
		List<Parameters> paramList) {
		Map<Long, List<Parameters>> map = new HashMap<>(paramList.size());
		for (Parameters param : paramList) {
			List<Parameters> list = map.get(param.getGroupId());
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(param);
			map.put(param.getGroupId(), list);
		}

		List<ParameterGroupVO> resList = new ArrayList<>();
		for (CategoryParameterGroup group : groupList) {
			ParameterGroupVO groupVo = new ParameterGroupVO();
			groupVo.setGroupId(group.getId());
			groupVo.setGroupName(group.getGroupName());
			groupVo.setParams(
				map.get(group.getId()) == null ? new ArrayList<>()
					: IParametersMapStruct.INSTANCE.parametersToParametersVOs(
						map.get(group.getId())));
			resList.add(groupVo);
		}
		return resList;
	}
}
