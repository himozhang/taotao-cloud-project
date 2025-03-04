package com.taotao.cloud.goods.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.CachePrefix;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.SecurityUser;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.goods.api.web.vo.StoreGoodsLabelVO;
import com.taotao.cloud.goods.biz.model.entity.StoreGoodsLabel;
import com.taotao.cloud.goods.biz.mapper.IStoreGoodsLabelMapper;
import com.taotao.cloud.goods.biz.service.IStoreGoodsLabelService;
import com.taotao.cloud.redis.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 店铺商品分类业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 17:02:58
 */
@Service
public class StoreGoodsLabelServiceImpl extends
	ServiceImpl<IStoreGoodsLabelMapper, StoreGoodsLabel> implements IStoreGoodsLabelService {

	/**
	 * 缓存
	 */
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public List<StoreGoodsLabelVO> listByStoreId(Long storeId) {
		//从缓存中获取店铺分类
		if (redisRepository.hasKey(CachePrefix.STORE_CATEGORY.getPrefix() + storeId)) {
			return (List<StoreGoodsLabelVO>) redisRepository.get(
				CachePrefix.STORE_CATEGORY.getPrefix() + storeId);
		}

		List<StoreGoodsLabel> list = list(storeId);
		List<StoreGoodsLabelVO> storeGoodsLabelVOList = new ArrayList<>();

		//循环列表判断是否为顶级，如果为顶级获取下级数据
		list.stream()
			.filter(storeGoodsLabel -> storeGoodsLabel.getLevel() == 0)
			.forEach(storeGoodsLabel -> {
				StoreGoodsLabelVO storeGoodsLabelVO = new StoreGoodsLabelVO(storeGoodsLabel.getId(),
					storeGoodsLabel.getLabelName(), storeGoodsLabel.getLevel(),
					storeGoodsLabel.getSortOrder());
				List<StoreGoodsLabelVO> storeGoodsLabelVOChildList = new ArrayList<>();
				list.stream()
					.filter(label -> label.getParentId().equals(storeGoodsLabel.getId()))
					.forEach(storeGoodsLabelChild -> storeGoodsLabelVOChildList.add(
						new StoreGoodsLabelVO(storeGoodsLabelChild.getId(),
							storeGoodsLabelChild.getLabelName(), storeGoodsLabelChild.getLevel(),
							storeGoodsLabelChild.getSortOrder())));
				storeGoodsLabelVO.setChildren(storeGoodsLabelVOChildList);
				storeGoodsLabelVOList.add(storeGoodsLabelVO);
			});

		//调整店铺分类排序
		storeGoodsLabelVOList.sort(Comparator.comparing(StoreGoodsLabelVO::getSortOrder));

		if (!storeGoodsLabelVOList.isEmpty()) {
			redisRepository.set(CachePrefix.CATEGORY.getPrefix() + storeId + "tree",
				storeGoodsLabelVOList);
		}
		return storeGoodsLabelVOList;
	}

	/**
	 * 根据分类id集合获取所有店铺分类根据层级排序
	 *
	 * @param ids 商家ID
	 * @return 店铺分类列表
	 */
	@Override
	public List<StoreGoodsLabel> listByStoreIds(List<Long> ids) {
		return this.list(new LambdaQueryWrapper<StoreGoodsLabel>().in(StoreGoodsLabel::getId, ids)
			.orderByAsc(StoreGoodsLabel::getLevel));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean addStoreGoodsLabel(StoreGoodsLabel storeGoodsLabel) {
		//获取当前登录商家账号
		SecurityUser tokenUser = SecurityUtil.getUser();
		storeGoodsLabel.setStoreId(tokenUser.getStoreId());
		//保存店铺分类
		this.save(storeGoodsLabel);
		//清除缓存
		removeCache(storeGoodsLabel.getStoreId());
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean editStoreGoodsLabel(StoreGoodsLabel storeGoodsLabel) {
		//修改当前店铺的商品分类
		SecurityUser tokenUser = SecurityUtil.getUser();

		LambdaUpdateWrapper<StoreGoodsLabel> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
		lambdaUpdateWrapper.eq(StoreGoodsLabel::getStoreId, tokenUser.getStoreId());
		lambdaUpdateWrapper.eq(StoreGoodsLabel::getId, storeGoodsLabel.getId());
		//修改店铺分类
		this.update(storeGoodsLabel, lambdaUpdateWrapper);
		//清除缓存
		removeCache(storeGoodsLabel.getStoreId());
		return true;
	}

	@Override
	public Boolean removeStoreGoodsLabel(Long storeLabelId) {
		SecurityUser tokenUser = SecurityUtil.getUser();
		if (tokenUser == null || Objects.isNull(tokenUser.getStoreId())) {
			throw new BusinessException(ResultEnum.USER_NOT_LOGIN);
		}
		//删除店铺分类
		this.removeById(storeLabelId);

		//清除缓存
		removeCache(tokenUser.getStoreId());

		return true;
	}

	/**
	 * 获取店铺商品分类列表
	 *
	 * @param storeId 店铺ID
	 * @return 店铺商品分类列表
	 */
	private List<StoreGoodsLabel> list(Long storeId) {
		LambdaQueryWrapper<StoreGoodsLabel> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.eq(StoreGoodsLabel::getStoreId, storeId);
		queryWrapper.orderByDesc(StoreGoodsLabel::getSortOrder);
		return this.baseMapper.selectList(queryWrapper);
	}

	/**
	 * 清除缓存
	 */
	private void removeCache(Long storeId) {
		redisRepository.del(CachePrefix.STORE_CATEGORY.getPrefix() + storeId);
	}
}
