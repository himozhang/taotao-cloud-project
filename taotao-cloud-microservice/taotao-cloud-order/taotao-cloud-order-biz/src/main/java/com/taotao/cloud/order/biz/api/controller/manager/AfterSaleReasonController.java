package com.taotao.cloud.order.biz.api.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.common.model.PageModel;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.order.api.web.dto.aftersale.AfterSaleReasonDTO;
import com.taotao.cloud.order.api.web.query.aftersale.AfterSaleReasonPageQuery;
import com.taotao.cloud.order.api.web.vo.aftersale.AfterSaleReasonVO;
import com.taotao.cloud.order.biz.model.entity.aftersale.AfterSaleReason;
import com.taotao.cloud.order.biz.mapstruct.IAfterSaleReasonMapStruct;
import com.taotao.cloud.order.biz.service.aftersale.IAfterSaleReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端,售后原因API
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:57:11
 */
@AllArgsConstructor
@Validated
@RestController
@Tag(name = "平台管理端-售后原因管理API", description = "平台管理端-售后原因管理API")
@RequestMapping("/order/manager/aftersale/reason")
public class AfterSaleReasonController {

	/**
	 * 售后原因
	 */
	private final IAfterSaleReasonService afterSaleReasonService;

	@Operation(summary = "查看售后原因", description = "查看售后原因")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/{id}")
	public Result<AfterSaleReasonVO> get(@PathVariable String id) {
		AfterSaleReason afterSaleReason = afterSaleReasonService.getById(id);
		return Result.success(IAfterSaleReasonMapStruct.INSTANCE.afterSaleReasonToAfterSaleReasonVO(afterSaleReason));
	}

	@Operation(summary = "分页获取售后原因", description = "分页获取售后原因")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/page")
	public Result<PageModel<AfterSaleReasonVO>> getByPage(@Validated AfterSaleReasonPageQuery afterSaleReasonPageQuery) {
		IPage<AfterSaleReason> afterSaleReasonPage = afterSaleReasonService.getByPage(afterSaleReasonPageQuery);
		return Result.success(PageModel.convertMybatisPage(afterSaleReasonPage, AfterSaleReasonVO.class));
	}

	@Operation(summary = "添加售后原因", description = "添加售后原因")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PostMapping
	public Result<Boolean> save(@Validated @RequestBody AfterSaleReasonDTO afterSaleReasonDTO) {
		afterSaleReasonService.save(IAfterSaleReasonMapStruct.INSTANCE.afterSaleReasonDTOToAfterSaleReason(afterSaleReasonDTO));
		return Result.success(true);
	}

	@Operation(summary = "修改售后原因", description = "修改售后原因")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PutMapping("/{id}")
	public Result<Boolean> update(@Validated @RequestBody AfterSaleReasonDTO afterSaleReasonDTO,
								  @PathVariable("id") Long id) {
		AfterSaleReason afterSaleReason = IAfterSaleReasonMapStruct.INSTANCE.afterSaleReasonDTOToAfterSaleReason(afterSaleReasonDTO);
		afterSaleReason.setId(id);
		return Result.success(afterSaleReasonService.editAfterSaleReason(afterSaleReason));
	}

	@Operation(summary = "删除售后原因", description = "删除售后原因")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@DeleteMapping(value = "/{id}")
	public Result<Boolean> delAllByIds(@PathVariable String id) {
		afterSaleReasonService.removeById(id);
		return Result.success(true);
	}
}
