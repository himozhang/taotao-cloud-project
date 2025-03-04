package com.taotao.cloud.workflow.biz.engine.model.flowtask;

import java.util.Date;
import lombok.Data;

/**
 *
 *
 */
@Data
public class FlowTaskListModel {

    private String id;
    private String processId;
    private String enCode;
    private String fullName;
    private Integer flowUrgent;
    private String flowId;
    private String flowCode;
    private String flowName;
    private String flowCategory;
    private String flowType;
    private Date startTime;
    private Date endTime;
    private String thisStep;
    private String thisStepId;
    private Integer status;
    private Integer completion;
    private String creatorUserId;
    private Date creatorTime;
    private String handleId;
    private String nodeName;
    private String lastModifyUserId;
    private Date lastModifyTime;
    private String approversProperties;
    private String description;
    private String flowVersion;
}
