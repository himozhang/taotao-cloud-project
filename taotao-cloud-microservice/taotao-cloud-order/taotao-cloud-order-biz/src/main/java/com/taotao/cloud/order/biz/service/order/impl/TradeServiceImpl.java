package com.taotao.cloud.order.biz.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.CachePrefix;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.member.api.enums.PointTypeEnum;
import com.taotao.cloud.member.api.feign.IFeignMemberService;
import com.taotao.cloud.order.api.web.dto.cart.MemberCouponDTO;
import com.taotao.cloud.order.api.web.dto.cart.TradeDTO;
import com.taotao.cloud.order.api.enums.cart.CartTypeEnum;
import com.taotao.cloud.order.api.enums.order.PayStatusEnum;
import com.taotao.cloud.order.api.web.vo.cart.CartVO;
import com.taotao.cloud.order.biz.model.entity.order.Order;
import com.taotao.cloud.order.biz.model.entity.order.Trade;
import com.taotao.cloud.order.biz.mapper.order.ITradeMapper;
import com.taotao.cloud.order.biz.service.order.IOrderService;
import com.taotao.cloud.order.biz.service.order.ITradeService;
import com.taotao.cloud.promotion.api.enums.KanJiaStatusEnum;
import com.taotao.cloud.promotion.api.feign.IFeignCouponService;
import com.taotao.cloud.promotion.api.feign.IFeignKanjiaActivityService;
import com.taotao.cloud.promotion.api.feign.IFeignMemberCouponService;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.taotao.cloud.stream.framework.rocketmq.RocketmqSendCallbackBuilder;
import com.taotao.cloud.stream.framework.rocketmq.tags.OrderTagsEnum;
import com.taotao.cloud.stream.properties.RocketmqCustomProperties;
import lombok.AllArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:55:19
 */
@AllArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class TradeServiceImpl extends ServiceImpl<ITradeMapper, Trade> implements ITradeService {

	/**
	 * 缓存
	 */
	private final RedisRepository redisRepository;
	/**
	 * 订单
	 */
	private final IOrderService orderService;
	/**
	 * 会员
	 */
	private final IFeignMemberService memberService;
	/**
	 * 优惠券
	 */
	private final IFeignCouponService couponService;
	/**
	 * 会员优惠券
	 */
	private final IFeignMemberCouponService memberCouponService;
	/**
	 * 砍价
	 */
	private final IFeignKanjiaActivityService kanjiaActivityService;
	/**
	 * RocketMQ
	 */
	private final RocketMQTemplate rocketMQTemplate;
	/**
	 * RocketMQ 配置
	 */
	private final RocketmqCustomProperties rocketmqCustomProperties;


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Trade createTrade(TradeDTO tradeDTO) {
		//创建订单预校验
		createTradeCheck(tradeDTO);

		Trade trade = new Trade(tradeDTO);
		String key = CachePrefix.TRADE.getPrefix() + trade.getSn();
		//优惠券预处理
		couponPretreatment(tradeDTO);
		//积分预处理
		pointPretreatment(tradeDTO);
		//添加交易
		this.save(trade);
		//添加订单
		orderService.intoDB(tradeDTO);

		//砍价订单处理
		kanjiaPretreatment(tradeDTO);
		//写入缓存，给消费者调用
		redisRepository.set(key, tradeDTO);
		//构建订单创建消息
		String destination =
			rocketmqCustomProperties.getOrderTopic() + ":" + OrderTagsEnum.ORDER_CREATE.name();
		//发送订单创建消息
		rocketMQTemplate.asyncSend(destination, key, RocketmqSendCallbackBuilder.commonCallback());
		return trade;
	}

	/**
	 * 创建订单最后一步校验
	 *
	 * @param tradeDTO
	 */
	private void createTradeCheck(TradeDTO tradeDTO) {
		//创建订单如果没有收获地址，
		MemberAddress memberAddress = tradeDTO.getMemberAddress();
		if (memberAddress == null) {
			throw new BusinessException(ResultEnum.MEMBER_ADDRESS_NOT_EXIST);
		}

		//订单配送区域校验
		if (tradeDTO.getNotSupportFreight() != null && tradeDTO.getNotSupportFreight().size() > 0) {
			StringBuilder stringBuilder = new StringBuilder("包含商品有-");
			tradeDTO.getNotSupportFreight().forEach(sku -> {
				stringBuilder.append(sku.getGoodsSku().getGoodsName());
			});
			throw new BusinessException(ResultEnum.ORDER_NOT_SUPPORT_DISTRIBUTION.getCode(),
				stringBuilder.toString());
		}
	}

	@Override
	public Trade getBySn(String sn) {
		LambdaQueryWrapper<Trade> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Trade::getSn, sn);
		return this.getOne(queryWrapper);
	}


	@Override
	public void payTrade(String tradeSn, String paymentName, String receivableNo) {
		LambdaQueryWrapper<Order> orderQueryWrapper = new LambdaQueryWrapper<>();
		orderQueryWrapper.eq(Order::getTradeSn, tradeSn);
		List<Order> orders = orderService.list(orderQueryWrapper);
		for (Order order : orders) {
			orderService.payOrder(order.getSn(), paymentName, receivableNo);
		}
		Trade trade = this.getBySn(tradeSn);
		trade.setPayStatus(PayStatusEnum.PAID.name());
		this.saveOrUpdate(trade);
	}

	/**
	 * 优惠券预处理 下单同时，扣除优惠券
	 *
	 * @param tradeDTO
	 */
	private void couponPretreatment(TradeDTO tradeDTO) {
		List<MemberCouponDTO> memberCouponDTOList = new ArrayList<>();
		if (null != tradeDTO.getPlatformCoupon()) {
			memberCouponDTOList.add(tradeDTO.getPlatformCoupon());
		}
		Collection<MemberCouponDTO> storeCoupons = tradeDTO.getStoreCoupons().values();
		if (!storeCoupons.isEmpty()) {
			memberCouponDTOList.addAll(storeCoupons);
		}
		List<String> ids = memberCouponDTOList.stream().map(e -> e.getMemberCoupon().getId())
			.collect(Collectors.toList());
		memberCouponService.used(ids);
		memberCouponDTOList.forEach(
			e -> couponService.usedCoupon(e.getMemberCoupon().getCouponId(), 1));
	}

	/**
	 * 创建交易，积分处理
	 *
	 * @param tradeDTO 交易对象
	 */
	private void pointPretreatment(TradeDTO tradeDTO) {
		//需要支付积分
		if (tradeDTO.getPriceDetailDTO() != null
			&& tradeDTO.getPriceDetailDTO().getPayPoint() != null
			&& tradeDTO.getPriceDetailDTO().getPayPoint() > 0) {
			StringBuilder orderSns = new StringBuilder();
			for (CartVO item : tradeDTO.getCartList()) {
				orderSns.append(item.getSn());
			}
			Result<Boolean> result = memberService.updateMemberPoint(
				tradeDTO.getPriceDetailDTO().getPayPoint(), PointTypeEnum.REDUCE.name(),
				tradeDTO.getMemberId(),
				"订单【" + orderSns + "】创建，积分扣减");

			if (!result.data()) {
				throw new BusinessException(ResultEnum.PAY_POINT_ENOUGH);
			}
		}
	}

	/**
	 * 创建交易、砍价处理
	 *
	 * @param tradeDTO
	 */
	private void kanjiaPretreatment(TradeDTO tradeDTO) {
		if (tradeDTO.getCartTypeEnum().equals(CartTypeEnum.KANJIA)) {
			String kanjiaId = tradeDTO.getSkuList().get(0).getKanjiaId();
			kanjiaActivityService.update(new LambdaUpdateWrapper<KanjiaActivity>()
				.eq(KanjiaActivity::getId, kanjiaId)
				.set(KanjiaActivity::getStatus, KanJiaStatusEnum.END.name()));
		}
	}

}
