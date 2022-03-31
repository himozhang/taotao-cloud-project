package com.taotao.cloud.order.api.vo.order;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单交易投诉VO
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "订单交易投诉VO")
public class OrderComplaintVO extends OrderComplaint {


	private static final long serialVersionUID = -7013465343480854816L;

	@Schema(description = "投诉对话")
	private List<OrderComplaintCommunication> orderComplaintCommunications;

	@Schema(description = "投诉图片")
	private String[] orderComplaintImages;

	@Schema(description = "申诉商家上传的图片")
	private String[] appealImagesList;

	public OrderComplaintVO(OrderComplaint orderComplaint) {
		BeanUtil.copyProperties(orderComplaint, this);
	}
}
