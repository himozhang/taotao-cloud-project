package com.taotao.cloud.payment.biz.kit;

import com.taotao.cloud.order.api.feign.IFeignOrderItemService;
import com.taotao.cloud.order.api.feign.IFeignOrderService;
import com.taotao.cloud.order.api.feign.IFeignStoreFlowService;
import com.taotao.cloud.order.api.web.vo.aftersale.AfterSaleVO;
import com.taotao.cloud.order.api.web.vo.order.OrderItemVO;
import com.taotao.cloud.order.api.web.vo.order.OrderVO;
import com.taotao.cloud.payment.api.enums.PaymentMethodEnum;
import com.taotao.cloud.payment.biz.entity.RefundLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 退款支持
 *
 * @author shuigedeng
 * @version 2022.06
 * @since 2022-06-02 14:59:13
 */
@Component
public class RefundSupport {
	/**
	 * 店铺流水
	 */
	@Autowired
	private IFeignStoreFlowService storeFlowService;
	/**
	 * 订单
	 */
	@Autowired
	private IFeignOrderService orderService;
	/**
	 * 子订单
	 */
	@Autowired
	private IFeignOrderItemService orderItemService;

	/**
	 * 售后退款
	 *
	 * @param afterSale
	 */
	public void refund(AfterSaleVO afterSale) {
		OrderVO order = orderService.getBySn(afterSale.orderSn()).data();
		RefundLog refundLog = RefundLog.builder()
			.isRefund(false)
			.totalAmount(afterSale.getActualRefundPrice())
			.payPrice(afterSale.getActualRefundPrice())
			.memberId(afterSale.getMemberId())
			.paymentName(order.getPaymentMethod())
			.afterSaleNo(afterSale.getSn())
			.paymentReceivableNo(order.getReceivableNo())
			.outOrderNo("AF" + SnowFlake.getIdStr())
			.orderSn(afterSale.getOrderSn())
			.refundReason(afterSale.getReason())
			.build();

		PaymentMethodEnum paymentMethodEnum = PaymentMethodEnum.paymentNameOf(order.getPaymentMethod());
		Payment payment = (Payment) SpringContextUtil.getBean(paymentMethodEnum.getPlugin());
		payment.refund(refundLog);

		this.updateReturnGoodsNumber(afterSale);

		//记录退款流水
		storeFlowService.refundOrder(afterSale);
	}

	/**
	 * 功能描述: 修改子订单中已售后退款商品数量
	 */
	private void updateReturnGoodsNumber(AfterSaleVO afterSale) {
		//根据商品id及订单sn获取子订单
		OrderItemVO orderItem = orderItemService.getByOrderSnAndSkuId(afterSale.orderSn(), afterSale.skuId());

		orderItem.setReturnGoodsNumber(afterSale.getNum() + orderItem.getReturnGoodsNumber());

		//修改子订单订单中的退货数量
		orderItemService.updateById(orderItem);
	}

	/**
	 * 订单取消
	 */
	public void cancel(AfterSaleVO afterSale) {
		OrderVO order = orderService.getBySn(afterSale.orderSn()).data();
		RefundLog refundLog = RefundLog.builder()
			.isRefund(false)
			.totalAmount(afterSale.getActualRefundPrice())
			.payPrice(afterSale.getActualRefundPrice())
			.memberId(afterSale.getMemberId())
			.paymentName(order.getPaymentMethod())
			.afterSaleNo(afterSale.getSn())
			.paymentReceivableNo(order.getReceivableNo())
			.outOrderNo("AF" + SnowFlake.getIdStr())
			.orderSn(afterSale.getOrderSn())
			.refundReason(afterSale.getReason())
			.build();
		PaymentMethodEnum paymentMethodEnum = PaymentMethodEnum.paymentNameOf(order.getPaymentMethod());
		Payment payment = (Payment) SpringContextUtil.getBean(paymentMethodEnum.getPlugin());
		payment.refund(refundLog);
	}


	/**
	 * 退款通知
	 *
	 * @param paymentMethodEnum 支付渠道
	 * @param request           请求
	 * @since 2022-06-02 14:59:26
	 */
	public void notify(PaymentMethodEnum paymentMethodEnum, HttpServletRequest request) {
		//获取支付插件
		Payment payment = (Payment) SpringContextUtil.getBean(paymentMethodEnum.getPlugin());
		payment.refundNotify(request);
	}

}
