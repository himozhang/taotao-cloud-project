package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.QuotationApprovalEntity;
import com.taotao.cloud.workflow.biz.form.model.quotationapproval.QuotationApprovalForm;
import com.taotao.cloud.workflow.biz.form.model.quotationapproval.QuotationApprovalInfoVO;
import com.taotao.cloud.workflow.biz.form.service.QuotationApprovalService;

import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报价审批表
 *
 */
@Tag(tags = "报价审批表", value = "QuotationApproval")
@RestController
@RequestMapping("/api/workflow/Form/QuotationApproval")
public class QuotationApprovalController {


    @Autowired
    private QuotationApprovalService quotationApprovalService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取报价审批表信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取报价审批表信息")
    @GetMapping("/{id}")
    public Result<QuotationApprovalInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        QuotationApprovalInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), QuotationApprovalInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            QuotationApprovalEntity entity = quotationApprovalService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, QuotationApprovalInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建报价审批表
     *
     * @param quotationApprovalForm 表单对象
     * @return
     */
    @Operation("新建报价审批表")
    @PostMapping
    public Result create(@RequestBody QuotationApprovalForm quotationApprovalForm) throws WorkFlowException {
        QuotationApprovalEntity entity = JsonUtil.getJsonToBean(quotationApprovalForm, QuotationApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(quotationApprovalForm.getStatus())) {
            quotationApprovalService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        quotationApprovalService.submit(entity.getId(), entity,quotationApprovalForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改报价审批表
     *
     * @param quotationApprovalForm 表单对象
     * @param id                    主键
     * @return
     */
    @Operation("修改报价审批表")
    @PutMapping("/{id}")
    public Result update(@RequestBody QuotationApprovalForm quotationApprovalForm, @PathVariable("id") String id) throws WorkFlowException {
        QuotationApprovalEntity entity = JsonUtil.getJsonToBean(quotationApprovalForm, QuotationApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(quotationApprovalForm.getStatus())) {
            quotationApprovalService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        quotationApprovalService.submit(id, entity,quotationApprovalForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
