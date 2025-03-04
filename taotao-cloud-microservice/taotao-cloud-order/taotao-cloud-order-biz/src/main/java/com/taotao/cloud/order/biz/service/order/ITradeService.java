package com.taotao.cloud.order.biz.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taotao.cloud.order.api.web.dto.cart.TradeDTO;
import com.taotao.cloud.order.biz.model.entity.order.Trade;

/**
 * 交易业务层
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:54:55
 */
public interface ITradeService extends IService<Trade> {

	/**
	 * 创建交易
	 * 1.订单数据校验
	 * 2.积分预处理
	 * 3.优惠券预处理
	 * 4.添加交易
	 * 5.添加订单
	 * 6.将交易写入缓存供消费者调用
	 * 7.发送交易创建消息
	 *
	 * @param tradeDTO 购物车视图
	 * @return {@link Trade }
	 * @since 2022-04-28 08:54:55
	 */
	Trade createTrade(TradeDTO tradeDTO);

	/**
	 * 获取交易详情
	 *
	 * @param sn 交易编号
	 * @return {@link Trade }
	 * @since 2022-04-28 08:54:55
	 */
	Trade getBySn(String sn);

	/**
	 * 整笔交易付款
	 *
	 * @param tradeSn      交易编号
	 * @param paymentName  支付方式
	 * @param receivableNo 第三方流水号
	 * @since 2022-04-28 08:54:55
	 */
	void payTrade(String tradeSn, String paymentName, String receivableNo);

}
