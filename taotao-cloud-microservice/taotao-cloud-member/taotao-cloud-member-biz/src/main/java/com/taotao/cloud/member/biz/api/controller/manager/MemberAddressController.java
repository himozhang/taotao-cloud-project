package com.taotao.cloud.member.biz.api.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.common.model.PageModel;
import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.member.api.web.vo.MemberAddressVO;
import com.taotao.cloud.member.biz.model.entity.MemberAddress;
import com.taotao.cloud.member.biz.service.MemberAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理端,会员地址API
 *
 * @since 2020-02-25 14:10:16
 */
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/member/manager/member/address")
@Tag(name = "管理端-会员地址管理API", description = "管理端-会员地址管理API")
public class MemberAddressController {

	private final MemberAddressService memberAddressService;

	@Operation(summary = "会员地址分页列表", description = "会员地址分页列表")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping("/{memberId}")
	public Result<PageModel<MemberAddressVO>> getByPage(@Validated PageParam page,
														@Parameter(description = "会员地址ID", required = true) @PathVariable("memberId") Long memberId) {
		IPage<MemberAddress> addressByMember = memberAddressService.getAddressByMember(page, memberId);
		return Result.success(PageModel.convertMybatisPage(addressByMember, MemberAddressVO.class));
	}

	@Operation(summary = "删除会员收件地址", description = "删除会员收件地址")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@DeleteMapping(value = "/{id}")
	public Result<Boolean> delShippingAddressById(
		@Parameter(description = "会员地址ID", required = true) @PathVariable Long id) {
		return Result.success(memberAddressService.removeMemberAddress(id));
	}

	@Operation(summary = "修改会员收件地址", description = "修改会员收件地址")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@PutMapping
	public Result<Boolean> editShippingAddress(@Valid MemberAddress shippingAddress) {
		return Result.success(memberAddressService.updateMemberAddress(shippingAddress));
	}

	@Operation(summary = "新增会员收件地址", description = "新增会员收件地址")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@PostMapping
	public Result<Boolean> addShippingAddress(@Valid MemberAddress shippingAddress) {
		return Result.success(memberAddressService.saveMemberAddress(shippingAddress));
	}


}
