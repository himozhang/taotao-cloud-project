/**
 * Copyright (C) 2018-2020
 * All rights reserved, Designed By www.yixiang.co
 * 注意：
 * 本软件为www.yixiang.co开发研制
 */
package com.taotao.cloud.system.api.dto;

import co.yixiang.annotation.Query;
import lombok.Data;

/**
 * @author hupeng
 * @date 2020-05-13
 */
@Data
public class YxUserExtractQueryCriteria {


    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String realName;
}
