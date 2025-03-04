package com.taotao.cloud.order.api.web.dto.trade;

import com.taotao.cloud.common.enums.ClientTypeEnum;
import com.taotao.cloud.order.api.web.dto.cart.StoreRemarkDTO;
import io.soabase.recordbuilder.core.RecordBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 交易参数
 */
@RecordBuilder
@Schema(description = "交易参数")
public record TradeDTO(
	@Schema(description = "购物车购买：CART/立即购买：BUY_NOW/拼团购买：PINTUAN / 积分购买：POINT")
	String way,

	/**
	 * @see ClientTypeEnum
	 */
	@Schema(description = "客户端：H5/移动端 PC/PC端,WECHAT_MP/小程序端,APP/移动应用端")
	String client,

	@Schema(description = "店铺备注")
	List<StoreRemarkDTO> remark,

	@Schema(description = "是否为其他订单下的订单，如果是则为依赖订单的sn，否则为空")
	String parentOrderSn

) implements Serializable {

	@Serial
	private static final long serialVersionUID = -8383072817737513063L;

}
