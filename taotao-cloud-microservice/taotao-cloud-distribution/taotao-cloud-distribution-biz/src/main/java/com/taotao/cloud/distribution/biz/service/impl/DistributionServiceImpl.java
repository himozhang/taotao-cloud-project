package com.taotao.cloud.distribution.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.CachePrefix;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.common.utils.bean.BeanUtil;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.distribution.api.web.dto.DistributionApplyDTO;
import com.taotao.cloud.distribution.api.web.query.DistributionPageQuery;
import com.taotao.cloud.distribution.api.enums.DistributionStatusEnum;
import com.taotao.cloud.distribution.biz.model.entity.Distribution;
import com.taotao.cloud.distribution.biz.mapper.DistributionMapper;
import com.taotao.cloud.distribution.biz.service.DistributionService;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.taotao.cloud.sys.api.dto.DistributionSetting;
import com.taotao.cloud.sys.api.enums.SettingEnum;
import com.taotao.cloud.sys.api.feign.IFeignSettingService;
import com.taotao.cloud.sys.api.web.vo.setting.SettingVO;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分销员接口实现
 */
@Service
public class DistributionServiceImpl extends
	ServiceImpl<DistributionMapper, Distribution> implements
	DistributionService {

	/**
	 * 会员
	 */
	@Autowired
	private IFeignMemberService memberService;
	/**
	 * 缓存
	 */
	@Autowired
	private RedisRepository redisRepository;
	/**
	 * 设置
	 */
	@Autowired
	private IFeignSettingService settingService;

	@Override
	public IPage<Distribution> distributionPage(DistributionPageQuery distributionPageQuery,
												PageParam page) {
		return this.page(page.buildMpPage(), distributionPageQuery.queryWrapper());
	}

	@Override
	public Distribution getDistribution() {
		return this.getOne(new LambdaQueryWrapper<Distribution>().eq(Distribution::getMemberId,
			SecurityUtil.getUserId()));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Distribution applyDistribution(DistributionApplyDTO distributionApplyDTO) {

		//检查分销开关
		checkDistributionSetting();

		//判断用户是否申请过分销
		Distribution distribution = getDistribution();

		//如果分销员非空并未审核则提示用户请等待，如果分销员为拒绝状态则重新提交申请
		if (Optional.ofNullable(distribution).isPresent()) {
			if (distribution.getDistributionStatus().equals(DistributionStatusEnum.APPLY.name())) {
				throw new BusinessException(ResultEnum.DISTRIBUTION_IS_APPLY);
			} else if (distribution.getDistributionStatus().equals(DistributionStatusEnum.REFUSE.name())) {
				distribution.setDistributionStatus(DistributionStatusEnum.APPLY.name());
				BeanUtil.copyProperties(distributionApplyDTO, distribution);
				this.updateById(distribution);
				return distribution;
			}
		}

		//如果未申请分销员则新增进行申请
		//获取当前登录用户
		Member member = memberService.getUserInfo();
		//新建分销员
		distribution = new Distribution(member.getId(), member.getNickName(), distributionApplyDTO);
		//添加分销员
		this.save(distribution);

		return distribution;
	}

	@Override
	public boolean audit(String id, String status) {

		//检查分销开关
		checkDistributionSetting();

		//根据id获取分销员
		Distribution distribution = this.getById(id);
		if (Optional.ofNullable(distribution).isPresent()) {
			if (status.equals(DistributionStatusEnum.PASS.name())) {
				distribution.setDistributionStatus(DistributionStatusEnum.PASS.name());
			} else {
				distribution.setDistributionStatus(DistributionStatusEnum.REFUSE.name());
			}
			return this.updateById(distribution);
		}
		return false;
	}

	@Override
	public boolean retreat(String id) {

		//检查分销开关
		checkDistributionSetting();

		//根据id获取分销员
		Distribution distribution = this.getById(id);
		if (Optional.ofNullable(distribution).isPresent()) {
			distribution.setDistributionStatus(DistributionStatusEnum.RETREAT.name());
			return this.updateById(distribution);
		}
		return false;
	}

	@Override
	public boolean resume(String id) {
		//检查分销开关
		checkDistributionSetting();

		//根据id获取分销员
		Distribution distribution = this.getById(id);
		if (Optional.ofNullable(distribution).isPresent()) {
			distribution.setDistributionStatus(DistributionStatusEnum.PASS.name());
			return this.updateById(distribution);
		}

		return false;
	}

	@Override
	public void bindingDistribution(String distributionId) {
		//判断用户是否登录，未登录不能进行绑定
		if (SecurityUtil.getCurrentUser() == null) {
			throw new BusinessException(ResultEnum.USER_NOT_LOGIN);
		}

		//储存分销关系时间
		Distribution distribution = this.getById(distributionId);
		if (distribution != null) {
			Result<SettingVO> settingResult = settingService.get(
				SettingEnum.DISTRIBUTION_SETTING.name());
			DistributionSetting distributionSetting = JSONUtil.toBean(
				settingResult.data().getSettingValue(),
				DistributionSetting.class);

			redisRepository.setExpire(
				CachePrefix.DISTRIBUTION.getPrefix() + "_" + SecurityUtil.getUserId(),
				distribution.getId(),
				distributionSetting.getDistributionDay().longValue(),
				TimeUnit.DAYS);
		}

	}

	/**
	 * 检查分销设置开关
	 */
	@Override
	public void checkDistributionSetting() {
		//获取分销是否开启
		Result<SettingVO> settingResult = settingService.get(
			SettingEnum.DISTRIBUTION_SETTING.name());
		DistributionSetting distributionSetting = JSONUtil.toBean(
			settingResult.data().getSettingValue(),
			DistributionSetting.class);
		if (Boolean.FALSE.equals(distributionSetting.getIsOpen())) {
			throw new BusinessException(ResultEnum.DISTRIBUTION_CLOSE);
		}
	}

	@Override
	public void subCanRebate(BigDecimal canRebate, String distributionId) {
		this.baseMapper.subCanRebate(canRebate, distributionId);
	}

	@Override
	public void addRebate(BigDecimal rebate, String distributionId) {
		this.baseMapper.addCanRebate(rebate, distributionId);
	}

}
