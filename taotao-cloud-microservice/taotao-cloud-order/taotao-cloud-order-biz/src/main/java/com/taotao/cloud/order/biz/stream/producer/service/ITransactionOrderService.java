package com.taotao.cloud.order.biz.stream.producer.service;

/**
 * 订单事务消息
 */
public interface ITransactionOrderService {

	/**
	 * 测试事务消息
	 */
	void testTransaction();

	/**
	 * 通过stream方式发送消息
	 * 注意配置spring.cloud.stream.rocketmq.bindings.order-output.producer.transactional=true
	 */
	void testStreamTransaction();
}
