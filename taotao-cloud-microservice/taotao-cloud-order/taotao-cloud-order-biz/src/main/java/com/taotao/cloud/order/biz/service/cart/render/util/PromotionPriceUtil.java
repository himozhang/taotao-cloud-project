package com.taotao.cloud.order.biz.service.cart.render.util;

import com.taotao.cloud.common.enums.PromotionTypeEnum;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.common.utils.number.CurrencyUtil;
import com.taotao.cloud.order.api.web.dto.cart.TradeDTO;
import com.taotao.cloud.order.api.web.vo.cart.CartSkuVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 促销价格计算业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:50:15
 */
@Service
public class PromotionPriceUtil {

	/**
	 * 重新计算购物车价格
	 *
	 * @param tradeDTO           交易DTO
	 * @param skuPromotionDetail 参与活动的商品，以及商品总金额
	 * @param discountPrice      需要分发的优惠金额
	 * @param promotionTypeEnum  促销类型
	 */
	public void recountPrice(TradeDTO tradeDTO, Map<String, BigDecimal> skuPromotionDetail,
							 BigDecimal discountPrice, PromotionTypeEnum promotionTypeEnum) {

		// sku 促销信息非空判定
		if (skuPromotionDetail == null || skuPromotionDetail.size() == 0) {
			return;
		}

		//计算总金额
		BigDecimal totalPrice = 0D;
		for (BigDecimal value : skuPromotionDetail.values()) {
			totalPrice = CurrencyUtil.add(totalPrice, value);
		}

		//极端情况，如果扣减金额小于需要支付的金额，则扣减金额=支付金额，不能成为负数
		if (discountPrice > totalPrice) {
			discountPrice = totalPrice;
			for (String skuId : skuPromotionDetail.keySet()) {
				//获取对应商品进行计算
				for (CartSkuVO cartSkuVO : tradeDTO.getSkuList()) {
					if (cartSkuVO.getGoodsSku().getId().equals(skuId)) {
						//优惠券金额，则计入优惠券 ，其他则计入总的discount price
						if (promotionTypeEnum == PromotionTypeEnum.COUPON) {
							cartSkuVO.getPriceDetailDTO()
								.setCouponPrice(cartSkuVO.getPriceDetailDTO().getGoodsPrice());
						} else {
							cartSkuVO.getPriceDetailDTO()
								.setDiscountPrice(cartSkuVO.getPriceDetailDTO().getGoodsPrice());
						}
					}
				}
			}
		}

		//获取购物车信息
		List<CartSkuVO> skuVOList = tradeDTO.getSkuList();

		// 获取map分配sku的总数，如果是最后一个商品分配金额，则将金额从百分比改为总金额扣减，避免出现小数除不尽
		Integer count = skuPromotionDetail.size();

		//已优惠金额
		BigDecimal deducted = 0D;
		for (String skuId : skuPromotionDetail.keySet()) {
			//获取对应商品进行计算
			for (CartSkuVO cartSkuVO : skuVOList) {
				if (cartSkuVO.getGoodsSku().getId().equals(skuId)) {
					count--;

					//sku 优惠金额
					BigDecimal skuDiscountPrice = 0d;
					//非最后一个商品，则按照比例计算
					if (count > 0) {
						//商品金额占比
						BigDecimal point = CurrencyUtil.div(
							cartSkuVO.getPriceDetailDTO().getGoodsPrice(), totalPrice, 4);
						//商品优惠金额
						skuDiscountPrice = CurrencyUtil.mul(discountPrice, point);
						//累加已优惠金额
						deducted = CurrencyUtil.add(deducted, skuDiscountPrice);
					}
					// 如果是最后一个商品 则减去之前优惠的金额来进行计算
					else {
						skuDiscountPrice = CurrencyUtil.sub(discountPrice, deducted);
					}
					//优惠券金额，则计入优惠券 ，其他则计入总的discount price
					if (promotionTypeEnum == PromotionTypeEnum.COUPON) {

						cartSkuVO.getPriceDetailDTO().setCouponPrice(
							CurrencyUtil.add(cartSkuVO.getPriceDetailDTO().getCouponPrice(),
								skuDiscountPrice));
					} else if (promotionTypeEnum == PromotionTypeEnum.PLATFORM_COUPON) {

						cartSkuVO.getPriceDetailDTO().setSiteCouponPrice(
							CurrencyUtil.add(cartSkuVO.getPriceDetailDTO().getCouponPrice(),
								skuDiscountPrice));

						cartSkuVO.getPriceDetailDTO().setCouponPrice(
							CurrencyUtil.add(cartSkuVO.getPriceDetailDTO().getCouponPrice(),
								cartSkuVO.getPriceDetailDTO().getSiteCouponPrice()));
					} else {
						cartSkuVO.getPriceDetailDTO().setDiscountPrice(
							CurrencyUtil.add(cartSkuVO.getPriceDetailDTO().getDiscountPrice(),
								skuDiscountPrice));
					}
				}
			}
		}


	}


	/**
	 * 检查活动有效时间
	 *
	 * @param startTime     活动开始时间
	 * @param endTime       活动结束时间
	 * @param promotionType 活动类型
	 * @param promotionId   活动ID
	 * @return 是否有效
	 */
	private boolean checkPromotionValidTime(Date startTime, Date endTime, String promotionType,
											String promotionId) {
		long now = System.currentTimeMillis();
		if (startTime.getTime() > now) {
			LogUtil.error("商品ID为{}的{}活动开始时间小于当时时间，活动未开始！", promotionId, promotionType);
			return false;
		}
		if (endTime.getTime() < now) {
			LogUtil.error("活动ID为{}的{}活动结束时间大于当时时间，活动已结束！", promotionId, promotionType);
			return false;
		}
		return true;
	}
}
