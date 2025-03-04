package com.taotao.cloud.member.biz.api.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.PageModel;
import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.member.api.web.vo.MemberGradeVO;
import com.taotao.cloud.member.biz.model.entity.MemberGrade;
import com.taotao.cloud.member.biz.mapstruct.IMemberGradeMapStruct;
import com.taotao.cloud.member.biz.service.MemberGradeService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端,会员等级API
 *
 * @since 2021/5/16 11:29 下午
 */
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/member/manager/member/grade")
@Tag(name = "管理端-会员等级管理API", description = "管理端-会员等级管理API")
public class MemberGradeController {

	private final MemberGradeService memberGradeService;

	@Operation(summary = "通过id获取会员等级", description = "通过id获取会员等级")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping(value = "/{id}")
	public Result<MemberGradeVO> get(@PathVariable Long id) {
		MemberGrade memberGrade = memberGradeService.getById(id);
		return Result.success(IMemberGradeMapStruct.INSTANCE.memberGradeToMemberGradeVO(memberGrade));
	}

	@Operation(summary = "获取会员等级分页", description = "获取会员等级分页")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping(value = "/page")
	public Result<PageModel<MemberGradeVO>> getByPage(PageParam pageParam) {
		IPage<MemberGrade> memberGradePage = memberGradeService.getByPage(pageParam);
		return Result.success(PageModel.convertMybatisPage(memberGradePage, MemberGradeVO.class));
	}

	@Operation(summary = "添加会员等级", description = "添加会员等级")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@PostMapping
	public Result<Boolean> daa(@Validated MemberGrade memberGrade) {
		return Result.success(memberGradeService.save(memberGrade));
	}

	@Operation(summary = "修改会员等级", description = "修改会员等级")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping
	@PutMapping(value = "/{id}")
	public Result<Boolean> update(@PathVariable Long id, MemberGrade memberGrade) {
		return Result.success(memberGradeService.updateById(memberGrade));
	}

	@Operation(summary = "删除会员等级", description = "删除会员等级")
	@RequestLogger
	@PreAuthorize("@el.check('admin','timing:list')")
	@DeleteMapping(value = "/{id}")
	public Result<Boolean> delete(@PathVariable Long id) {
		if (memberGradeService.getById(id).getDefaulted()) {
			throw new BusinessException(ResultEnum.USER_GRADE_IS_DEFAULT);
		}
		return Result.success(memberGradeService.removeById(id));
	}
}
