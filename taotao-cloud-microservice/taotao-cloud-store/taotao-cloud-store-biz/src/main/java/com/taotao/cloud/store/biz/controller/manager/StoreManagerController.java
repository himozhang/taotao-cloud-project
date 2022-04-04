package com.taotao.cloud.store.biz.controller.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.taotao.cloud.store.biz.service.StoreDetailService;
import com.taotao.cloud.store.biz.service.StoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端,店铺管理接口
 *
 * 
 * @since 2020/12/6 16:09
 */
@Api(tags = "管理端,店铺管理接口")
@RestController
@RequestMapping("/manager/store")
public class StoreManagerController {

    /**
     * 店铺
     */
    @Autowired
    private StoreService storeService;
    /**
     * 店铺详情
     */
    @Autowired
    private StoreDetailService storeDetailService;

    @ApiOperation(value = "获取店铺分页列表")
    @GetMapping("/all")
    public Result<List<Store>> getAll() {
        return Result.success(storeService.list(new QueryWrapper<Store>().eq("store_disable", "OPEN")));
    }

    @ApiOperation(value = "获取店铺分页列表")
    @GetMapping
    public Result<IPage<StoreVO>> getByPage(StoreSearchParams entity, PageVO page) {
        return Result.success(storeService.findByConditionPage(entity, page));
    }

    @ApiOperation(value = "获取店铺详情")
    @ApiImplicitParam(name = "storeId", value = "店铺ID", required = true, paramType = "path", dataType = "String")
    @GetMapping(value = "/get/detail/{storeId}")
    public Result<StoreDetailVO> detail(@PathVariable String storeId) {
        return Result.success(storeDetailService.getStoreDetailVO(storeId));
    }

    @ApiOperation(value = "添加店铺")
    @PostMapping(value = "/add")
    public Result<Store> add(@Valid AdminStoreApplyDTO adminStoreApplyDTO) {
        return Result.success(storeService.add(adminStoreApplyDTO));
    }

    @ApiOperation(value = "编辑店铺")
    @ApiImplicitParam(name = "storeId", value = "店铺ID", required = true, paramType = "path", dataType = "String")
    @PutMapping(value = "/edit/{id}")
    public Result<Store> edit(@PathVariable String id, @Valid StoreEditDTO storeEditDTO) {
        storeEditDTO.setStoreId(id);
        return Result.success(storeService.edit(storeEditDTO));
    }

    @ApiOperation(value = "审核店铺申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "passed", value = "是否通过审核 0 通过 1 拒绝 编辑操作则不需传递", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "id", value = "店铺id", required = true, paramType = "path", dataType = "String")
    })
    @PutMapping(value = "/audit/{id}/{passed}")
    public Result<Object> audit(@PathVariable String id, @PathVariable Integer passed) {
        storeService.audit(id, passed);
        return ResultUtil.success();
    }


    @DemoSite
    @ApiOperation(value = "关闭店铺")
    @ApiImplicitParam(name = "id", value = "店铺id", required = true, dataType = "String", paramType = "path")
    @PutMapping(value = "/disable/{id}")
    public Result<Store> disable(@PathVariable String id) {
        storeService.disable(id);
        return ResultUtil.success();
    }

    @ApiOperation(value = "开启店铺")
    @ApiImplicitParam(name = "id", value = "店铺id", required = true, dataType = "String", paramType = "path")
    @PutMapping(value = "/enable/{id}")
    public Result<Store> enable(@PathVariable String id) {
        storeService.enable(id);
        return ResultUtil.success();
    }

    @ApiOperation(value = "查询一级分类列表")
    @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/managementCategory/{storeId}")
    public Result<List<StoreManagementCategoryVO>> firstCategory(@PathVariable String storeId) {
        return Result.success(this.storeDetailService.goodsManagementCategory(storeId));
    }


    @ApiOperation(value = "根据会员id查询店铺信息")
    @GetMapping("/{memberId}/member")
    public Result<Store> getByMemberId(@Valid @PathVariable String memberId) {
        List<Store> list = storeService.list(new QueryWrapper<Store>().eq("member_id", memberId));
        if (list.size() > 0) {
            return Result.success(list.get(0));
        }
        return Result.success(null);
    }
}
