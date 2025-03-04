package com.taotao.cloud.sys.biz.config.stream;

import com.taotao.cloud.common.utils.log.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 流函数服务
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 11:54:07
 */
@Component
public class StreamFunctionService {

	@Autowired
	private StreamBridge bridge;

	public void sendKafka(String content) {
		boolean send = bridge.send("outputKafka-out-0", content);
		LogUtil.info(String.valueOf(send));
	}

	public void sendRabbit(String content) {
		boolean send = bridge.send("outputRabbit-out-0", content);
		LogUtil.info(String.valueOf(send));
	}

	@Bean
	public Consumer<String> inputKafka1() {
		return str -> {
			// 收到消息在这里做一些处理
			LogUtil.info("inputKafka1 message: {}", str);
		};
	}

	@Bean
	public Consumer<String> inputKafka2() {
		return str -> {
			LogUtil.info("inputKafka2 message: {}", str);
		};
	}

	@Bean
	public Consumer<String> inputRabbit1() {
		return str -> {
			LogUtil.info("inputRabbit1 message: {}", str);
		};
	}

	@Bean
	public Consumer<String> inputRabbit2() {
		return str -> {
			LogUtil.info("inputRabbit2 message: {}", str);
		};
	}

	//@Bean
	//public Function<KStream<Long, Order>,
	//	Function<GlobalKTable<Long, Customer>,
	//		Function<GlobalKTable<Long, Product>, KStream<Long, EnrichedOrder>>>> enrichOrder() {
	//
	//	return orders -> (
	//		customers -> (
	//			products -> (
	//				orders.join(customers,
	//						(orderId, order) -> order.getCustomerId(),
	//						(order, customer) -> new CustomerOrder(customer, order))
	//					.join(products,
	//						(orderId, customerOrder) -> customerOrder
	//							.productId(),
	//						(customerOrder, product) -> {
	//							EnrichedOrder enrichedOrder = new EnrichedOrder();
	//							enrichedOrder.setProduct(product);
	//							enrichedOrder.setCustomer(customerOrder.customer);
	//							enrichedOrder.setOrder(customerOrder.order);
	//							return enrichedOrder;
	//						})
	//			)
	//		)
	//	);
	//}

	//@Bean
	//public Function<KStream<Object, String>, KStream<?, WordCount>[]> process() {
	//
	//	Predicate<Object, WordCount> isEnglish = (k, v) -> v.word.equals("english");
	//	Predicate<Object, WordCount> isFrench = (k, v) -> v.word.equals("french");
	//	Predicate<Object, WordCount> isSpanish = (k, v) -> v.word.equals("spanish");
	//
	//	return input -> input
	//		.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
	//		.groupBy((key, value) -> value)
	//		.windowedBy(TimeWindows.of(5000))
	//		.count(Materialized.as("WordCounts-branch"))
	//		.toStream()
	//		.map((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value,
	//			new Date(key.window().start()), new Date(key.window().end()))))
	//		.branch(isEnglish, isFrench, isSpanish);
	//}


}
