package com.taotao.cloud.order.biz.service.order.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.SecurityUser;
import com.taotao.cloud.common.utils.bean.BeanUtil;
import com.taotao.cloud.common.utils.common.OperationalJudgment;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.goods.api.feign.IFeignGoodsSkuService;
import com.taotao.cloud.goods.api.web.vo.GoodsSkuSpecGalleryVO;
import com.taotao.cloud.order.api.web.dto.order.OrderComplaintDTO;
import com.taotao.cloud.order.api.web.dto.order.OrderComplaintOperationDTO;
import com.taotao.cloud.order.api.web.dto.order.StoreAppealDTO;
import com.taotao.cloud.order.api.enums.aftersale.ComplaintStatusEnum;
import com.taotao.cloud.order.api.enums.order.OrderComplaintStatusEnum;
import com.taotao.cloud.order.api.web.query.order.OrderComplaintPageQuery;
import com.taotao.cloud.order.api.web.vo.order.OrderComplaintVO;
import com.taotao.cloud.order.api.web.vo.order.OrderDetailVO;
import com.taotao.cloud.order.api.web.vo.order.OrderItemVO;
import com.taotao.cloud.order.biz.model.entity.order.OrderComplaint;
import com.taotao.cloud.order.biz.model.entity.order.OrderComplaintCommunication;
import com.taotao.cloud.order.biz.mapper.order.IOrderComplaintMapper;
import com.taotao.cloud.order.biz.service.order.IOrderComplaintCommunicationService;
import com.taotao.cloud.order.biz.service.order.IOrderComplaintService;
import com.taotao.cloud.order.biz.service.order.IOrderItemService;
import com.taotao.cloud.order.biz.service.order.IOrderService;
import lombok.AllArgsConstructor;
import org.apache.shardingsphere.distsql.parser.autogen.CommonDistSQLStatementParser.UserContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易投诉业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:55:04
 */
