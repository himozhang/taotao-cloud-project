package com.taotao.cloud.goods.biz.api.controller.seller;

import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.goods.api.web.vo.SpecificationVO;
import com.taotao.cloud.goods.biz.model.entity.Specification;
import com.taotao.cloud.goods.biz.mapstruct.ISpecificationMapStruct;
import com.taotao.cloud.goods.biz.service.ICategorySpecificationService;
import com.taotao.cloud.logger.annotation.RequestLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 店铺端,商品分类规格接口
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-20 16:59:38
 */
@AllArgsConstructor
@Validated
@RestController
@Tag(name = "店铺端-商品分类规格API", description = "店铺端-商品分类规格API")
@RequestMapping("/goods/seller/category/spec")
public class CategorySpecificationStoreController {

	/**
	 * 商品规格服务
	 */
	private final ICategorySpecificationService categorySpecificationService;

	@Operation(summary = "查询某分类下绑定的规格信息", description = "查询某分类下绑定的规格信息")
	@RequestLogger("查询某分类下绑定的规格信息")
	@PreAuthorize("hasAuthority('dept:tree:data')")
	@GetMapping(value = "/{categoryId}")
	public Result<List<SpecificationVO>> getCategorySpec(
		@PathVariable("categoryId") Long categoryId) {
		List<Specification> categorySpecList = categorySpecificationService.getCategorySpecList(
			categoryId);

		return Result.success(
			ISpecificationMapStruct.INSTANCE.specificationsToSpecificationVOs(categorySpecList));
	}


}
