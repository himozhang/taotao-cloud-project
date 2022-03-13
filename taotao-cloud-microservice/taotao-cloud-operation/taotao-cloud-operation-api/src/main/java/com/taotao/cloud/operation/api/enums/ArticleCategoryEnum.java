package com.taotao.cloud.operation.api.enums;

/**
 * 文章分类枚举
 *
 */
public enum ArticleCategoryEnum {

    /**
     * 店铺公告
     */
    STORE_ARTICLE,
    /**
     * 平台公告
     */
    ANNOUNCEMENT,
    /**
     * 平台信息
     */
    PLATFORM_INFORMATION,
    /**
     * 其他文章分类
     */
    OTHER;

    public String value() {
        return this.name();
    }

}
