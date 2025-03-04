package com.taotao.cloud.sys.biz.model.entity.config;

import java.io.Serializable;
import lombok.Data;

/**
 * 快递设置
 *
 */
@Data
public class KuaidiSetting implements Serializable {
    private static final long serialVersionUID = 3520379500723173689L;
    /**
     * 企业id
     */
    private String ebusinessID;
    /**
     * 密钥
     */
    private String appKey;
    /**
     * api地址
     */
    private String reqURL;
}
