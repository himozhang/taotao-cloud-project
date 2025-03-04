package com.taotao.cloud.order.biz.service.cart.render.impl;

import com.taotao.cloud.common.enums.PromotionTypeEnum;
import com.taotao.cloud.common.utils.number.CurrencyUtil;
import com.taotao.cloud.order.api.web.dto.cart.TradeDTO;
import com.taotao.cloud.order.api.web.dto.order.PriceDetailDTO;
import com.taotao.cloud.order.api.enums.cart.RenderStepEnums;
import com.taotao.cloud.order.api.web.vo.cart.CartSkuVO;
import com.taotao.cloud.order.api.web.vo.cart.CartVO;
import com.taotao.cloud.order.biz.service.cart.render.ICartRenderStep;
import com.taotao.cloud.promotion.api.enums.KanJiaStatusEnum;
import com.taotao.cloud.promotion.api.web.vo.PromotionSkuVO;
import com.taotao.cloud.promotion.api.web.vo.kanjia.KanjiaActivityVO;
import lombok.AllArgsConstructor;
import org.apache.shardingsphere.distsql.parser.autogen.CommonDistSQLStatementParser.UserContext;
import org.springframework.stereotype.Service;

/**
 * 购物促销信息渲染实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:50:54
 */
@AllArgsConstructor
@Service
public class SkuPromotionRender implements ICartRenderStep {

	/**
	 * 促销商品
	 */
	private final PromotionGoodsService promotionGoodsService;

	private final KanjiaActivityService kanjiaActivityService;

	@Override
	public RenderStepEnums step() {
		return RenderStepEnums.SKU_PROMOTION;
	}

	@Override
	public void render(TradeDTO tradeDTO) {
		//基础价格渲染
		renderBasePrice(tradeDTO);
		//渲染单品促销
		renderSkuPromotion(tradeDTO);
	}

	/**
	 * 基础价格渲染
	 *
	 * @param tradeDTO tradeDTO
	 * @since 2022-04-28 08:54:15
	 */
	private void renderBasePrice(TradeDTO tradeDTO) {
		tradeDTO.getCartList().forEach(
			cartVO -> cartVO.getCheckedSkuList().forEach(cartSkuVO -> {
				PriceDetailDTO priceDetailDTO = cartSkuVO.getPriceDetailDTO();
				priceDetailDTO.setGoodsPrice(cartSkuVO.getSubTotal());
				priceDetailDTO.setDiscountPrice(
					CurrencyUtil.sub(priceDetailDTO.getOriginalPrice(), cartSkuVO.getSubTotal()));
			})
		);
	}

	/**
	 * 渲染单品优惠 积分/拼团/秒杀/砍价
	 *
	 * @param tradeDTO 购物车视图
	 * @since 2022-04-28 08:54:17
	 */
	private void renderSkuPromotion(TradeDTO tradeDTO) {
		switch (tradeDTO.getCartTypeEnum()) {
			//这里是双重循环，但是实际积分购买或者是砍价购买时，购物车只有一个商品，所以没有循环操作数据库或者其他的问题
			case POINTS:
				//处理积分商品购买
				for (CartVO cartVO : tradeDTO.getCartList()) {
					for (CartSkuVO cartSkuVO : cartVO.getCheckedSkuList()) {
						cartSkuVO.getPriceDetailDTO().setPayPoint(cartSkuVO.getPoint());
						PromotionSkuVO promotionSkuVO = new PromotionSkuVO(
							PromotionTypeEnum.POINTS_GOODS.name(), cartSkuVO.getPointsId());
						cartSkuVO.getPriceDetailDTO().getJoinPromotion().add(promotionSkuVO);
					}
				}
				return;
			case KANJIA:
				for (CartVO cartVO : tradeDTO.getCartList()) {
					for (CartSkuVO cartSkuVO : cartVO.getCheckedSkuList()) {
						KanjiaActivitySearchParams kanjiaActivitySearchParams = new KanjiaActivitySearchParams();
						kanjiaActivitySearchParams.setGoodsSkuId(cartSkuVO.getGoodsSku().getId());
						kanjiaActivitySearchParams.setMemberId(
							UserContext.getCurrentUser().getId());
						kanjiaActivitySearchParams.setStatus(KanJiaStatusEnum.SUCCESS.name());
						KanjiaActivityVO kanjiaActivityVO = kanjiaActivityService.getKanjiaActivityVO(
							kanjiaActivitySearchParams);
						//可以砍价金额购买，则处理信息
						if (kanjiaActivityVO.getPass()) {
							cartSkuVO.setKanjiaId(kanjiaActivityVO.getId());
							cartSkuVO.setPurchasePrice(kanjiaActivityVO.getPurchasePrice());
							cartSkuVO.setSubTotal(kanjiaActivityVO.getPurchasePrice());
							cartSkuVO.getPriceDetailDTO()
								.setGoodsPrice(kanjiaActivityVO.getPurchasePrice());
						}

						PromotionSkuVO promotionSkuVO = new PromotionSkuVO(
							PromotionTypeEnum.KANJIA.name(), cartSkuVO.getKanjiaId());
						cartSkuVO.getPriceDetailDTO().getJoinPromotion().add(promotionSkuVO);
					}
				}
				return;
			case PINTUAN:
				for (CartVO cartVO : tradeDTO.getCartList()) {
					for (CartSkuVO cartSkuVO : cartVO.getCheckedSkuList()) {
						PromotionSkuVO promotionSkuVO = new PromotionSkuVO(
							PromotionTypeEnum.PINTUAN.name(), cartSkuVO.getPintuanId());
						cartSkuVO.getPriceDetailDTO().getJoinPromotion().add(promotionSkuVO);
					}
				}
				return;
			case CART:
			case BUY_NOW:
				return;
			case VIRTUAL:
				//循环购物车
				for (CartVO cartVO : tradeDTO.getCartList()) {
					//循环sku
					for (CartSkuVO cartSkuVO : cartVO.getCheckedSkuList()) {
						//更新商品促销
						promotionGoodsService.updatePromotion(cartSkuVO);
						//赋予商品促销信息
						for (PromotionGoods promotionGoods : cartSkuVO.getPromotions()) {

							// 忽略拼团活动
							if (promotionGoods.getPromotionType()
								.equals(PromotionTypeEnum.PINTUAN.name())) {
								continue;
							}
							PromotionSkuVO promotionSkuVO = new PromotionSkuVO(
								promotionGoods.getPromotionType(), promotionGoods.getPromotionId());
							cartSkuVO.setPurchasePrice(promotionGoods.getPrice());
							cartSkuVO.setSubTotal(
								CurrencyUtil.mul(promotionGoods.getPrice(), cartSkuVO.getNum()));
							cartSkuVO.getPriceDetailDTO().setGoodsPrice(cartSkuVO.getSubTotal());

							cartSkuVO.getPriceDetailDTO().getJoinPromotion().add(promotionSkuVO);
						}
					}
				}
				return;
			default:
		}
	}


}
