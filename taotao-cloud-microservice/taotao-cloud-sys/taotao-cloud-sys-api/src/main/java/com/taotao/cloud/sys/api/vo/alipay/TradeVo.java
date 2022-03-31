/**
 * Copyright (C) 2018-2020 All rights reserved, Designed By www.yixiang.co 注意：
 * 本软件为www.yixiang.co开发研制
 */
package com.taotao.cloud.sys.api.vo.alipay;


import java.sql.Date;
import java.sql.Timestamp;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易详情，按需应该存入数据库
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeVo {

	/**
	 * （必填）商品描述
	 */
	private String body;

	/**
	 * （必填）商品名称
	 */
	private String subject;

	/**
	 * （必填）商户订单号，应该由后台生成
	 */
	private String outTradeNo;

	/**
	 * （必填）第三方订单号
	 */
	private String tradeNo;

	/**
	 * （必填）价格
	 */
	private String totalAmount;

	/**
	 * 订单状态,已支付，未支付，作废
	 */
	private String state;

	/**
	 * 创建时间，存入数据库时需要
	 */
	private Timestamp createTime;

	/**
	 * 作废时间，存入数据库时需要
	 */
	private Date cancelTime;
}
