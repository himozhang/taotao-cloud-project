package com.taotao.cloud.order.api.web.vo.order;

import io.soabase.recordbuilder.core.RecordBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 订单详情VO
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-21 16:59:38
 */
@RecordBuilder
@Schema(description = "订单详情VO")
public record OrderDetailVO(
	/**
	 * 订单
	 */
	OrderBaseVO order,

	/**
	 * 子订单信息
	 */
	List<OrderItemVO> orderItems,

	/**
	 * 订单状态
	 */
	String orderStatusValue,

	/**
	 * 付款状态
	 */
	String payStatusValue,

	/**
	 * 物流状态
	 */
	String deliverStatusValue,

	/**
	 * 物流类型
	 */
	String deliveryMethodValue,

	/**
	 * 支付类型
	 */
	String paymentMethodValue,

	/**
	 * 发票
	 */
	ReceiptVO receipt,

	/**
	 * 获取订单日志
	 */
	List<OrderLogVO> orderLogs,

	@Schema(description = "价格详情")
	String priceDetail
) implements Serializable {

	@Serial
	private static final long serialVersionUID = -6293102172184734928L;

	//
	// public OrderDetailVO(OrderBaseVO order, List<OrderItemVO> orderItems,
	// 	List<OrderLogVO> orderLogs,
	// 	ReceiptVO receipt) {
	// 	this.order = order;
	// 	this.orderItems = orderItems;
	// 	this.orderLogs = orderLogs;
	// 	this.receipt = receipt;
	// }
	//
	// /**
	//  * 可操作类型
	//  */
	// public AllowOperation getAllowOperationVO() {
	// 	return new AllowOperation(this.order);
	// }
	//
	// public String getOrderStatusValue() {
	// 	try {
	// 		return OrderStatusEnum.valueOf(order.getOrderStatus()).description();
	// 	} catch (Exception e) {
	// 		return "";
	// 	}
	// }
	//
	// public String getPayStatusValue() {
	// 	try {
	// 		return PayStatusEnum.valueOf(order.getPayStatus()).description();
	// 	} catch (Exception e) {
	// 		return "";
	// 	}
	//
	// }
	//
	// public String getDeliverStatusValue() {
	// 	try {
	// 		return DeliverStatusEnum.valueOf(order.getDeliverStatus()).getDescription();
	// 	} catch (Exception e) {
	// 		return "";
	// 	}
	// }
	//
	// public String getDeliveryMethodValue() {
	// 	try {
	// 		return DeliveryMethodEnum.valueOf(order.getDeliveryMethod()).getDescription();
	// 	} catch (Exception e) {
	// 		return "";
	// 	}
	// }
	//
	// public String getPaymentMethodValue() {
	// 	try {
	// 		return PaymentMethodEnum.valueOf(order.getPaymentMethod()).paymentName();
	// 	} catch (Exception e) {
	// 		return "";
	// 	}
	// }


}
