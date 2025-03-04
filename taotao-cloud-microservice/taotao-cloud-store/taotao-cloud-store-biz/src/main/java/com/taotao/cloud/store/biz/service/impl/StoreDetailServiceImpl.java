package com.taotao.cloud.store.biz.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.common.utils.bean.BeanUtil;
import com.taotao.cloud.goods.api.feign.IFeignCategoryService;
import com.taotao.cloud.goods.api.feign.IFeignGoodsService;
import com.taotao.cloud.goods.api.web.vo.CategoryTreeVO;
import com.taotao.cloud.store.api.web.dto.StoreAfterSaleAddressDTO;
import com.taotao.cloud.store.api.web.dto.StoreSettingDTO;
import com.taotao.cloud.store.api.web.dto.StoreSettlementDay;
import com.taotao.cloud.store.api.web.vo.StoreBasicInfoVO;
import com.taotao.cloud.store.api.web.vo.StoreDetailInfoVO;
import com.taotao.cloud.store.api.web.vo.StoreManagementCategoryVO;
import com.taotao.cloud.store.api.web.vo.StoreOtherVO;
import com.taotao.cloud.store.biz.model.entity.Store;
import com.taotao.cloud.store.biz.model.entity.StoreDetail;
import com.taotao.cloud.store.biz.mapper.StoreDetailMapper;
import com.taotao.cloud.store.biz.service.StoreDetailService;
import com.taotao.cloud.store.biz.service.StoreService;
import com.taotao.cloud.stream.framework.rocketmq.RocketmqSendCallbackBuilder;
import com.taotao.cloud.stream.framework.rocketmq.tags.GoodsTagsEnum;
import com.taotao.cloud.stream.properties.RocketmqCustomProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 店铺详细业务层实现
 *
 * @since 2020-03-07 16:18:56
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StoreDetailServiceImpl extends ServiceImpl<StoreDetailMapper, StoreDetail> implements
	StoreDetailService {

	/**
	 * 店铺
	 */
	@Autowired
	private StoreService storeService;
	/**
	 * 分类
	 */
	@Autowired
	private IFeignCategoryService categoryService;

	@Autowired
	private IFeignGoodsService goodsService;

	@Autowired
	private RocketmqCustomProperties rocketmqCustomProperties;

	@Autowired
	private RocketMQTemplate rocketMQTemplate;

	@Override
	public StoreDetailInfoVO getStoreDetailVO(Long storeId) {
		return this.baseMapper.getStoreDetail(storeId);
	}

	@Override
	public StoreDetailInfoVO getStoreDetailVOByMemberId(Long memberId) {
		return this.baseMapper.getStoreDetailByMemberId(memberId);
	}

	@Override
	public StoreDetail getStoreDetail(Long storeId) {
		LambdaQueryWrapper<StoreDetail> lambdaQueryWrapper = Wrappers.lambdaQuery();
		lambdaQueryWrapper.eq(StoreDetail::getStoreId, storeId);
		return this.getOne(lambdaQueryWrapper);
	}

	@Override
	public Boolean editStoreSetting(StoreSettingDTO storeSettingDTO) {
		Long storeId = SecurityUtil.getCurrentUser().getStoreId();
		//修改店铺
		Store store = storeService.getById(storeId);
		BeanUtil.copyProperties(storeSettingDTO, store);
		boolean result = storeService.updateById(store);
		if (result) {
			this.updateStoreGoodsInfo(store);
		}
		return result;
	}

	@Override
	public void updateStoreGoodsInfo(Store store) {
		goodsService.updateStoreDetail(store.getId());

		Map<String, Object> updateIndexFieldsMap = EsIndexUtil.getUpdateIndexFieldsMap(
			MapUtil.builder().put("storeId", store.getId()).build(),
			MapUtil.builder().put("storeName", store.getStoreName()).put("selfOperated", store.getSelfOperated()).build());
		String destination = rocketmqCustomProperties.getGoodsTopic() + ":" + GoodsTagsEnum.UPDATE_GOODS_INDEX_FIELD.name();
		//发送mq消息
		rocketMQTemplate.asyncSend(destination, JSONUtil.toJsonStr(updateIndexFieldsMap), RocketmqSendCallbackBuilder.commonCallback());
	}

	@Override
	public Boolean editMerchantEuid(String merchantEuid) {
		Long storeId = SecurityUtil.getCurrentUser().getStoreId();
		Store store = storeService.getById(storeId);
		store.setMerchantEuid(merchantEuid);
		return storeService.updateById(store);
	}

	@Override
	public List<StoreSettlementDay> getSettlementStore(int day) {
		return null;
	}

	@Override
	public void updateSettlementDay(Long storeId, LocalDateTime endTime) {

	}

	@Override
	public StoreBasicInfoVO getStoreBasicInfoDTO(String storeId) {
		return this.baseMapper.getStoreBasicInfoDTO(storeId);
	}

	@Override
	public StoreAfterSaleAddressDTO getStoreAfterSaleAddressDTO() {
		Long storeId = SecurityUtil.getCurrentUser().getStoreId();
		return this.baseMapper.getStoreAfterSaleAddressDTO(storeId);
	}

	@Override
	public StoreAfterSaleAddressDTO getStoreAfterSaleAddressDTO(Long id) {
		StoreAfterSaleAddressDTO storeAfterSaleAddressDTO = this.baseMapper.getStoreAfterSaleAddressDTO(id);
		if (storeAfterSaleAddressDTO == null) {
			storeAfterSaleAddressDTO = new StoreAfterSaleAddressDTO();
		}
		return storeAfterSaleAddressDTO;
	}

	@Override
	public boolean editStoreAfterSaleAddressDTO(StoreAfterSaleAddressDTO storeAfterSaleAddressDTO) {
		Long storeId = SecurityUtil.getCurrentUser().getStoreId();
		LambdaUpdateWrapper<StoreDetail> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
		lambdaUpdateWrapper.set(StoreDetail::getSalesConsigneeName, storeAfterSaleAddressDTO.getSalesConsigneeName());
		lambdaUpdateWrapper.set(StoreDetail::getSalesConsigneeAddressId, storeAfterSaleAddressDTO.getSalesConsigneeAddressId());
		lambdaUpdateWrapper.set(StoreDetail::getSalesConsigneeAddressPath, storeAfterSaleAddressDTO.getSalesConsigneeAddressPath());
		lambdaUpdateWrapper.set(StoreDetail::getSalesConsigneeDetail, storeAfterSaleAddressDTO.getSalesConsigneeDetail());
		lambdaUpdateWrapper.set(StoreDetail::getSalesConsigneeMobile, storeAfterSaleAddressDTO.getSalesConsigneeMobile());
		lambdaUpdateWrapper.eq(StoreDetail::getStoreId, storeId);
		return this.update(lambdaUpdateWrapper);
	}

	@Override
	public boolean updateStockWarning(Integer stockWarning) {
		Long storeId = SecurityUtil.getCurrentUser().getStoreId();
		LambdaUpdateWrapper<StoreDetail> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
		lambdaUpdateWrapper.set(StoreDetail::getStockWarning, stockWarning);
		lambdaUpdateWrapper.eq(StoreDetail::getStoreId, storeId);
		return this.update(lambdaUpdateWrapper);
	}

	@Override
	public List<StoreManagementCategoryVO> goodsManagementCategory(String storeId) {

		//获取顶部分类列表
		List<CategoryTreeVO> categoryList = categoryService.firstCategory().data();
		//获取店铺信息
		StoreDetail storeDetail = this.getOne(new LambdaQueryWrapper<StoreDetail>().eq(StoreDetail::getStoreId, storeId));
		//获取店铺分类
		String[] storeCategoryList = storeDetail.getGoodsManagementCategory().split(",");
		List<StoreManagementCategoryVO> list = new ArrayList<>();
		for (CategoryTreeVO category : categoryList) {
			StoreManagementCategoryVO storeManagementCategoryVO = new StoreManagementCategoryVO();
			BeanUtil.copyProperties(category, storeManagementCategoryVO);

			for (String storeCategory : storeCategoryList) {
				if (Long.valueOf(storeCategory).equals(category.getId())) {
					storeManagementCategoryVO.setSelected(true);
				}
			}
			list.add(storeManagementCategoryVO);
		}
		return list;
	}

	@Override
	public StoreOtherVO getStoreOtherVO(String storeId) {
		return this.baseMapper.getLicencePhoto(storeId);
	}

}