@AllArgsConstructor
@Service
public class OrderComplaintServiceImpl extends ServiceImpl<IOrderComplaintMapper, OrderComplaint> implements
	IOrderComplaintService {

	/**
	 * 订单
	 */
	private final IOrderService orderService;
	/**
	 * 订单货物
	 */
	private final IOrderItemService orderItemService;
	/**
	 * 商品规格
	 */
	private final IFeignGoodsSkuService goodsSkuService;
	/**
	 * 交易投诉沟通
	 */
	private final IOrderComplaintCommunicationService orderComplaintCommunicationService;

	@Override
	public IPage<OrderComplaint> getOrderComplainByPage(OrderComplaintPageQuery orderComplaintPageQuery) {
		LambdaQueryWrapper<OrderComplaint> queryWrapper = new LambdaQueryWrapper<>();
		if (StrUtil.isNotEmpty(orderComplaintPageQuery.getStatus())) {
			queryWrapper.eq(OrderComplaint::getComplainStatus, orderComplaintPageQuery.getStatus());
		}
		if (StrUtil.isNotEmpty(orderComplaintPageQuery.getOrderSn())) {
			queryWrapper.eq(OrderComplaint::getOrderSn, orderComplaintPageQuery.getOrderSn());
		}
		if (StrUtil.isNotEmpty(orderComplaintPageQuery.getStoreName())) {
			queryWrapper.like(OrderComplaint::getStoreName, orderComplaintPageQuery.getStoreName());
		}
		if (StrUtil.isNotEmpty(orderComplaintPageQuery.getStoreId())) {
			queryWrapper.eq(OrderComplaint::getStoreId, orderComplaintPageQuery.getStoreId());
		}
		if (StrUtil.isNotEmpty(orderComplaintPageQuery.getMemberName())) {
			queryWrapper.like(OrderComplaint::getMemberName, orderComplaintPageQuery.getMemberName());
		}
		if (StrUtil.isNotEmpty(orderComplaintPageQuery.getMemberId())) {
			queryWrapper.eq(OrderComplaint::getMemberId, orderComplaintPageQuery.getMemberId());
		}
		queryWrapper.eq(OrderComplaint::getDelFlag, false);
		queryWrapper.orderByDesc(OrderComplaint::getCreateTime);

		return this.page(orderComplaintPageQuery.buildMpPage(), queryWrapper);
	}

	@Override
	public OrderComplaintVO getOrderComplainById(Long id) {
		OrderComplaint orderComplaint = this.checkOrderComplainExist(id);
		LambdaQueryWrapper<OrderComplaintCommunication> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(OrderComplaintCommunication::getComplainId, id);
		List<OrderComplaintCommunication> list = orderComplaintCommunicationService.list(queryWrapper);
		OrderComplaintVO orderComplainVO = new OrderComplaintVO(orderComplaint);
		orderComplainVO.setOrderComplaintCommunications(list);
		orderComplainVO.setOrderComplaintImages(orderComplaint.getImages().split(","));
		if (orderComplaint.getAppealImages() != null) {
			orderComplainVO.setAppealImagesList(orderComplaint.getAppealImages().split(","));
		}
		return orderComplainVO;
	}

	@Override
	public OrderComplaint getOrderComplainByStoreId(Long storeId) {
		return this.getOne(new LambdaQueryWrapper<OrderComplaint>().eq(OrderComplaint::getStoreId, storeId));
	}

	@Override
	public OrderComplaint addOrderComplain(OrderComplaintDTO orderComplaintDTO) {
		try {
			SecurityUser currentUser = SecurityUtil.getCurrentUser();
			//查询订单信息
			OrderDetailVO orderDetailVO = orderService.queryDetail(orderComplaintDTO.getOrderSn());
			List<OrderItemVO> orderItems = orderDetailVO.getOrderItems();
			OrderItemVO orderItem = orderItems.stream()
				.filter(i -> orderComplaintDTO.getSkuId().equals(i.getSkuId()))
				.findFirst()
				.orElse(null);

			if (orderItem == null) {
				throw new BusinessException(ResultEnum.COMPLAINT_ORDER_ITEM_EMPTY_ERROR);
			}

			//新建交易投诉
			OrderComplaint orderComplaint = new OrderComplaint();
			BeanUtil.copyProperties(orderComplaintDTO, orderComplaint);

			//获取商品规格信息
			GoodsSkuSpecGalleryVO goodsSku = goodsSkuService.getGoodsSkuByIdFromCache(orderItem.getSkuId());
			if (goodsSku == null) {
				throw new BusinessException(ResultEnum.COMPLAINT_SKU_EMPTY_ERROR);
			}
			orderComplaint.setComplainStatus(ComplaintStatusEnum.NEW.name());
			orderComplaint.setGoodsId(goodsSku.getGoodsId());
			orderComplaint.setGoodsName(goodsSku.getGoodsName());
			orderComplaint.setGoodsImage(goodsSku.getThumbnail());
			orderComplaint.setGoodsPrice(goodsSku.getPrice());
			orderComplaint.setNum(orderItem.getNum());

			//获取订单信息
			orderComplaint.setOrderTime(orderDetailVO.getOrder().getcre());
			orderComplaint.setOrderPrice(orderDetailVO.getOrder().getPriceDetailDTO().getBillPrice());
			orderComplaint.setNum(orderDetailVO.getOrder().getGoodsNum());
			orderComplaint.setFreightPrice(orderDetailVO.getOrder().getPriceDetailDTO().getFreightPrice());
			orderComplaint.setLogisticsNo(orderDetailVO.getOrder().getLogisticsNo());
			orderComplaint.setConsigneeMobile(orderDetailVO.getOrder().getConsigneeMobile());
			orderComplaint.setConsigneeAddressPath(orderDetailVO.getOrder().getConsigneeAddressPath());
			orderComplaint.setConsigneeName(orderDetailVO.getOrder().getConsigneeName());

			//获取商家信息
			orderComplaint.setStoreId(orderDetailVO.getOrder().getStoreId());
			orderComplaint.setStoreName(orderDetailVO.getOrder().getStoreName());

			orderComplaint.setMemberId(currentUser.getUserId());
			orderComplaint.setMemberName(currentUser.getUsername());
			//保存订单投诉
			this.save(orderComplaint);

			//更新订单投诉状态
			orderItemService.updateOrderItemsComplainStatus(orderComplaint.getOrderSn(), orderComplaint.getSkuId(), orderComplaint.getId(), OrderComplaintStatusEnum.APPLYING);
			return orderComplaint;
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			log.error("订单投诉异常：", e);
			throw new BusinessException(ResultEnum.COMPLAINT_ERROR);
		}
	}

	@Override
	public Boolean updateOrderComplain(OrderComplaint orderComplaint) {
		OperationalJudgment.judgment(this.checkOrderComplainExist(orderComplaint.getId()));
		return this.updateById(orderComplaint);
	}

	@Override
	public Boolean updateOrderComplainByStatus(OrderComplaintOperationDTO orderComplaintOperationDTO) {
		OrderComplaint orderComplaint = OperationalJudgment.judgment(this.checkOrderComplainExist(orderComplaintOperationDTO.getComplainId()));
		this.checkOperationParams(orderComplaintOperationDTO, orderComplaint);
		orderComplaint.setComplainStatus(orderComplaintOperationDTO.getComplainStatus());
		this.updateById(orderComplaint);
		return true;
	}

	@Override
	public long waitComplainNum() {
		QueryWrapper<OrderComplaint> queryWrapper = Wrappers.query();
		queryWrapper.ne("complain_status", ComplaintStatusEnum.COMPLETE.name());
		queryWrapper.eq(CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.STORE.name()),
			"store_id", UserContext.getCurrentUser().getStoreId());
		return this.count(queryWrapper);
	}

	@Override
	public Boolean cancel(Long id) {
		OrderComplaint orderComplaint = OperationalJudgment.judgment(this.getById(id));
		//如果以及仲裁，则不可以进行申诉取消
		if (orderComplaint.getComplainStatus().equals(ComplaintStatusEnum.COMPLETE.name())) {
			throw new BusinessException(ResultEnum.COMPLAINT_CANCEL_ERROR);
		}
		LambdaUpdateWrapper<OrderComplaint> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
		lambdaUpdateWrapper.eq(OrderComplaint::getId, id);
		lambdaUpdateWrapper.set(OrderComplaint::getComplainStatus, ComplaintStatusEnum.CANCEL.name());
		return this.update(lambdaUpdateWrapper);
	}

	@Override
	public Boolean appeal(StoreAppealDTO storeAppealDTO) {
		//获取投诉信息
		OrderComplaint orderComplaint = OperationalJudgment.judgment(this.checkOrderComplainExist(storeAppealDTO.getOrderComplaintId()));
		orderComplaint.setAppealContent(storeAppealDTO.getAppealContent());
		orderComplaint.setAppealImages(storeAppealDTO.getAppealImages());
		orderComplaint.setAppealTime(LocalDateTime.now());
		orderComplaint.setComplainStatus(ComplaintStatusEnum.WAIT_ARBITRATION.name());
		this.updateById(orderComplaint);
		return true;
	}

	private OrderComplaint checkOrderComplainExist(Long id) {
		OrderComplaint orderComplaint = this.getById(id);
		if (orderComplaint == null) {
			throw new BusinessException(ResultEnum.COMPLAINT_NOT_EXIT);
		}
		return orderComplaint;
	}

	private void checkOperationParams(OrderComplaintOperationDTO orderComplaintOperationDTO, OrderComplaint orderComplaint) {
		ComplaintStatusEnum complaintStatusEnum = ComplaintStatusEnum.valueOf(orderComplaintOperationDTO.getComplainStatus());
		if (complaintStatusEnum == ComplaintStatusEnum.COMPLETE) {
			if (CharSequenceUtil.isEmpty(orderComplaintOperationDTO.getArbitrationResult())) {
				throw new BusinessException(ResultEnum.COMPLAINT_ARBITRATION_RESULT_ERROR);
			}
			orderComplaint.setArbitrationResult(orderComplaintOperationDTO.getArbitrationResult());
		} else if (complaintStatusEnum == ComplaintStatusEnum.COMMUNICATION) {
			if (CharSequenceUtil.isEmpty(orderComplaintOperationDTO.getAppealContent()) || orderComplaintOperationDTO.getImages() == null) {
				throw new BusinessException(ResultEnum.COMPLAINT_APPEAL_CONTENT_ERROR);
			}
			orderComplaint.setContent(orderComplaintOperationDTO.getAppealContent());
			orderComplaint.setImages(orderComplaintOperationDTO.getImages().get(0));
		}
	}

}
