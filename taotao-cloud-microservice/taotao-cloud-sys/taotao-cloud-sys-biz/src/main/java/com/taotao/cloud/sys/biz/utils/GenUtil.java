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
package com.taotao.cloud.sys.biz.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateException;
import cn.hutool.extra.template.TemplateUtil;
import com.taotao.cloud.common.utils.lang.StringUtil;
import com.taotao.cloud.sys.biz.model.entity.config.ColumnConfig;
import com.taotao.cloud.sys.biz.model.entity.config.GenConfig;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ObjectUtils;

/**
 * 代码生成
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2022-02-15 09:11:14
 */
public class GenUtil {

	private static final String TIMESTAMP = "Timestamp";

	private static final String BIGDECIMAL = "BigDecimal";

	public static final String PK = "PRI";

	public static final String EXTRA = "auto_increment";

	/**
	 * 获取后端代码模板名称
	 *
	 * @return List
	 */
	private static List<String> getAdminTemplateNames() {
		List<String> templateNames = new ArrayList<>();
        /*templateNames.add("Entity");
        templateNames.add("Dto");
        templateNames.add("Mapper");
        templateNames.add("Controller");
        templateNames.add("QueryCriteria");
        templateNames.add("Service");
        templateNames.add("ServiceImpl");
        templateNames.add("Repository");*/
		templateNames.add("EntityP");
		templateNames.add("DtoP");
		templateNames.add("MapperP");
		templateNames.add("ControllerP");
		templateNames.add("QueryCriteriaP");
		templateNames.add("ServiceP");
		templateNames.add("ServiceImplP");
		//templateNames.add("Repository");
		return templateNames;
	}

	/**
	 * 获取前端代码模板名称
	 *
	 * @return List
	 */
	private static List<String> getFrontTemplateNames() {
		List<String> templateNames = new ArrayList<>();
		templateNames.add("index");
		templateNames.add("api");
		return templateNames;
	}

