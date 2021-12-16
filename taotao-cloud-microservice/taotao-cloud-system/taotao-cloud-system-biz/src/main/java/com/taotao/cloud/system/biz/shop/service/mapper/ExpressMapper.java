/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package com.taotao.cloud.system.biz.shop.service.mapper;

import co.yixiang.common.mapper.CoreMapper;
import co.yixiang.modules.shop.domain.YxExpress;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author hupeng
 * @date 2020-05-12
 */
@Repository
@Mapper
public interface ExpressMapper extends CoreMapper<YxExpress> {

}
