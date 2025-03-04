package com.taotao.cloud.order.biz.service.cart.render.impl;

import com.taotao.cloud.common.utils.number.CurrencyUtil;
import com.taotao.cloud.order.api.web.dto.cart.TradeDTO;
import com.taotao.cloud.order.api.enums.cart.RenderStepEnums;
import com.taotao.cloud.order.api.web.vo.cart.CartSkuVO;
import com.taotao.cloud.order.biz.service.cart.render.ICartRenderStep;
import com.taotao.cloud.store.api.web.dto.FreightTemplateChildDTO;
import com.taotao.cloud.store.api.enums.FreightTemplateEnum;
import com.taotao.cloud.store.api.web.vo.FreightTemplateInfoVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * sku 运费计算
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:50:51
 */
@AllArgsConstructor
@Service
public class SkuFreightRender implements ICartRenderStep {

	private final FreightTemplateService freightTemplateService;

	@Override
	public RenderStepEnums step() {
		return RenderStepEnums.SKU_FREIGHT;
	}

	@Override
	public void render(TradeDTO tradeDTO) {
		List<CartSkuVO> cartSkuVOS = tradeDTO.getCheckedSkuList();
		//会员收货地址问题处理
		MemberAddress memberAddress = tradeDTO.getMemberAddress();
		//如果收货地址为空，则抛出异常
		if (memberAddress == null) {
			return;
		}
		//循环渲染购物车商品运费价格
		forSku:
		for (CartSkuVO cartSkuVO : cartSkuVOS) {
			//获取sku运费模版
			String freightTemplateId = cartSkuVO.getGoodsSku().getFreightTemplateId();
			//免运费则跳出运费计算
			if (Boolean.TRUE.equals(cartSkuVO.getIsFreeFreight()) || freightTemplateId == null) {
				continue;
			}
			//寻找对应对商品运费计算模版
			FreightTemplateInfoVO freightTemplate = freightTemplateService.getFreightTemplate(
				freightTemplateId);
			if (freightTemplate != null
				&& freightTemplate.getFreightTemplateChildList() != null
				&& !freightTemplate.getFreightTemplateChildList().isEmpty()) {
				//店铺支付运费则跳过
				if (freightTemplate.getPricingMethod().equals(FreightTemplateEnum.FREE.name())) {
					break;
				}
				FreightTemplateChild freightTemplateChild = null;

				//获取市级别id
				String addressId = memberAddress.getConsigneeAddressIdPath().split(",")[1];
				//获取匹配的收货地址
				for (FreightTemplateChild templateChild : freightTemplate.getFreightTemplateChildList()) {
					//如果当前模版包含，则返回
					if (templateChild.getAreaId().contains(addressId)) {
						freightTemplateChild = templateChild;
						break;
					}
				}
				//如果没有匹配到物流规则，则说明不支持配送
				if (freightTemplateChild == null) {
					if (tradeDTO.getNotSupportFreight() == null) {
						tradeDTO.setNotSupportFreight(new ArrayList<>());
					}
					tradeDTO.getNotSupportFreight().add(cartSkuVO);
					continue forSku;
				}

				//物流规则模型创立
				FreightTemplateChildDTO freightTemplateChildDTO = new FreightTemplateChildDTO(
					freightTemplateChild);

				freightTemplateChildDTO.setPricingMethod(freightTemplate.getPricingMethod());

				//要计算的基数 数量/重量
				BigDecimal count = (freightTemplateChildDTO.getPricingMethod()
					.equals(FreightTemplateEnum.NUM.name())) ?
					cartSkuVO.getNum() :
					cartSkuVO.getGoodsSku().getWeight() * cartSkuVO.getNum();

				//计算运费
				BigDecimal countFreight = countFreight(count, freightTemplateChildDTO);
				//写入SKU运费
				cartSkuVO.getPriceDetailDTO().setFreightPrice(countFreight);
			}
		}
	}

	/**
	 * 计算运费
	 *
	 * @param count    重量/件
	 * @param template 计算模版
	 * @return {@link BigDecimal }
	 * @since 2022-04-28 08:54:10
	 */
	private BigDecimal countFreight(BigDecimal count, FreightTemplateChildDTO template) {
		try {
			BigDecimal finalFreight = template.getFirstPrice();
			//不满首重 / 首件
			if (template.getFirstCompany() >= count) {
				return finalFreight;
			}
			//如果续重/续件，费用不为空，则返回
			if (template.getContinuedCompany() == 0 || template.getContinuedPrice() == 0) {
				return finalFreight;
			}

			//计算 续重 / 续件 价格
			BigDecimal continuedCount = count - template.getFirstCompany();
			return CurrencyUtil.add(finalFreight,
				CurrencyUtil.mul(Math.ceil(continuedCount / template.getContinuedCompany()),
					template.getContinuedPrice()));
		} catch (Exception e) {
			return 0D;
		}


	}


}
