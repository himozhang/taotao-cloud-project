/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
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
package com.taotao.cloud.sys.biz.api.controller.tools.codegen;

import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.logger.annotation.RequestLogger;
import com.taotao.cloud.sys.biz.model.entity.config.ColumnConfig;
import com.taotao.cloud.sys.biz.service.IGenConfigService;
import com.taotao.cloud.sys.biz.service.IGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GeneratorController
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2022-02-15 08:59:11
 */
@AllArgsConstructor
@Validated
@RestController
@Tag(name = "工具管理端-代码生成管理API", description = "工具管理端-代码生成管理API")
@RequestMapping("/sys/tools/codegen/generator")
public class GeneratorController {

	private final IGeneratorService generatorService;
	private final IGenConfigService genConfigService;

	//@Value("${generator.enabled:false}")
	//private Boolean generatorEnabled = false;

	@Operation(summary = "查询数据库数据", description = "查询数据库数据")
	@RequestLogger("查询数据库数据")
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping(value = "/tables/all")
	public Result<Object> getTables() {
		return Result.success(generatorService.getTables());
	}

	@Operation(summary = "查询数据库数据", description = "查询数据库数据")
	@RequestLogger("查询数据库数据")
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping(value = "/tables")
	public Result<Object> getTables(@RequestParam(defaultValue = "") String name,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "10") Integer size) {
		return Result.success(generatorService.getTables(name, page, size));
	}

	@Operation(summary = "查询字段数据", description = "查询字段数据")
	@RequestLogger("查询字段数据")
	@PreAuthorize("@el.check('admin','timing:list')")
	@GetMapping(value = "/columns")
	public Result<List<ColumnConfig>> getTables(@RequestParam String tableName) {
		List<ColumnConfig> columnInfos = generatorService.getColumns(tableName);
		return Result.success(columnInfos);
		//return new ResponseEntity<>(PageUtil.toPage(columnInfos, columnInfos.size()),
		//	HttpStatus.OK);
	}

	@PutMapping
	@Operation(summary = "保存字段数据", description = "保存字段数据")
	@RequestLogger("保存字段数据")
	@PreAuthorize("@el.check('admin','timing:list')")
	public Result<Boolean> save(@RequestBody List<ColumnConfig> columnInfos) {
		generatorService.save(columnInfos);
		return Result.success(true);
	}

	@Operation(summary = "同步字段数据", description = "同步字段数据")
	@RequestLogger("同步字段数据")
	@PreAuthorize("@el.check('admin','timing:list')")
	@PostMapping(value = "/sync")
	public Result<Boolean> sync(@RequestBody List<String> tables) {
		for (String table : tables) {
			generatorService.sync(generatorService.getColumns(table),
				generatorService.query(table));
		}
		return Result.success(true);
	}

	@Operation(summary = "生成代码", description = "生成代码")
	@RequestLogger("生成代码")
	@PreAuthorize("@el.check('admin','timing:list')")
	@PostMapping(value = "/{tableName}/{type}")
	public Result<Object> generator(@PathVariable String tableName,
		@PathVariable Integer type, HttpServletRequest request, HttpServletResponse response) {
		//if (!generatorEnabled && type == 0) {
		//	throw new BusinessException("此环境不允许生成代码，请选择预览或者下载查看！");
		//}
		switch (type) {
			// 生成代码
			case 0:
				generatorService.generator(genConfigService.find(tableName),
					generatorService.getColumns(tableName));
				break;
			// 预览
			case 1:
				List<Map<String, Object>> preview = generatorService.preview(
					genConfigService.find(tableName),
					generatorService.getColumns(tableName));
				return Result.success(preview);
				// 打包
			case 2:
				generatorService.download(genConfigService.find(tableName),
					generatorService.getColumns(tableName), request, response);
				break;
			default:
				throw new BusinessException("没有这个选项");
		}
		return Result.success(true);
	}
}
