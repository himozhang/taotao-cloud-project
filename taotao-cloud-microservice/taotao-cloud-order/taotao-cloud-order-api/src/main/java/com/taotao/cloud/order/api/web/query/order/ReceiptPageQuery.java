package com.taotao.cloud.order.api.web.query.order;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.taotao.cloud.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.Objects;

/**
 * 发票搜索参数
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-21 16:59:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "发票搜索参数")
public class ReceiptPageQuery extends PageParam {

	@Serial
	private static final long serialVersionUID = 8808470688518188146L;

	@Schema(description = "发票抬头")
	private String receiptTitle;

	@Schema(description = "纳税人识别号")
	private Long taxpayerId;

	@Schema(description = "会员ID")
	private Long memberId;

	@Schema(description = "会员名称")
	private String memberName;

	@Schema(description = "店铺名称")
	private String storeName;

	@Schema(description = "商家ID")
	private Long storeId;

	@Schema(description = "订单号")
	private String orderSn;

	@Schema(description = "发票状态")
	private String receiptStatus;

	public <T> QueryWrapper<T> wrapper() {
		QueryWrapper<T> queryWrapper = new QueryWrapper<>();
		if (StrUtil.isNotEmpty(receiptTitle)) {
			queryWrapper.like("r.receipt_title", receiptTitle);
		}
		if (Objects.nonNull(taxpayerId)) {
			queryWrapper.like("r.taxpayer_id", taxpayerId);
		}
		if (Objects.nonNull(memberId)) {
			queryWrapper.eq("r.member_id", memberId);
		}
		if (StrUtil.isNotEmpty(storeName)) {
			queryWrapper.like("r.store_name", storeName);
		}
		if (Objects.nonNull(storeId)) {
			queryWrapper.eq("r.store_id", storeId);
		}
		if (StrUtil.isNotEmpty(memberName)) {
			queryWrapper.like("r.member_name", memberName);
		}
		if (StrUtil.isNotEmpty(receiptStatus)) {
			queryWrapper.like("r.receipt_status", receiptStatus);
		}
		if (StrUtil.isNotEmpty(orderSn)) {
			queryWrapper.like("r.order_sn", orderSn);
		}
		queryWrapper.eq("r.delete_flag", false);
		return queryWrapper;
	}

}
