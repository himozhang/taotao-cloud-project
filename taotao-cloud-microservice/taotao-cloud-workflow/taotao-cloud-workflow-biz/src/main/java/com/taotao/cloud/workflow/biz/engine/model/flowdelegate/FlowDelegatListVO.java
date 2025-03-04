package com.taotao.cloud.workflow.biz.engine.model.flowdelegate;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 */
@Data
public class FlowDelegatListVO {
    @Schema(description = "主键id")
    private String id;
    @Schema(description = "流程分类")
    private String flowCategory;
    @Schema(description = "被委托人id")
    private String toUserId;
    @Schema(description = "被委托人")
    private String toUserName;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "开始日期")
    private Long startTime;
    @Schema(description = "结束日期")
    private Long endTime;
    @Schema(description = "委托流程id")
    private String flowId;
    @Schema(description = "委托流程名称")
    private String flowName;
    @Schema(description = "有效标志")
    private Integer enabledMark;

}
