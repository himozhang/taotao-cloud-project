package com.taotao.cloud.order.biz.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.utils.common.IdGeneratorUtil;
import com.taotao.cloud.common.utils.bean.BeanUtil;
import com.taotao.cloud.common.utils.lang.StringUtil;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.common.utils.number.CurrencyUtil;
import com.taotao.cloud.order.api.enums.order.FlowTypeEnum;
import com.taotao.cloud.order.api.enums.order.OrderPromotionTypeEnum;
import com.taotao.cloud.order.api.enums.order.PayStatusEnum;
import com.taotao.cloud.order.api.web.query.distribution.DistributionPageQuery;
import com.taotao.cloud.order.api.web.query.order.StoreFlowPageQuery;
import com.taotao.cloud.order.api.web.query.store.StorePageQuery;
import com.taotao.cloud.order.biz.model.entity.aftersale.AfterSale;
import com.taotao.cloud.order.biz.model.entity.order.Order;
import com.taotao.cloud.order.biz.model.entity.order.OrderItem;
import com.taotao.cloud.order.biz.model.entity.order.StoreFlow;
import com.taotao.cloud.order.biz.mapper.order.IStoreFlowMapper;
import com.taotao.cloud.order.biz.service.order.IOrderItemService;
import com.taotao.cloud.order.biz.service.order.IOrderService;
import com.taotao.cloud.order.biz.service.order.IStoreFlowService;
import com.taotao.cloud.payment.api.feign.IFeignRefundLogService;
import com.taotao.cloud.payment.api.vo.RefundLogVO;
import com.taotao.cloud.store.api.feign.IFeignBillService;
import com.taotao.cloud.store.api.web.vo.BillVO;
import com.taotao.cloud.store.api.web.vo.StoreFlowPayDownloadVO;
import com.taotao.cloud.store.api.web.vo.StoreFlowRefundDownloadVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商家订单流水业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:55:17
 */
@AllArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class StoreFlowServiceImpl extends ServiceImpl<IStoreFlowMapper, StoreFlow> implements
	IStoreFlowService {

	/**
	 * 订单
	 */
	private final IOrderService orderService;
	/**
	 * 订单货物
	 */
	private final IOrderItemService orderItemService;
	/**
	 * 退款日志
	 */
	private final IFeignRefundLogService refundLogService;

	private final IFeignBillService billService;

	@Override
	public void payOrder(String orderSn) {
		//根据订单编号获取子订单列表
		List<OrderItem> orderItems = orderItemService.getByOrderSn(orderSn);
		//根据订单编号获取订单数据
		Order order = orderService.getBySn(orderSn);

		//如果查询到多条支付记录，打印日志
		if (order.getPayStatus().equals(PayStatusEnum.PAID.name())) {
			LogUtil.error("订单[{}]检测到重复付款，请处理", orderSn);
		}

		//获取订单促销类型,如果为促销订单则获取促销商品并获取结算价
		String orderPromotionType = order.getOrderPromotionType();
		//循环子订单记录流水
		for (OrderItem item : orderItems) {
			StoreFlow storeFlow = new StoreFlow();
			BeanUtil.copyProperties(item, storeFlow);

			//入账
			storeFlow.setId(IdGeneratorUtil.getId());
			storeFlow.setFlowType(FlowTypeEnum.PAY.name());
			storeFlow.setSn(IdGeneratorUtil.createStr("SF"));
			storeFlow.setOrderSn(item.getOrderSn());
			storeFlow.setOrderItemSn(item.getSn());
			storeFlow.setStoreId(order.getStoreId());
			storeFlow.setStoreName(order.getStoreName());
			storeFlow.setMemberId(order.getMemberId());
			storeFlow.setMemberName(order.getMemberName());
			storeFlow.setGoodsName(item.getGoodsName());

			storeFlow.setOrderPromotionType(item.getPromotionType());

			//计算平台佣金
			storeFlow.setFinalPrice(item.getPriceDetailDTO().getFlowPrice());
			storeFlow.setCommissionPrice(item.getPriceDetailDTO().getPlatFormCommission());
			storeFlow.setDistributionRebate(item.getPriceDetailDTO().getDistributionCommission());
			storeFlow.setBillPrice(item.getPriceDetailDTO().getBillPrice());
			//兼容为空，以及普通订单操作
			if (StringUtil.isNotEmpty(orderPromotionType)) {
				if (orderPromotionType.equals(OrderPromotionTypeEnum.NORMAL.name())) {
					//普通订单操作
				}
				//如果为砍价活动，填写砍价结算价
				else if (orderPromotionType.equals(OrderPromotionTypeEnum.KANJIA.name())) {
					storeFlow.setKanjiaSettlementPrice(item.getPriceDetailDTO().getSettlementPrice());
				}
				//如果为砍价活动，填写砍价结算价
				else if (orderPromotionType.equals(OrderPromotionTypeEnum.POINTS.name())) {
					storeFlow.setPointSettlementPrice(item.getPriceDetailDTO().getSettlementPrice());
				}
			}
			//添加支付方式
			storeFlow.setPaymentName(order.getPaymentMethod());
			//添加第三方支付流水号
			storeFlow.setTransactionId(order.getReceivableNo());

			//添加付款交易流水
			this.save(storeFlow);
		}
	}

	@Override
	public void refundOrder(AfterSale afterSale) {
		StoreFlow storeFlow = new StoreFlow();
		//退款
		storeFlow.setFlowType(FlowTypeEnum.REFUND.name());
		storeFlow.setSn(IdGeneratorUtil.createStr("SF"));
		storeFlow.setRefundSn(afterSale.getSn());
		storeFlow.setOrderSn(afterSale.getOrderSn());
		storeFlow.setOrderItemSn(afterSale.getOrderItemSn());
		storeFlow.setStoreId(afterSale.getStoreId());
		storeFlow.setStoreName(afterSale.getStoreName());
		storeFlow.setMemberId(afterSale.getMemberId());
		storeFlow.setMemberName(afterSale.getMemberName());
		storeFlow.setGoodsId(afterSale.getGoodsId());
		storeFlow.setGoodsName(afterSale.getGoodsName());
		storeFlow.setSkuId(afterSale.getSkuId());
		storeFlow.setImage(afterSale.getGoodsImage());
		storeFlow.setSpecs(afterSale.getSpecs());

		//获取付款信息
		StoreFlow payStoreFlow = this.getOne(new LambdaUpdateWrapper<StoreFlow>().eq(StoreFlow::getOrderItemSn, afterSale.getOrderItemSn())
			.eq(StoreFlow::getFlowType, FlowTypeEnum.PAY));
		storeFlow.setNum(afterSale.getNum());
		storeFlow.setCategoryId(payStoreFlow.getCategoryId());
		//佣金
		storeFlow.setCommissionPrice(
			CurrencyUtil.mul(CurrencyUtil.div(payStoreFlow.getCommissionPrice(), payStoreFlow.getNum()), afterSale.getNum()));
		//分销佣金
		storeFlow.setDistributionRebate(CurrencyUtil.mul(CurrencyUtil.div(payStoreFlow.getDistributionRebate(), payStoreFlow.getNum()), afterSale.getNum()));
		//流水金额
		storeFlow.setFinalPrice(afterSale.getActualRefundPrice());
		//最终结算金额
		storeFlow.setBillPrice(CurrencyUtil.add(CurrencyUtil.add(storeFlow.getFinalPrice(), storeFlow.getDistributionRebate()), storeFlow.getCommissionPrice()));
		//获取第三方支付流水号
		RefundLogVO refundLog = refundLogService.queryByAfterSaleSn(afterSale.getSn());
		storeFlow.setTransactionId(refundLog.getReceivableNo());
		storeFlow.setPaymentName(refundLog.getPaymentName());
		this.save(storeFlow);
	}

	@Override
	public IPage<StoreFlow> getStoreFlow(StoreFlowPageQuery storeFlowPageQuery) {
		return this.page(storeFlowPageQuery.buildMpPage(), generatorQueryWrapper(storeFlowPageQuery));
	}

	@Override
	public StoreFlow queryOne(StoreFlowPageQuery storeFlowQueryDTO) {
		return this.getOne(generatorQueryWrapper(storeFlowQueryDTO));
	}

	@Override
	public List<StoreFlowPayDownloadVO> getStoreFlowPayDownloadVO(StoreFlowPageQuery storeFlowQueryDTO) {
		return baseMapper.getStoreFlowPayDownloadVO(generatorQueryWrapper(storeFlowQueryDTO));
	}

	@Override
	public List<StoreFlowRefundDownloadVO> getStoreFlowRefundDownloadVO(StoreFlowPageQuery storeFlowQueryDTO) {
		return baseMapper.getStoreFlowRefundDownloadVO(generatorQueryWrapper(storeFlowQueryDTO));
	}

	@Override
	public IPage<StoreFlow> getStoreFlow(StorePageQuery storePageQuery) {
		BillVO bill = billService.getById(storePageQuery.getId());
		return this.getStoreFlow(StoreFlowPageQuery.builder().type(type).pageVO(pageVO).bill(bill).build());
	}

	@Override
	public IPage<StoreFlow> getDistributionFlow(DistributionPageQuery distributionPageQuery) {
		BillVO bill = billService.getById(distributionPageQuery.getId());
		return this.getStoreFlow(StoreFlowPageQuery.builder().pageVO(pageVO).bill(bill).build());
	}

	@Override
	public List<StoreFlow> listStoreFlow(StoreFlowPageQuery storeFlowQueryDTO) {
		return this.list(generatorQueryWrapper(storeFlowQueryDTO));
	}

	/**
	 * 生成查询wrapper
	 *
	 * @param storeFlowPageQuery 搜索参数
	 * @return 查询wrapper
	 */
	private LambdaQueryWrapper<StoreFlow> generatorQueryWrapper(StoreFlowPageQuery storeFlowPageQuery) {
		LambdaQueryWrapper<StoreFlow> lambdaQueryWrapper = Wrappers.lambdaQuery();
		//分销订单过滤是否判定
		lambdaQueryWrapper.isNotNull(storeFlowPageQuery.getJustDistribution() != null && storeFlowPageQuery.getJustDistribution(),
			StoreFlow::getDistributionRebate);

		//流水类型判定
		lambdaQueryWrapper.eq(StringUtil.isNotEmpty(storeFlowPageQuery.getType()),
			StoreFlow::getFlowType, storeFlowPageQuery.getType());

		//售后编号判定
		lambdaQueryWrapper.eq(StringUtil.isNotEmpty(storeFlowPageQuery.getRefundSn()),
			StoreFlow::getRefundSn, storeFlowPageQuery.getRefundSn());

		//售后编号判定
		lambdaQueryWrapper.eq(StringUtil.isNotEmpty(storeFlowPageQuery.getOrderSn()),
			StoreFlow::getOrderSn, storeFlowPageQuery.getOrderSn());

		//结算单非空，则校对结算单参数
		if (storeFlowPageQuery.getBill() != null) {
			StoreFlowPageQuery.BillDTO bill = storeFlowPageQuery.getBill();
			lambdaQueryWrapper.eq(StringUtil.isNotEmpty(bill.getStoreId()), StoreFlow::getStoreId, bill.getStoreId());
			lambdaQueryWrapper.between(bill.getStartTime() != null && bill.getEndTime() != null,
				StoreFlow::getCreateTime, bill.getStartTime(), bill.getEndTime());
		}
		return lambdaQueryWrapper;
	}
}
