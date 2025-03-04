package com.taotao.cloud.order.biz.service.cart.render.impl;

import com.taotao.cloud.order.api.web.dto.cart.TradeDTO;
import com.taotao.cloud.order.api.web.dto.order.PriceDetailDTO;
import com.taotao.cloud.order.api.enums.cart.RenderStepEnums;
import com.taotao.cloud.order.api.web.vo.cart.CartSkuVO;
import com.taotao.cloud.order.api.web.vo.cart.CartVO;
import com.taotao.cloud.order.biz.service.cart.render.ICartRenderStep;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车渲染，将购物车中的各个商品，拆分到每个商家，形成购物车VO
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:50:24
 */
@Service
public class CartPriceRender implements ICartRenderStep {

	@Override
	public RenderStepEnums step() {
		return RenderStepEnums.CART_PRICE;
	}

	@Override
	public void render(TradeDTO tradeDTO) {
		//价格过滤 在购物车商品失效时，需要对价格进行初始化操作
		initPriceDTO(tradeDTO);

		//构造cartVO
		buildCartPrice(tradeDTO);
		buildTradePrice(tradeDTO);
	}

	/**
	 * 特殊情况下对购物车金额进行护理
	 *
	 * @param tradeDTO tradeDTO
	 */
	private void initPriceDTO(TradeDTO tradeDTO) {
		tradeDTO.getCartList().forEach(cartVO -> cartVO.setPriceDetailDTO(new PriceDetailDTO()));
		tradeDTO.setPriceDetailDTO(new PriceDetailDTO());
	}

	/**
	 * 购物车价格
	 *
	 * @param tradeDTO 购物车展示信息
	 * @since 2022-04-28 08:51:55
	 */
	void buildCartPrice(TradeDTO tradeDTO) {
		//购物车列表
		List<CartVO> cartVOS = tradeDTO.getCartList();

		cartVOS.forEach(cartVO -> {
			cartVO.getPriceDetailDTO().accumulationPriceDTO(
				cartVO.getCheckedSkuList().stream().filter(CartSkuVO::getChecked)
					.map(CartSkuVO::getPriceDetailDTO).collect(Collectors.toList())
			);
			List<Integer> skuNum = cartVO.getSkuList().stream().filter(CartSkuVO::getChecked)
				.map(CartSkuVO::getNum).toList();
			for (Integer num : skuNum) {
				cartVO.addGoodsNum(num);
			}
		});
	}


	/**
	 * 初始化购物车
	 *
	 * @param tradeDTO 购物车展示信息
	 * @since 2022-04-28 08:51:52
	 */
	void buildTradePrice(TradeDTO tradeDTO) {
		tradeDTO.getPriceDetailDTO().accumulationPriceDTO(
			tradeDTO.getCartList().stream().map(CartVO::getPriceDetailDTO)
				.collect(Collectors.toList()));
	}

}
