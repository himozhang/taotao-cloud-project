package com.taotao.cloud.promotion.api.web.query;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import lombok.experimental.SuperBuilder;

/**
 * 拼团查询通用类
 */
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PintuanPageQuery extends BasePromotionsSearchQuery {

	@Schema(description = "商家id")
	private String storeId;

	@Schema(description = "商家名称，如果是平台，这个值为 platform")
	private String storeName;

	@NotEmpty(message = "活动名称不能为空")
	@Schema(description = "活动名称", required = true)
	private String promotionName;


	@Override
	public <T> QueryWrapper<T> queryWrapper() {
		QueryWrapper<T> queryWrapper = super.queryWrapper();
		if (CharSequenceUtil.isNotEmpty(promotionName)) {
			queryWrapper.like("promotion_name", promotionName);
		}
		if (CharSequenceUtil.isNotEmpty(storeName)) {
			queryWrapper.like("store_name", storeName);
		}
		if (CharSequenceUtil.isNotEmpty(storeId)) {
			queryWrapper.eq("store_id", storeId);
		}
		return queryWrapper;
	}

}
