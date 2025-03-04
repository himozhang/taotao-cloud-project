package com.taotao.cloud.order.biz.api.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.member.api.web.dto.MemberAddressDTO;
import com.taotao.cloud.order.api.web.query.order.OrderPageQuery;
import com.taotao.cloud.order.api.web.vo.cart.OrderExportVO;
import com.taotao.cloud.order.api.web.vo.order.OrderDetailVO;
import com.taotao.cloud.order.api.web.vo.order.OrderSimpleVO;
import com.taotao.cloud.order.biz.model.entity.order.Order;
import com.taotao.cloud.order.biz.service.order.IOrderPriceService;
import com.taotao.cloud.order.biz.service.order.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 管理端,订单API
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:57:16
 */
@AllArgsConstructor
@Validated
@RestController
@Tag(name = "平台管理端-订单管理API", description = "平台管理端-订单管理API")
@RequestMapping("/order/manager/order")
public class OrderController {

	/**
	 * 订单
	 */
	private final IOrderService orderService;
	/**
	 * 订单价格
	 */
	private final IOrderPriceService orderPriceService;

	@Operation(summary = "查询订单列表分页", description = "查询订单列表分页")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping("/tree")
	public Result<IPage<OrderSimpleVO>> queryMineOrder(OrderPageQuery orderPageQuery) {
		return Result.success(orderService.queryByParams(orderPageQuery));
	}

	@Operation(summary = "查询订单导出列表", description = "查询订单导出列表")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping("/queryExportOrder")
	public Result<List<OrderExportVO>> queryExportOrder(
		OrderPageQuery orderPageQuery) {
		return Result.success(orderService.queryExportOrder(orderPageQuery));
	}

	@Operation(summary = "订单明细", description = "订单明细")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/{orderSn}")
	public Result<OrderDetailVO> detail(@PathVariable String orderSn) {
		return Result.success(orderService.queryDetail(orderSn));
	}

	@Operation(summary = "确认收款", description = "确认收款")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PostMapping(value = "/{orderSn}/pay")
	public Result<Boolean> payOrder(@PathVariable String orderSn) {
		return Result.success(orderPriceService.adminPayOrder(orderSn));
	}

	@Operation(summary = "修改收货人信息", description = "修改收货人信息")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PutMapping(value = "/{orderSn}/consignee")
	public Result<Order> consignee(@NotNull(message = "参数非法") @PathVariable String orderSn,
		@Valid MemberAddressDTO memberAddressDTO) {
		return Result.success(orderService.updateConsignee(orderSn, memberAddressDTO));
	}

	@Operation(summary = "修改订单价格", description = "修改订单价格")
	@RequestLogger
	@PutMapping(value = "/{orderSn}/price")
	public Result<Boolean> updateOrderPrice(@PathVariable String orderSn,
		@NotNull(message = "订单价格不能为空") @RequestParam BigDecimal price) {
		return Result.success(orderPriceService.updatePrice(orderSn, price));
	}

	@Operation(summary = "取消订单", description = "取消订单")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PostMapping(value = "/{orderSn}/cancel")
	public Result<Order> cancel(@PathVariable String orderSn,
		@RequestParam String reason) {
		return Result.success(orderService.cancel(orderSn, reason));
	}

	@Operation(summary = "查询物流踪迹", description = "查询物流踪迹")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PostMapping(value = "/traces/{orderSn}")
	public Result<Object> getTraces(
		@NotBlank(message = "订单编号不能为空") @PathVariable String orderSn) {
		return Result.success(orderService.getTraces(orderSn));
	}
}
