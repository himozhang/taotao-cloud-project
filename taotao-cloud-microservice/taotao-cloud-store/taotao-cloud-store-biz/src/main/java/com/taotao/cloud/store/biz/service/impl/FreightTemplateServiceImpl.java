package com.taotao.cloud.store.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.CachePrefix;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.common.model.SecurityUser;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.common.utils.bean.BeanUtil;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.taotao.cloud.store.api.web.vo.FreightTemplateChildVO;
import com.taotao.cloud.store.api.web.vo.FreightTemplateInfoVO;
import com.taotao.cloud.store.biz.model.entity.FreightTemplate;
import com.taotao.cloud.store.biz.model.entity.FreightTemplateChild;
import com.taotao.cloud.store.biz.mapper.FreightTemplateMapper;
import com.taotao.cloud.store.biz.mapstruct.IFreightTemplateChildMapStruct;
import com.taotao.cloud.store.biz.service.FreightTemplateChildService;
import com.taotao.cloud.store.biz.service.FreightTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺运费模板业务层实现
 *
 * @author shuigedeng
 * @version 2022.06
 * @since 2022-06-01 15:05:19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FreightTemplateServiceImpl extends ServiceImpl<FreightTemplateMapper, FreightTemplate> implements
	FreightTemplateService {
	/**
	 * 配送子模板
	 */
	@Autowired
	private FreightTemplateChildService freightTemplateChildService;
	/**
	 * 缓存
	 */
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public List<FreightTemplateInfoVO> getFreightTemplateList(String storeId) {
		//先从缓存中获取运费模板，如果有则直接返回，如果没有则查询数据后再返回
		List<FreightTemplateInfoVO> list = (List<FreightTemplateInfoVO>) redisRepository.get(CachePrefix.SHIP_TEMPLATE.getPrefix() + storeId);
		if (list != null) {
			return list;
		}
		list = new ArrayList<>();
		//查询运费模板
		LambdaQueryWrapper<FreightTemplate> lambdaQueryWrapper = Wrappers.lambdaQuery();
		lambdaQueryWrapper.eq(FreightTemplate::getStoreId, storeId);
		List<FreightTemplate> freightTemplates = this.baseMapper.selectList(lambdaQueryWrapper);
		if (!freightTemplates.isEmpty()) {
			//如果模板不为空则查询子模板信息
			for (FreightTemplate freightTemplate : freightTemplates) {
				FreightTemplateInfoVO freightTemplateInfoVO = new FreightTemplateInfoVO();
				BeanUtil.copyProperties(freightTemplate, freightTemplateInfoVO);
				List<FreightTemplateChild> freightTemplateChildren = freightTemplateChildService.getFreightTemplateChild(freightTemplate.getId());
				if (!freightTemplateChildren.isEmpty()) {

					freightTemplateInfoVO.setFreightTemplateChildList(IFreightTemplateChildMapStruct.INSTANCE.freightTemplateChildListToFreightTemplateChildVoList(freightTemplateChildren));
				}
				list.add(freightTemplateInfoVO);
			}
		}
		redisRepository.set(CachePrefix.SHIP_TEMPLATE.getPrefix() + storeId, list);
		return list;

	}

	@Override
	public IPage<FreightTemplate> getFreightTemplate(PageParam pageParam) {
		LambdaQueryWrapper<FreightTemplate> lambdaQueryWrapper = Wrappers.lambdaQuery();
		lambdaQueryWrapper.eq(FreightTemplate::getStoreId, SecurityUtil.getCurrentUser().getStoreId());
		return this.baseMapper.selectPage(pageParam.buildMpPage(), lambdaQueryWrapper);
	}

	@Override
	public FreightTemplateInfoVO getFreightTemplate(Long id) {
		FreightTemplateInfoVO freightTemplateInfoVO = new FreightTemplateInfoVO();
		//获取运费模板
		FreightTemplate freightTemplate = this.getById(id);
		if (freightTemplate != null) {
			//复制属性
			BeanUtils.copyProperties(freightTemplate, freightTemplateInfoVO);
			//填写运费模板子内容
			List<FreightTemplateChild> freightTemplateChildList = freightTemplateChildService.getFreightTemplateChild(id);
			freightTemplateInfoVO.setFreightTemplateChildList(IFreightTemplateChildMapStruct.INSTANCE.freightTemplateChildListToFreightTemplateChildVoList(freightTemplateChildList));
		}
		return freightTemplateInfoVO;
	}

	@Override
	public FreightTemplateInfoVO addFreightTemplate(FreightTemplateInfoVO freightTemplateInfoVO) {
		//获取当前登录商家账号
		SecurityUser tokenUser = SecurityUtil.getCurrentUser();
		FreightTemplate freightTemplate = new FreightTemplate();
		//设置店铺ID
		freightTemplateInfoVO.setStoreId(tokenUser.getStoreId());
		//复制属性
		BeanUtils.copyProperties(freightTemplateInfoVO, freightTemplate);
		//添加运费模板
		this.save(freightTemplate);
		//给子模板赋父模板的id
		List<FreightTemplateChildVO> list = new ArrayList<>();
		//如果子运费模板不为空则进行新增
		if (freightTemplateInfoVO.getFreightTemplateChildList() != null) {
			for (FreightTemplateChildVO freightTemplateChild : freightTemplateInfoVO.getFreightTemplateChildList()) {
				freightTemplateChild.setFreightTemplateId(freightTemplate.getId());
				list.add(freightTemplateChild);
			}
			List<FreightTemplateChild> freightTemplateChildren = IFreightTemplateChildMapStruct.INSTANCE.freightTemplateChildVOListTofreightTemplateChildList(list);
			//添加运费模板子内容
			freightTemplateChildService.addFreightTemplateChild(freightTemplateChildren);
		}

		//更新缓存
		redisRepository.del(CachePrefix.SHIP_TEMPLATE.getPrefix() + tokenUser.getStoreId());
		return freightTemplateInfoVO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public FreightTemplateInfoVO editFreightTemplate(FreightTemplateInfoVO freightTemplateInfoVO) {
		//获取当前登录商家账号
		SecurityUser tokenUser = SecurityUtil.getCurrentUser();
		if (freightTemplateInfoVO.getId().equals(tokenUser.getStoreId())) {
			throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
		}

		FreightTemplate freightTemplate = new FreightTemplate();
		//复制属性
		BeanUtils.copyProperties(freightTemplateInfoVO, freightTemplate);
		//修改运费模板
		this.updateById(freightTemplate);
		//删除模板子内容
		freightTemplateChildService.removeFreightTemplate(freightTemplateInfoVO.getId());
		//给子模板赋父模板的id
		List<FreightTemplateChildVO> list = new ArrayList<>();
		for (FreightTemplateChildVO freightTemplateChild : freightTemplateInfoVO.getFreightTemplateChildList()) {
			freightTemplateChild.setFreightTemplateId(freightTemplate.getId());
			list.add(freightTemplateChild);
		}
		List<FreightTemplateChild> freightTemplateChildren = IFreightTemplateChildMapStruct.INSTANCE.freightTemplateChildVOListTofreightTemplateChildList(list);
		//添加模板子内容
		freightTemplateChildService.addFreightTemplateChild(freightTemplateChildren);
		//更新缓存
		redisRepository.del(CachePrefix.SHIP_TEMPLATE.getPrefix() + tokenUser.getStoreId());
		return null;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeFreightTemplate(Long id) {
		//获取当前登录商家账号
		SecurityUser tokenUser = SecurityUtil.getCurrentUser();
		LambdaQueryWrapper<FreightTemplate> lambdaQueryWrapper = Wrappers.lambdaQuery();
		lambdaQueryWrapper.eq(FreightTemplate::getStoreId, tokenUser.getStoreId());
		lambdaQueryWrapper.eq(FreightTemplate::getId, id);
		//如果删除成功则删除运费模板子项
		if (this.remove(lambdaQueryWrapper)) {
			redisRepository.del(CachePrefix.SHIP_TEMPLATE.getPrefix() + tokenUser.getStoreId());
			return freightTemplateChildService.removeFreightTemplate(id);
		}
		return false;
	}
}
