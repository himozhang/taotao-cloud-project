package com.taotao.cloud.distribution.api.web.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.common.utils.lang.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 分销员对象
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分销订单查询对象")
public class DistributionOrderPageQuery extends PageParam {

	@Serial
	private static final long serialVersionUID = -8736018687663645064L;

	@Schema(description = "分销员名称")
	private String distributionName;

	@Schema(description = "订单sn")
	private String orderSn;

	@Schema(description = "分销员ID", hidden = true)
	private String distributionId;

	@Schema(description = "分销订单状态")
	private String distributionOrderStatus;

	@Schema(description = "店铺ID")
	private String storeId;

	@Schema(description = "开始时间")
	private LocalDateTime startTime;

	@Schema(description = "结束时间")
	private LocalDateTime endTime;

	public <T> QueryWrapper<T> queryWrapper() {
		QueryWrapper<T> queryWrapper = Wrappers.query();
		queryWrapper.like(StringUtil.isNotBlank(distributionName), "distribution_name",
			distributionName);
		queryWrapper.eq(StringUtil.isNotBlank(distributionOrderStatus), "distribution_order_status",
			distributionOrderStatus);
		queryWrapper.eq(StringUtil.isNotBlank(orderSn), "order_sn", orderSn);
		queryWrapper.eq(StringUtil.isNotBlank(distributionId), "distribution_id", distributionId);
		queryWrapper.eq(StringUtil.isNotBlank(storeId), "store_id", storeId);
		if (endTime != null && startTime != null) {
			queryWrapper.between("create_time", startTime, endTime);
		}
		return queryWrapper;
	}

}
