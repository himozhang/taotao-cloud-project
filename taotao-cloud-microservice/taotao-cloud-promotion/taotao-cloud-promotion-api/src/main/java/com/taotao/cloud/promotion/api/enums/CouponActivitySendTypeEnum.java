package com.taotao.cloud.promotion.api.enums;

/**
 * 优惠券活动发送类型枚举
 *
 */
public enum CouponActivitySendTypeEnum {

    /**
     * "全部会员"
     */
    ALL("全部会员"),
    /**
     * "指定会员"
     */
    DESIGNATED("指定会员");

    private final String description;

    CouponActivitySendTypeEnum(String str) {
        this.description = str;
    }

    public String description() {
        return description;
    }
}
