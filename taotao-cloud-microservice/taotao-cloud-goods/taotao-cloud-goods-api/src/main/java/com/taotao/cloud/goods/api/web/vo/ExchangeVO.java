package com.taotao.cloud.goods.api.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 兑换VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeVO {

	@Schema(description = "是否允许积分兑换")
	private Integer enableExchange;

	@Schema(description = "兑换所需金额 ")
	private BigDecimal exchangeMoney;

	@Schema(description = "积分兑换所属分类 ")
	private Integer categoryId;

	@Schema(description = "积分兑换使用的积分 ")
	private Integer exchangePoint;
}
