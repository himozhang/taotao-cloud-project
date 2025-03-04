package com.taotao.cloud.member.biz.api.controller.buyer;

import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.goods.api.web.vo.EsGoodsIndexVO;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.member.biz.service.IMemberBrowseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * 买家端-浏览历史API
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2022-03-11 15:52:08
 */
@AllArgsConstructor
@Validated
@RestController
@Tag(name = "买家端-会员浏览历史API", description = "买家端-会员浏览历史API")
@RequestMapping("/member/buyer/member/browse")
public class MemberBrowseController {

	private final IMemberBrowseService memberBrowseService;

	@Operation(summary = "分页获取浏览历史", description = "分页获取浏览历史")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping
	public Result<List<EsGoodsIndexVO>> getByPage(PageParam pageParam) {
		return Result.success(memberBrowseService.footPrintPage(pageParam));
	}

	@Operation(summary = "根据id删除浏览历史", description = "根据id删除浏览历史")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@DeleteMapping(value = "/{ids}")
	public Result<Boolean> delAllByIds(
		@Parameter(description = "会员地址ID", required = true)
		@NotEmpty(message = "商品ID不能为空") @PathVariable("ids") List<Long> ids) {
		return Result.success(memberBrowseService.deleteByIds(ids));
	}

	@Operation(summary = "清空足迹", description = "清空足迹")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@DeleteMapping
	public Result<Boolean> deleteAll() {
		return Result.success(memberBrowseService.clean());
	}

	@Operation(summary = "获取当前会员足迹数量", description = "获取当前会员足迹数量")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping(value = "/current/foot/count")
	public Result<Long> getFootprintNum() {
		return Result.success(memberBrowseService.getFootprintNum());
	}

}
