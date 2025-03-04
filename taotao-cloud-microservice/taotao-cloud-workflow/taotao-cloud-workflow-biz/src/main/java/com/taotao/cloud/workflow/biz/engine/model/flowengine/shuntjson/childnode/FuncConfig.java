package com.taotao.cloud.workflow.biz.engine.model.flowengine.shuntjson.childnode;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 */
@Data
public class FuncConfig {

    private boolean on = false;
    private String msgId;
    private String interfaceId;
    private String msgName;
    private List<TemplateJsonModel> templateJson = new ArrayList<>();
}
