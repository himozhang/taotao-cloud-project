package com.taotao.cloud.workflow.biz.form.model.outboundorder;


import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 出库单
 *
 *
  /
@Data
public class OutboundOrderForm {
    @NotBlank(message = "流程主键不能为空")
    @Schema(description = "流程主键")
    private String flowId;
    @NotBlank(message = "流程标题不能为空")
    @Schema(description = "流程标题")
    private String flowTitle;
    @NotNull(message = "紧急程度不能为空")
    @Schema(description = "紧急程度")
    private Integer flowUrgent;
    @NotBlank(message = "流程单据不能为空")
    @Schema(description = "流程单据")
    private String billNo;
    @Schema(description = "客户名称")
    private String customerName;
    @Schema(description = "仓库")
    private String warehouse;
    @Schema(description = "仓库人")
    private String outStorage;
    @Schema(description = "业务人员")
    private String businessPeople;
    @Schema(description = "业务类型")
    private String businessType;
    @NotNull(message = "出库日期不能为空")
    @Schema(description = "出库日期")
    private Long outboundDate;
    @Schema(description = "备注")
    private String description;
    @Schema(description = "提交/保存 0-1")
    private String status;
    @Schema(description = "明细")
    List<OutboundEntryEntityInfoModel> entryList;
    @Schema(description = "候选人")
    private Map<String, List<String>> candidateList;
}
