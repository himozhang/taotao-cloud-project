package com.taotao.cloud.workflow.biz.form.model.travelapply;


import java.math.BigDecimal;
import lombok.Data;

/**
 * 出差预支申请单
 *
 */
@Data
public class TravelApplyInfoVO {
    @Schema(description = "主键id")
    private String id;
    @Schema(description = "起始地点")
    private String startPlace;
    @Schema(description = "出差人")
    private String travelMan;
    @Schema(description = "紧急程度")
    private Integer flowUrgent;
    @Schema(description = "结束日期")
    private Long endDate;
    @Schema(description = "预支旅费")
    private BigDecimal prepaidTravel;
    @Schema(description = "目的地")
    private String destination;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "流程标题")
    private String flowTitle;
    @Schema(description = "所属部门")
    private String departmental;
    @Schema(description = "所属职务")
    private String position;
    @Schema(description = "申请日期")
    private Long applyDate;
    @Schema(description = "流程主键")
    private String flowId;
    @Schema(description = "流程单据")
    private String billNo;
    @Schema(description = "开始日期")
    private Long startDate;
}
