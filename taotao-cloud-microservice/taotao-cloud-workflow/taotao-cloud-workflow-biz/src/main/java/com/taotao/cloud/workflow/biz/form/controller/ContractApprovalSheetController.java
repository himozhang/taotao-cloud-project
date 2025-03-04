package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.ContractApprovalSheetEntity;
import com.taotao.cloud.workflow.biz.form.model.contractapprovalsheet.ContractApprovalSheetForm;
import com.taotao.cloud.workflow.biz.form.model.contractapprovalsheet.ContractApprovalSheetInfoVO;
import com.taotao.cloud.workflow.biz.form.service.ContractApprovalSheetService;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
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
 * 合同申请单表
 *
 */
@Tag(tags = "合同申请单表", value = "ContractApprovalSheet")
@RestController
@RequestMapping("/api/workflow/Form/ContractApprovalSheet")
public class ContractApprovalSheetController {

    @Autowired
    private ContractApprovalSheetService contractApprovalSheetService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取合同申请单表信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取合同申请单表信息")
    @GetMapping("/{id}")
    public Result<ContractApprovalSheetInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        ContractApprovalSheetInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), ContractApprovalSheetInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            ContractApprovalSheetEntity entity = contractApprovalSheetService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, ContractApprovalSheetInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建合同申请单表
     *
     * @param contractApprovalSheetForm 表单对象
     * @return
     */
    @Operation("新建合同申请单表")
    @PostMapping
    public Result create(@RequestBody @Valid ContractApprovalSheetForm contractApprovalSheetForm) throws WorkFlowException {
        if (contractApprovalSheetForm.getStartContractDate() > contractApprovalSheetForm.getEndContractDate()) {
            return Result.fail("结束时间不能小于开始时间");
        }
        if (contractApprovalSheetForm.getIncomeAmount() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getIncomeAmount())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getIncomeAmount()))) {
            return Result.fail("收入金额必须大于0，最多可以输入两位小数点");
        }
        if (contractApprovalSheetForm.getTotalExpenditure() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getIncomeAmount())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getTotalExpenditure()))) {
            return Result.fail("支出金额必须大于0，最多可以输入两位小数点");
        }
        ContractApprovalSheetEntity entity = JsonUtil.getJsonToBean(contractApprovalSheetForm, ContractApprovalSheetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(contractApprovalSheetForm.getStatus())) {
            contractApprovalSheetService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        contractApprovalSheetService.submit(entity.getId(), entity, contractApprovalSheetForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改合同申请单表
     *
     * @param contractApprovalSheetForm 表单对象
     * @param id                        主键
     * @return
     */
    @Operation("修改合同申请单表")
    @PutMapping("/{id}")
    public Result update(@RequestBody @Valid ContractApprovalSheetForm contractApprovalSheetForm, @PathVariable("id") String id) throws WorkFlowException {
        if (contractApprovalSheetForm.getStartContractDate() > contractApprovalSheetForm.getEndContractDate()) {
            return Result.fail("结束时间不能小于开始时间");
        }
        if (contractApprovalSheetForm.getIncomeAmount() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getIncomeAmount())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getIncomeAmount()))) {
            return Result.fail("收入金额必须大于0，最多可以输入两位小数点");
        }
        if (contractApprovalSheetForm.getTotalExpenditure() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getTotalExpenditure())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getTotalExpenditure()))) {
            return Result.fail("支出金额必须大于0，最多可以输入两位小数点");
        }
        ContractApprovalSheetEntity entity = JsonUtil.getJsonToBean(contractApprovalSheetForm, ContractApprovalSheetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(contractApprovalSheetForm.getStatus())) {
            contractApprovalSheetService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        contractApprovalSheetService.submit(id, entity, contractApprovalSheetForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
