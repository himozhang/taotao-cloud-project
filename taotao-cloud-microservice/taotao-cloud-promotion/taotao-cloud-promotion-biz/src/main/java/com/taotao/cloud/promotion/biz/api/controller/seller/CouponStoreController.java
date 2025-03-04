package com.taotao.cloud.promotion.biz.api.controller.seller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.common.utils.common.OperationalJudgment;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.promotion.api.web.query.CouponPageQuery;
import com.taotao.cloud.promotion.api.web.vo.CouponVO;
import com.taotao.cloud.promotion.biz.model.entity.Coupon;
import com.taotao.cloud.promotion.biz.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.shardingsphere.distsql.parser.autogen.CommonDistSQLStatementParser.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺端,优惠券接口
 *
 * @since 2020/8/28
 */
@RestController
@Tag(name = "店铺端,优惠券接口")
@RequestMapping("/store/promotion/coupon")
public class CouponStoreController {

	@Autowired
	private CouponService couponService;

	@RequestLogger
	@PreAuthorize("hasAuthority('sys:resource:info:roleId')")
	@GetMapping
	@Operation(summary = "获取优惠券列表")
	public Result<IPage<CouponVO>> getCouponList(CouponPageQuery queryParam, PageVO page) {
		page.setNotConvert(true);
		String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
		queryParam.setStoreId(storeId);
		IPage<CouponVO> coupons = couponService.pageVOFindAll(queryParam, page);
		return Result.success(coupons);
	}

	@RequestLogger
	@PreAuthorize("hasAuthority('sys:resource:info:roleId')")
	@Operation(summary = "获取优惠券详情")
	@GetMapping("/{couponId}")
	public Result<Coupon> getCouponList(@PathVariable String couponId) {
		CouponVO coupon = OperationalJudgment.judgment(couponService.getDetail(couponId));
		return Result.success(coupon);
	}

	@RequestLogger
	@PreAuthorize("hasAuthority('sys:resource:info:roleId')")
	@Operation(summary = "添加优惠券")
	@PostMapping(consumes = "application/json", produces = "application/json")
	public Result<CouponVO> addCoupon(@RequestBody CouponVO couponVO) {
		AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
		couponVO.setStoreId(currentUser.getStoreId());
		couponVO.setStoreName(currentUser.getStoreName());
		if (couponService.savePromotions(couponVO)) {
			return Result.success(couponVO);
		}
		return Result.error(ResultEnum.COUPON_SAVE_ERROR);
	}

	@RequestLogger
	@PreAuthorize("hasAuthority('sys:resource:info:roleId')")
	@PutMapping(consumes = "application/json", produces = "application/json")
	@Operation(summary = "修改优惠券")
	public Result<Coupon> updateCoupon(@RequestBody CouponVO couponVO) {
		OperationalJudgment.judgment(couponService.getById(couponVO.getId()));
		AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
		couponVO.setStoreId(currentUser.getStoreId());
		couponVO.setStoreName(currentUser.getStoreName());
		if (couponService.updatePromotions(couponVO)) {
			return Result.success(couponVO);
		}
		return Result.error(ResultEnum.COUPON_SAVE_ERROR);
	}

	@RequestLogger
	@PreAuthorize("hasAuthority('sys:resource:info:roleId')")
	@DeleteMapping(value = "/{ids}")
	@Operation(summary = "批量删除")
	public Result<Object> delAllByIds(@PathVariable List<String> ids) {
		String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
		LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(Coupon::getId, ids);
		queryWrapper.eq(Coupon::getStoreId, storeId);
		List<Coupon> list = couponService.list(queryWrapper);
		List<String> filterIds = list.stream().map(Coupon::getId).collect(Collectors.toList());
		return couponService.removePromotions(filterIds) ? Result.success()
			: Result.error(ResultEnum.COUPON_DELETE_ERROR);
	}

	@RequestLogger
	@PreAuthorize("hasAuthority('sys:resource:info:roleId')")
	@Operation(summary = "修改优惠券状态")
	@PutMapping("/status")
	public Result<Object> updateCouponStatus(String couponIds, Long startTime, Long endTime) {
		AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
		String[] split = couponIds.split(",");
		List<String> couponIdList = couponService.list(
				new LambdaQueryWrapper<Coupon>().in(Coupon::getId, Arrays.asList(split))
					.eq(Coupon::getStoreId, currentUser.getStoreId())).stream().map(Coupon::getId)
			.collect(Collectors.toList());
		if (couponService.updateStatus(couponIdList, startTime, endTime)) {
			return Result.success(ResultEnum.COUPON_EDIT_STATUS_SUCCESS);
		}
		throw new BusinessException(ResultEnum.COUPON_EDIT_STATUS_ERROR);
	}
}
