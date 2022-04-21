/*
 * Copyright 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
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
package com.taotao.cloud.sys.biz.service;


import com.taotao.cloud.sys.biz.entity.dict.DictItem;
import com.taotao.cloud.web.base.service.BaseSuperService;

/**
 * ISysDictItemService
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2021-10-09 20:32:47
 */
public interface IDictItemService extends BaseSuperService<DictItem, Long> {

	///**
	// * 根据字典id删除字典项
	// *
	// * @param dictId dictId
	// * @return {@link Boolean }
	// * @since 2021-10-09 20:33:32
	// */
	//Boolean deleteByDictId(Long dictId);
	//
	///**
	// * 添加字典项详情
	// *
	// * @param dictItemDTO dictItemDTO
	// * @return {@link SysDictItem }
	// * @since 2021-10-09 20:33:40
	// */
	//SysDictItem save(DictItemDTO dictItemDTO);
	//
	///**
	// * 更新字典项详情
	// *
	// * @param id          id
	// * @param dictItemDTO dictItemDTO
	// * @return {@link SysDictItem }
	// * @since 2021-10-09 20:33:47
	// */
	//SysDictItem updateById(Long id, DictItemDTO dictItemDTO);
	//
	///**
	// * 根据id删除字典项详情
	// *
	// * @param id id
	// * @return {@link Boolean }
	// * @since 2021-10-09 20:33:54
	// */
	//Boolean deleteById(Long id);
	//
	///**
	// * 分页查询字典详情内容
	// *
	// * @param page              page
	// * @param dictItemPageQuery dictItemPageQuery
	// * @return {@link Page&lt;com.taotao.cloud.sys.biz.entity.SysDictItem&gt; }
	// * @since 2021-10-09 20:34:02
	// */
	//Page<SysDictItem> getPage(Pageable page, DictItemPageQuery dictItemPageQuery);
	//
	///**
	// * 查询字典详情内容
	// *
	// * @param dictItemQuery dictItemQuery
	// * @return {@link List&lt;com.taotao.cloud.sys.biz.entity.SysDictItem&gt; }
	// * @since 2021-10-09 20:34:10
	// */
	//List<SysDictItem> getInfo(DictItemQuery dictItemQuery);
}
