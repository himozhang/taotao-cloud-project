package com.taotao.cloud.order.biz.stream.producer.service.impl;

import lombok.AllArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import vip.mate.core.rocketmq.constant.MessageConstant;
import vip.mate.core.rocketmq.entity.Order;
import vip.mate.message.service.ITransactionOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@AllArgsConstructor
public class TransactionOrderServiceImpl implements ITransactionOrderService {

	private final RocketMQTemplate rocketMQTemplate;

	private final StreamBridge streamBridge;

	/**
	 * 这里消息发送只是half发送，
	 * 后面消息队列中half成功后，在TestTransactionListener中的executeLocalTransaction的方法中决定是否要提交本地事务
	 */
	@StreamListener(Sink.INPUT)
	@Override
	public void testTransaction() {
		Order order = Order.builder()
				.id(1L)
				.goodsId(100L)
				.goodsPrice(BigDecimal.valueOf(100.00))
				.tradeId(100L)
				.number(2)
				.createTime(LocalDateTime.now())
				.build();

		// 事务id
		String transactionId = UUID.randomUUID().toString();
		rocketMQTemplate.sendMessageInTransaction(MessageConstant.ORDER_BINDER_GROUP,
				MessageConstant.ORDER_MESSAGE_OUTPUT,
				MessageBuilder.withPayload(order)
		.setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
		.setHeader("share_id", 3).build(),
				4L);

		log.info("half消息发送成功");

	}

	@Override
	public void testStreamTransaction() {

		Order order = Order.builder()
				.id(1L)
				.goodsId(100L)
				.goodsPrice(BigDecimal.valueOf(100.00))
				.tradeId(100L)
				.number(2)
				.createTime(LocalDateTime.now())
				.build();
		// 事务id
		String transactionId = UUID.randomUUID().toString();

		streamBridge.send(MessageConstant.ORDER_MESSAGE_OUTPUT, MessageBuilder.withPayload(order)
				.setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
				.setHeader("share_id", 3).build());
		log.info("half消息发送成功");
	}
}
