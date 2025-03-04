package com.taotao.cloud.goods.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.disruptor.util.StringUtils;
import com.taotao.cloud.goods.api.web.query.SpecificationPageQuery;
import com.taotao.cloud.goods.biz.model.entity.CategorySpecification;
import com.taotao.cloud.goods.biz.model.entity.Specification;
import com.taotao.cloud.goods.biz.mapper.ISpecificationMapper;
import com.taotao.cloud.goods.biz.service.ICategorySpecificationService;
import com.taotao.cloud.goods.biz.service.ISpecificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品规格业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 17:02:55
 */
@AllArgsConstructor
@Service
public class SpecificationServiceImpl extends
	ServiceImpl<ISpecificationMapper, Specification> implements ISpecificationService {

	/**
	 * 分类-规格绑定服务
	 */
	private final ICategorySpecificationService categorySpecificationService;
	/**
	 * 分类服务
	 */
	private final CategoryServiceImpl categoryService;

	@Override
	public Boolean deleteSpecification(List<Long> ids) {
		boolean result = false;
		for (Long id : ids) {
			//如果此规格绑定分类则不允许删除
			List<CategorySpecification> list = categorySpecificationService.list(
				new QueryWrapper<CategorySpecification>().eq("specification_id", id));

			if (!list.isEmpty()) {
				List<Long> categoryIds = new ArrayList<>();
				list.forEach(item -> categoryIds.add(item.getCategoryId()));
				throw new BusinessException(ResultEnum.SPEC_DELETE_ERROR.getCode(),
					JSONUtil.toJsonStr(categoryService.getCategoryNameByIds(categoryIds)));
			}
			//删除规格
			result = this.removeById(id);
		}
		return result;
	}

	@Override
	public IPage<Specification> getPage(SpecificationPageQuery specificationPageQuery) {
		LambdaQueryWrapper<Specification> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.like(StringUtils.isNotEmpty(specificationPageQuery.getSpecName()), Specification::getSpecName,
			specificationPageQuery.getSpecName());
		return this.page(specificationPageQuery.buildMpPage(), lambdaQueryWrapper);
	}

}
