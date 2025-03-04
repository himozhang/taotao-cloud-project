package com.taotao.cloud.goods.biz.api.controller.manager;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.common.model.PageModel;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.goods.api.web.dto.BrandDTO;
import com.taotao.cloud.goods.api.web.query.BrandPageQuery;
import com.taotao.cloud.goods.api.web.vo.BrandVO;
import com.taotao.cloud.goods.biz.model.entity.Brand;
import com.taotao.cloud.goods.biz.mapstruct.IBrandMapStruct;
import com.taotao.cloud.goods.biz.service.IBrandService;
import com.taotao.cloud.logger.annotation.RequestLogger;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 管理端,品牌接口
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-25 16:16:20
 */
@AllArgsConstructor
@Validated
@RestController
@Tag(name = "平台管理端-品牌管理API", description = "平台管理端-品牌管理API")
@RequestMapping("/goods/manager/brand")
public class BrandManagerController {

	/**
	 * 品牌
	 */
	private final IBrandService brandService;

	@Operation(summary = "通过id获取", description = "通过id获取")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/{id}")
	public Result<BrandVO> getById(@NotBlank(message = "id不能为空") @PathVariable Long id) {
		Brand brand = brandService.getById(id);
		return Result.success(IBrandMapStruct.INSTANCE.brandToBrandVO(brand));
	}

	@Operation(summary = "获取所有可用品牌", description = "获取所有可用品牌")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/all/available")
	public Result<List<BrandVO>> getAllAvailable() {
		List<Brand> list = brandService.getAllAvailable();
		return Result.success(IBrandMapStruct.INSTANCE.brandsToBrandVOs(list));
	}

	@Operation(summary = "分页获取", description = "分页获取")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/page")
	public Result<PageModel<BrandVO>> page(@Validated BrandPageQuery page) {
		IPage<Brand> brandPage = brandService.getBrandsByPage(page);
		return Result.success(PageModel.convertMybatisPage(brandPage, BrandVO.class));
	}

	@Operation(summary = "新增品牌", description = "新增品牌")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PostMapping
	public Result<Boolean> save(@Validated @RequestBody BrandDTO brand) {
		return Result.success(brandService.addBrand(brand));
	}

	@Operation(summary = "更新品牌", description = "更新品牌")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PutMapping("/{id}")
	public Result<Boolean> update(@PathVariable Long id, @Validated BrandDTO brand) {
		brand.setId(id);
		return Result.success(brandService.updateBrand(brand));
	}

	@Operation(summary = "后台禁用品牌", description = "后台禁用品牌")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@PutMapping(value = "/disable/{brandId}")
	public Result<Boolean> disable(@PathVariable Long brandId, @RequestParam Boolean disable) {
			return Result.success(brandService.brandDisable(brandId, disable));
	}

	@Operation(summary = "批量删除", description = "批量删除")
	@RequestLogger
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@DeleteMapping(value = "/{ids}")
	public Result<Boolean> delAllByIds(@PathVariable List<Long> ids) {
		return Result.success(brandService.deleteBrands(ids));
	}

}
