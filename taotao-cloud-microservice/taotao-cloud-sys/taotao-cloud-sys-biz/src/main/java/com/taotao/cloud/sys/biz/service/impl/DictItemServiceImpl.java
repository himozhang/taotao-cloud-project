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
package com.taotao.cloud.sys.biz.service.impl;

import com.taotao.cloud.sys.api.dubbo.IDubboDictItemService;
import com.taotao.cloud.sys.biz.model.entity.dict.DictItem;
import com.taotao.cloud.sys.biz.mapper.IDictItemMapper;
import com.taotao.cloud.sys.biz.repository.cls.DictItemRepository;
import com.taotao.cloud.sys.biz.repository.inf.IDictItemRepository;
import com.taotao.cloud.sys.biz.service.IDictItemService;
import com.taotao.cloud.web.base.service.BaseSuperServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * DictItemServiceImpl
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2021-10-09 20:34:52
 */
@Service
@DubboService(interfaceClass = IDubboDictItemService.class)
public class DictItemServiceImpl extends
	BaseSuperServiceImpl<IDictItemMapper, DictItem, DictItemRepository, IDictItemRepository, Long>
	implements IDubboDictItemService, IDictItemService {

	//private final static QDictItem SYS_DICT_ITEM = QDictItem.sysDictItem;
	//private final static BooleanExpression PREDICATE = SYS_DICT_ITEM.delFlag.eq(false);
	//private final static OrderSpecifier<LocalDateTime> CREATE_TIME_DESC = SYS_DICT_ITEM.createTime.desc();

	//private final DictItemRepository dictItemRepository;
	//
	//public DictItemServiceImpl(
	//	DictItemRepository dictItemRepository) {
	//	this.dictItemRepository = dictItemRepository;
	//}
	//
	//@Override
	//@Transactional(rollbackFor = Exception.class)
	//public Boolean deleteByDictId(Long dictId) {
	//	return dictItemRepository.deleteByDictId(dictId);
	//}
	//
	//@Override
	//@Transactional(rollbackFor = Exception.class)
	//public DictItem save(DictItemDTO dictItemDTO) {
	//	//DictItem item = DictItem.builder().build();
	//	//BeanUtil.copyIgnoredNull(dictItemDTO, item);
	//	//return dictItemRepository.saveAndFlush(item);
	//	return null;
	//}
	//
	//@Override
	//@Transactional(rollbackFor = Exception.class)
	//public DictItem updateById(Long id, DictItemDTO dictItemDTO) {
	//	Optional<DictItem> optionalDictItem = dictItemRepository.findById(id);
	//	DictItem item = optionalDictItem.orElseThrow(() -> new BusinessException("字典项数据不存在"));
	//	BeanUtil.copyIgnoredNull(dictItemDTO, item);
	//	return dictItemRepository.saveAndFlush(item);
	//}
	//
	//@Override
	//@Transactional(rollbackFor = Exception.class)
	//public Boolean deleteById(Long id) {
	//
	//	dictItemRepository.deleteById(id);
	//	return true;
	//}
	//
	//@Override
	//public Page<DictItem> getPage(Pageable page, DictItemPageQuery dictItemPageQuery) {
	//	Optional.ofNullable(dictItemPageQuery.getDictId())
	//		.ifPresent(dictId -> PREDICATE.and(SYS_DICT_ITEM.dictId.eq(dictId)));
	//	Optional.ofNullable(dictItemPageQuery.getItemText())
	//		.ifPresent(itemText -> PREDICATE.and(SYS_DICT_ITEM.itemText.like(itemText)));
	//	Optional.ofNullable(dictItemPageQuery.getItemValue())
	//		.ifPresent(itemValue -> PREDICATE.and(SYS_DICT_ITEM.itemValue.like(itemValue)));
	//	Optional.ofNullable(dictItemPageQuery.getDescription())
	//		.ifPresent(description -> PREDICATE.and(SYS_DICT_ITEM.description.like(description)));
	//	Optional.ofNullable(dictItemPageQuery.getStatus())
	//		.ifPresent(status -> PREDICATE.and(SYS_DICT_ITEM.status.eq(status)));
	//	return dictItemRepository.findPageable(PREDICATE, page, CREATE_TIME_DESC);
	//}
	//
	//@Override
	//public List<DictItem> getInfo(DictItemQuery dictItemQuery) {
	//	Optional.ofNullable(dictItemQuery.getDictId())
	//		.ifPresent(dictId -> PREDICATE.and(SYS_DICT_ITEM.dictId.eq(dictId)));
	//	Optional.ofNullable(dictItemQuery.getItemText())
	//		.ifPresent(itemText -> PREDICATE.and(SYS_DICT_ITEM.itemText.like(itemText)));
	//	Optional.ofNullable(dictItemQuery.getItemValue())
	//		.ifPresent(itemValue -> PREDICATE.and(SYS_DICT_ITEM.itemValue.like(itemValue)));
	//	Optional.ofNullable(dictItemQuery.getDescription())
	//		.ifPresent(description -> PREDICATE.and(SYS_DICT_ITEM.description.like(description)));
	//	Optional.ofNullable(dictItemQuery.getStatus())
	//		.ifPresent(status -> PREDICATE.and(SYS_DICT_ITEM.status.eq(status)));
	//	return dictItemRepository.getInfo(PREDICATE);
	//}
}
