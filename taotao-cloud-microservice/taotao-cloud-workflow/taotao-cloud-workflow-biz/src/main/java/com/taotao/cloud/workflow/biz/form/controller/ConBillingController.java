package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.ConBillingEntity;
import com.taotao.cloud.workflow.biz.form.model.conbilling.ConBillingForm;
import com.taotao.cloud.workflow.biz.form.model.conbilling.ConBillingInfoVO;
import com.taotao.cloud.workflow.biz.form.service.ConBillingService;

import javax.validation.Valid;

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
 * 合同开票流程
 */
@Tag(tags = "合同开票流程", value = "ConBilling")
@RestController
@RequestMapping("/api/workflow/Form/ConBilling")
public class ConBillingController {

    @Autowired
    private ConBillingService conBillingService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取合同开票流程信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取合同开票流程信息")
    @GetMapping("/{id}")
    public Result<ConBillingInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        ConBillingInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), ConBillingInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            ConBillingEntity entity = conBillingService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, ConBillingInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建合同开票流程
     *
     * @param conBillingForm 表单对象
     * @return
     */
    @Operation("新建合同开票流程")
    @PostMapping
    public Result create(@RequestBody @Valid ConBillingForm conBillingForm) throws WorkFlowException {
        if (conBillingForm.getBillAmount() != null && !"".equals(String.valueOf(conBillingForm.getBillAmount())) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getBillAmount()))) {
            return Result.fail("开票金额必须大于0，最多可以精确到小数点后两位");
        }
        if (conBillingForm.getPayAmount() != null && !"".equals(String.valueOf(conBillingForm.getPayAmount())) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getPayAmount()))) {
            return Result.fail("付款金额必须大于0，最多可以精确到小数点后两位");
        }
        ConBillingEntity entity = JsonUtil.getJsonToBean(conBillingForm, ConBillingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(conBillingForm.getStatus())) {
            conBillingService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        conBillingService.submit(entity.getId(), entity, conBillingForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改合同开票流程
     *
     * @param conBillingForm 表单对象
     * @param id             主键
     * @return
     */
    @Operation("修改合同开票流程")
    @PutMapping("/{id}")
    public Result update(@RequestBody @Valid ConBillingForm conBillingForm, @PathVariable("id") String id) throws WorkFlowException {
        if (conBillingForm.getBillAmount() != null && !"".equals(conBillingForm.getBillAmount()) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getBillAmount()))) {
            return Result.fail("开票金额必须大于0，最多可以精确到小数点后两位");
        }
        if (conBillingForm.getPayAmount() != null && !"".equals(conBillingForm.getPayAmount()) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getPayAmount()))) {
            return Result.fail("付款金额必须大于0，最多可以精确到小数点后两位");
        }
        ConBillingEntity entity = JsonUtil.getJsonToBean(conBillingForm, ConBillingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(conBillingForm.getStatus())) {
            conBillingService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        conBillingService.submit(id, entity, conBillingForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
