package com.taotao.cloud.order.biz.api.controller.seller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.common.model.PageModel;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.common.utils.common.OperationalJudgment;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.order.api.web.query.aftersale.AfterSalePageQuery;
import com.taotao.cloud.order.api.web.vo.aftersale.AfterSaleVO;
import com.taotao.cloud.order.biz.model.entity.aftersale.AfterSale;
import com.taotao.cloud.order.biz.mapstruct.IAfterSaleMapStruct;
import com.taotao.cloud.order.biz.service.aftersale.IAfterSaleService;
import com.taotao.cloud.store.api.web.vo.StoreAfterSaleAddressVO;
import com.taotao.cloud.sys.api.web.vo.logistics.TracesVO;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺端,售后管理API
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:57:33
 */
@AllArgsConstructor
@Validated
@RestController
@Tag(name = "店铺端-售后API", description = "店铺端-售后API")
@RequestMapping("/order/seller/aftersale")
public class AfterSaleController {

	private final IAfterSaleService afterSaleService;

	@Operation(summary = "查看售后服务详情", description = "查看售后服务详情")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/{sn}")
	public Result<AfterSaleVO> get(@PathVariable String sn) {
		AfterSale afterSale = OperationalJudgment.judgment(afterSaleService.getAfterSale(sn));
		return Result.success(IAfterSaleMapStruct.INSTANCE.afterSaleToAfterSaleVO(afterSale));
	}

	@Operation(summary = "分页获取售后服务", description = "分页获取售后服务")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/page")
	public Result<PageModel<AfterSaleVO>> getByPage(AfterSalePageQuery searchParams) {
		Long storeId = SecurityUtil.getCurrentUser().getStoreId();
		searchParams.setStoreId(storeId);
		IPage<AfterSale> afterSalePages = afterSaleService.getAfterSalePages(searchParams);
		return Result.success(PageModel.convertMybatisPage(afterSalePages, AfterSaleVO.class));
	}

	@Operation(summary = "获取导出售后服务列表列表", description = "获取导出售后服务列表列表")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/exportAfterSaleOrder")
	public Result<List<AfterSaleVO>> exportAfterSaleOrder(AfterSalePageQuery searchParams) {
		Long storeId = SecurityUtil.getCurrentUser().getStoreId();
		searchParams.setStoreId(storeId);
		List<AfterSale> afterSales = afterSaleService.exportAfterSaleOrder(searchParams);
		return Result.success(IAfterSaleMapStruct.INSTANCE.afterSalesToAfterSaleVOs(afterSales));
	}

	@Operation(summary = "审核售后申请", description = "审核售后申请")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PostMapping(value = "/review/{afterSaleSn}")
	public Result<Boolean> review(
		@NotNull(message = "请选择售后单") @PathVariable String afterSaleSn,
		@NotNull(message = "请审核") String serviceStatus,
		String remark,
		BigDecimal actualRefundPrice) {

		return Result.success(
			afterSaleService.review(afterSaleSn, serviceStatus, remark, actualRefundPrice));
	}

	@Operation(summary = "卖家确认收货", description = "卖家确认收货")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping("/tree")
	@PutMapping(value = "/confirm/{afterSaleSn}")
	public Result<Boolean> confirm(
		@NotNull(message = "请选择售后单") @PathVariable String afterSaleSn,
		@NotNull(message = "请审核") String serviceStatus,
		String remark) {
		return Result.success(afterSaleService.storeConfirm(afterSaleSn, serviceStatus, remark));
	}

	@Operation(summary = "查看买家退货物流踪迹", description = "查看买家退货物流踪迹")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/getDeliveryTraces/{sn}")
	public Result<TracesVO> getDeliveryTraces(@PathVariable String sn) {
		return Result.success(afterSaleService.deliveryTraces(sn));
	}

	@Operation(summary = "获取商家售后收件地址", description = "获取商家售后收件地址")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/getStoreAfterSaleAddress/{sn}")
	public Result<StoreAfterSaleAddressVO> getStoreAfterSaleAddress(
		@NotNull(message = "售后单号") @PathVariable("sn") String sn) {
		return Result.success(afterSaleService.getStoreAfterSaleAddressDTO(sn));
	}

}
