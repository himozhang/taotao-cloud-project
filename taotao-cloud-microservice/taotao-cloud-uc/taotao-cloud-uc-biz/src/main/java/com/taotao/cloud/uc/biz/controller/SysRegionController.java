/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.uc.biz.controller;

import com.taotao.cloud.uc.api.dto.region.RegionQueryDTO;
import com.taotao.cloud.uc.api.dto.region.RegionSaveDTO;
import com.taotao.cloud.uc.api.dto.region.RegionUpdateDTO;
import com.taotao.cloud.uc.api.service.ISysRegionService;
import com.taotao.cloud.uc.api.vo.region.RegionQueryVO;
import com.taotao.cloud.uc.biz.entity.SysRegion;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.taotao.cloud.web.base.controller.SuperController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地区管理API
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-10-09 15:01:59
 */
@RestController
@RequestMapping("/uc/region")
@Tag(name = "地区管理API", description = "地区管理API")
public class SysRegionController extends
	SuperController<ISysRegionService<SysRegion, Long>, SysRegion, Long, RegionQueryDTO, RegionSaveDTO, RegionUpdateDTO, RegionQueryVO> {


	//private final SysRegionService sysRegionService;
	//
	//public SysRegionController(SysRegionService sysRegionService) {
	//	this.sysRegionService = sysRegionService;
	//}
	//
	///**
	// * 根据父id查询
	// *
	// * @param parentId 父id
	// * @return {@link Result&lt;java.util.List&lt;com.taotao.cloud.uc.api.vo.QueryRegionByParentIdVO&gt;&gt;
	// * }
	// * @author shuigedeng
	// * @since 2021-10-09 15:02:04
	// */
	//@Operation(summary = "根据父id查询", description = "根据父id查询", method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据父id查询")
	//@GetMapping("/parentId")
	//public Result<List<QueryRegionByParentIdVO>> queryRegionByParentId(
	//	@Parameter(name = "parentId", description = "父id", required = true, in = ParameterIn.QUERY)
	//	@RequestParam(value = "parentId", defaultValue = "1") Long parentId) {
	//	List<QueryRegionByParentIdVO> result = sysRegionService.queryRegionByParentId(parentId);
	//	return Result.success(result);
	//}
	//
	///**
	// * 树形结构查询
	// *
	// * @return {@link Result&lt;java.util.List&lt;com.taotao.cloud.uc.api.vo.QueryRegionByParentIdVO&gt;&gt;
	// * }
	// * @author shuigedeng
	// * @since 2021-10-09 15:02:13
	// */
	//@Operation(summary = "树形结构查询", description = "树形结构查询", method = CommonConstant.GET, security = @SecurityRequirement(name = HttpHeaders.AUTHORIZATION))
	//@RequestOperateLog(description = "根据父id查询")
	//@GetMapping(value = "/tree")
	//public Result<List<QueryRegionByParentIdVO>> tree() {
	//	List<QueryRegionByParentIdVO> result = sysRegionService.tree();
	//	return Result.success(result);
	//}
}