	public static List<Map<String, Object>> preview(List<ColumnConfig> columns,
		GenConfig genConfig) {
		Map<String, Object> genMap = getGenMap(columns, genConfig);
		List<Map<String, Object>> genList = new ArrayList<>();
		// 获取后端模版
		List<String> templates = getAdminTemplateNames();
		TemplateEngine engine = TemplateUtil.createEngine(
			new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
		for (String templateName : templates) {
			Map<String, Object> map = new HashMap<>(1);
			Template template = engine.getTemplate("generator/admin/" + templateName + ".ftl");
			map.put("content", template.render(genMap));
			map.put("name", templateName);
			genList.add(map);
		}
		// 获取前端模版
		templates = getFrontTemplateNames();
		for (String templateName : templates) {
			Map<String, Object> map = new HashMap<>(1);
			Template template = engine.getTemplate("generator/front/" + templateName + ".ftl");
			map.put(templateName, template.render(genMap));
			map.put("content", template.render(genMap));
			map.put("name", templateName);
			genList.add(map);
		}
		return genList;
	}

	public static String download(List<ColumnConfig> columns, GenConfig genConfig)
		throws IOException {
		String tempPath = System.getProperty("java.io.tmpdir") + "yshop-gen-temp" + File.separator
			+ genConfig.getTableName() + File.separator;
		Map<String, Object> genMap = getGenMap(columns, genConfig);
		TemplateEngine engine = TemplateUtil.createEngine(
			new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
		// 生成后端代码
		List<String> templates = getAdminTemplateNames();
		for (String templateName : templates) {
			Template template = engine.getTemplate("generator/admin/" + templateName + ".ftl");
			String filePath = getAdminFilePath(templateName, genConfig,
				genMap.get("className").toString(), tempPath + "yshop" + File.separator);
			assert filePath != null;
			File file = new File(filePath);
			// 如果非覆盖生成
			if (!genConfig.getCover() && FileUtil.exist(file)) {
				continue;
			}
			// 生成代码
			genFile(file, template, genMap);
		}
		// 生成前端代码
		templates = getFrontTemplateNames();
		for (String templateName : templates) {
			Template template = engine.getTemplate("generator/front/" + templateName + ".ftl");
			String path = tempPath + "yshop-web" + File.separator;
			String apiPath = path + "src" + File.separator + "api" + File.separator;
			String srcPath = path + "src" + File.separator + "views" + File.separator + genMap.get(
				"changeClassName").toString() + File.separator;
			String filePath = getFrontFilePath(templateName, apiPath, srcPath,
				genMap.get("changeClassName").toString());
			assert filePath != null;
			File file = new File(filePath);
			// 如果非覆盖生成
			if (!genConfig.getCover() && FileUtil.exist(file)) {
				continue;
			}
			// 生成代码
			genFile(file, template, genMap);
		}
		return tempPath;
	}

	public static void generatorCode(List<ColumnConfig> columnInfos, GenConfig genConfig)
		throws IOException {
		Map<String, Object> genMap = getGenMap(columnInfos, genConfig);
		TemplateEngine engine = TemplateUtil.createEngine(
			new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
		// 生成后端代码
		List<String> templates = getAdminTemplateNames();
		for (String templateName : templates) {
			Template template = engine.getTemplate("generator/admin/" + templateName + ".ftl");
			String filePath = getAdminFilePath(templateName, genConfig,
				genMap.get("className").toString(), System.getProperty("user.dir"));

			assert filePath != null;
			File file = new File(filePath);

			// 如果非覆盖生成
			if (!genConfig.getCover() && FileUtil.exist(file)) {
				continue;
			}
			// 生成代码
			genFile(file, template, genMap);
		}

		// 生成前端代码
		templates = getFrontTemplateNames();
		for (String templateName : templates) {
			Template template = engine.getTemplate("generator/front/" + templateName + ".ftl");
			String filePath = getFrontFilePath(templateName, genConfig.getApiPath(),
				genConfig.getPath(), genMap.get("changeClassName").toString());

			assert filePath != null;
			File file = new File(filePath);

			// 如果非覆盖生成
			if (!genConfig.getCover() && FileUtil.exist(file)) {
				continue;
			}
			// 生成代码
			genFile(file, template, genMap);
		}
	}

	// 获取模版数据
	private static Map<String, Object> getGenMap(List<ColumnConfig> columnInfos,
		GenConfig genConfig) {
		// 存储模版字段数据
		Map<String, Object> genMap = new HashMap<>(16);
		// 接口别名
		genMap.put("apiAlias", genConfig.getApiAlias());
		// 包名称
		genMap.put("package", genConfig.getPack());
		// 模块名称
		genMap.put("moduleName", genConfig.getModuleName());
		// 作者
		genMap.put("author", genConfig.getAuthor());
		// 创建日期
		genMap.put("date", LocalDate.now().toString());
		// 表名
		genMap.put("tableName", genConfig.getTableName());
		// 大写开头的类名
		String className = StringUtil.toCapitalizeCamelCase(genConfig.getTableName());
		// 小写开头的类名
		String changeClassName = StringUtil.toCamelCase(genConfig.getTableName());
		// 判断是否去除表前缀
		if (StringUtil.isNotBlank(genConfig.getPrefix())) {
			className = StringUtil.toCapitalizeCamelCase(
				StrUtil.removePrefix(genConfig.getTableName(), genConfig.getPrefix()));
			changeClassName = StringUtil.toCamelCase(
				StrUtil.removePrefix(genConfig.getTableName(), genConfig.getPrefix()));
		}
		// 保存类名
		genMap.put("className", className);
		// 保存小写开头的类名
		genMap.put("changeClassName", changeClassName);
		// 存在 Timestamp 字段
		genMap.put("hasTimestamp", false);
		// 存在 Images 字段
		genMap.put("hasImages", false);
		// 查询类中存在 Timestamp 字段
		genMap.put("queryHasTimestamp", false);
		// 存在 BigDecimal 字段
		genMap.put("hasBigDecimal", false);
		// 查询类中存在 BigDecimal 字段
		genMap.put("queryHasBigDecimal", false);
		// 是否需要创建查询
		genMap.put("hasQuery", false);
		// 自增主键
		genMap.put("auto", false);
		// 存在字典
		genMap.put("hasDict", false);
		// 存在日期注解
		genMap.put("hasDateAnnotation", false);
		// 保存字段信息
		List<Map<String, Object>> columns = new ArrayList<>();
		// 保存查询字段的信息
		List<Map<String, Object>> queryColumns = new ArrayList<>();
		// 存储字典信息
		List<String> dicts = new ArrayList<>();
		// 存储 between 信息
		List<Map<String, Object>> betweens = new ArrayList<>();
		// 存储不为空的字段信息
		List<Map<String, Object>> isNotNullColumns = new ArrayList<>();

		for (ColumnConfig column : columnInfos) {
			Map<String, Object> listMap = new HashMap<>(16);
			// 字段描述
			listMap.put("remark", column.getRemark());
			// 字段类型
			listMap.put("columnKey", column.getKeyType());
			// 主键类型
			String colType = ColUtil.cloToJava(column.getColumnType());
			// 小写开头的字段名
			String changeColumnName = StringUtil.toCamelCase(column.getColumnName().toString());
			// 大写开头的字段名
			String capitalColumnName = StringUtil.toCapitalizeCamelCase(
				column.getColumnName().toString());
			if (PK.equals(column.getKeyType())) {
				// 存储主键类型
				genMap.put("pkColumnType", colType);
				// 存储小写开头的字段名
				genMap.put("pkChangeColName", changeColumnName);
				// 存储大写开头的字段名
				genMap.put("pkCapitalColName", capitalColumnName);
			}
			if ("Images".equals(column.getFormType())) {
				// 存在 Images 字段
				genMap.put("hasImages", true);
			}
			// 是否存在 Timestamp 类型的字段
			if (TIMESTAMP.equals(colType)) {
				genMap.put("hasTimestamp", true);
			}
			// 是否存在 BigDecimal 类型的字段
			if (BIGDECIMAL.equals(colType)) {
				genMap.put("hasBigDecimal", true);
			}
			// 主键是否自增
			if (EXTRA.equals(column.getExtra())) {
				genMap.put("auto", true);
			}
			// 主键存在字典
			if (StringUtil.isNotBlank(column.getDictName())) {
				genMap.put("hasDict", true);
				dicts.add(column.getDictName());
			}

			// 存储字段类型
			listMap.put("columnType", colType);
			// 存储字原始段名称
			listMap.put("columnName", column.getColumnName());
			// 不为空
			listMap.put("istNotNull", column.getNotNull());
			// 字段列表显示
			listMap.put("columnShow", column.getListShow());
			// 表单显示
			listMap.put("formShow", column.getFormShow());
			// 表单组件类型
			listMap.put("formType",
				StringUtil.isNotBlank(column.getFormType()) ? column.getFormType() : "Input");
			// 小写开头的字段名称
			listMap.put("changeColumnName", changeColumnName);
			//大写开头的字段名称
			listMap.put("capitalColumnName", capitalColumnName);
			// 字典名称
			listMap.put("dictName", column.getDictName());
			// 日期注解
			listMap.put("dateAnnotation", column.getDateAnnotation());
			if (StringUtil.isNotBlank(column.getDateAnnotation())) {
				genMap.put("hasDateAnnotation", true);
			}
			// 添加非空字段信息
			if (column.getNotNull()) {
				isNotNullColumns.add(listMap);
			}
			// 判断是否有查询，如有则把查询的字段set进columnQuery
			if (!StringUtil.isBlank(column.getQueryType())) {
				// 查询类型
				listMap.put("queryType", column.getQueryType());
				// 是否存在查询
				genMap.put("hasQuery", true);
				if (TIMESTAMP.equals(colType)) {
					// 查询中存储 Timestamp 类型
					genMap.put("queryHasTimestamp", true);
				}
				if (BIGDECIMAL.equals(colType)) {
					// 查询中存储 BigDecimal 类型
					genMap.put("queryHasBigDecimal", true);
				}
				if ("between".equalsIgnoreCase(column.getQueryType())) {
					betweens.add(listMap);
				} else {
					// 添加到查询列表中
					queryColumns.add(listMap);
				}
			}
			// 添加到字段列表中
			columns.add(listMap);
		}
		// 保存字段列表
		genMap.put("columns", columns);
		// 保存查询列表
		genMap.put("queryColumns", queryColumns);
		// 保存字段列表
		genMap.put("dicts", dicts);
		// 保存查询列表
		genMap.put("betweens", betweens);
		// 保存非空字段信息
		genMap.put("isNotNullColumns", isNotNullColumns);
		return genMap;
	}

	/**
	 * 定义后端文件路径以及名称
	 */
	private static String getAdminFilePath(String templateName, GenConfig genConfig,
		String className, String rootPath) {
		String projectPath = rootPath + File.separator + genConfig.getModuleName();
		String packagePath =
			projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java"
				+ File.separator;
		if (!ObjectUtils.isEmpty(genConfig.getPack())) {
			packagePath += genConfig.getPack().replace(".", File.separator) + File.separator;
		}

		if ("Entity".equals(templateName)) {
			return packagePath + "domain" + File.separator + className + ".java";
		}
		if ("EntityP".equals(templateName)) {
			return packagePath + "domain" + File.separator + className + ".java";
		}
		if ("Controller".equals(templateName)) {
			return packagePath + "rest" + File.separator + className + "Controller.java";
		}
		if ("ControllerP".equals(templateName)) {
			return packagePath + "rest" + File.separator + className + "Controller.java";
		}
		if ("Service".equals(templateName)) {
			return packagePath + "service" + File.separator + className + "Service.java";
		}
		if ("ServiceP".equals(templateName)) {
			return packagePath + "service" + File.separator + className + "Service.java";
		}

		if ("ServiceImpl".equals(templateName)) {
			return packagePath + "service" + File.separator + "impl" + File.separator + className
				+ "ServiceImpl.java";
		}
		if ("ServiceImplP".equals(templateName)) {
			return packagePath + "service" + File.separator + "impl" + File.separator + className
				+ "ServiceImpl.java";
		}

		if ("Dto".equals(templateName)) {
			return packagePath + "service" + File.separator + "dto" + File.separator + className
				+ "Dto.java";
		}
		if ("DtoP".equals(templateName)) {
			return packagePath + "service" + File.separator + "dto" + File.separator + className
				+ "Dto.java";
		}

		if ("QueryCriteria".equals(templateName)) {
			return packagePath + "service" + File.separator + "dto" + File.separator + className
				+ "QueryCriteria.java";
		}
		if ("QueryCriteriaP".equals(templateName)) {
			return packagePath + "service" + File.separator + "dto" + File.separator + className
				+ "QueryCriteria.java";
		}

		if ("Mapper".equals(templateName)) {
			return packagePath + "service" + File.separator + "mapper" + File.separator + className
				+ "Mapper.java";
		}
		if ("MapperP".equals(templateName)) {
			return packagePath + "service" + File.separator + "mapper" + File.separator + className
				+ "Mapper.java";
		}

		if ("Repository".equals(templateName)) {
			return packagePath + "repository" + File.separator + className + "Repository.java";
		}

		return null;
	}

	/**
	 * 定义前端文件路径以及名称
	 */
	private static String getFrontFilePath(String templateName, String apiPath, String path,
		String apiName) {

		if ("api".equals(templateName)) {
			return apiPath + File.separator + apiName + ".js";
		}

		if ("index".equals(templateName)) {
			return path + File.separator + "index.vue";
		}

		return null;
	}

	private static void genFile(File file, Template template, Map<String, Object> map)
		throws IOException {
		// 生成目标文件
		Writer writer = null;
		try {
			FileUtil.touch(file);
			writer = new FileWriter(file);
			template.render(map, writer);
		} catch (TemplateException | IOException e) {
			throw new RuntimeException(e);
		} finally {
			assert writer != null;
			writer.close();
		}
	}
}
