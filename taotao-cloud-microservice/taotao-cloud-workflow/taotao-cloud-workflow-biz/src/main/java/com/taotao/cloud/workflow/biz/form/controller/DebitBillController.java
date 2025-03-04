package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.DebitBillEntity;
import com.taotao.cloud.workflow.biz.form.model.debitbill.DebitBillForm;
import com.taotao.cloud.workflow.biz.form.model.debitbill.DebitBillInfoVO;
import com.taotao.cloud.workflow.biz.form.service.DebitBillService;

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
 * 借支单
 *
 */
@Tag(tags = "借支单", value = "DebitBill")
@RestController
@RequestMapping("/api/workflow/Form/DebitBill")
public class DebitBillController {

    @Autowired
    private DebitBillService debitBillService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取借支单信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取借支单信息")
    @GetMapping("/{id}")
    public Result<DebitBillInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        DebitBillInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), DebitBillInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            DebitBillEntity entity = debitBillService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, DebitBillInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建借支单
     *
     * @param debitBillForm 表单对象
     * @return
     */
    @Operation("新建借支单")
    @PostMapping
    public Result create(@RequestBody @Valid DebitBillForm debitBillForm) throws WorkFlowException {
        if (debitBillForm.getAmountDebit() != null && !"".equals(String.valueOf(debitBillForm.getAmountDebit())) && !RegexUtils.checkDecimals2(String.valueOf(debitBillForm.getAmountDebit()))) {
            return Result.fail("借支金额必须大于0，最多可以输入两位小数点");
        }
        DebitBillEntity entity = JsonUtil.getJsonToBean(debitBillForm, DebitBillEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(debitBillForm.getStatus())) {
            debitBillService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        debitBillService.submit(entity.getId(), entity, debitBillForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改借支单
     *
     * @param debitBillForm 表单对象
     * @param id            主键
     * @return
     */
    @Operation("修改借支单")
    @PutMapping("/{id}")
    public Result update(@RequestBody @Valid DebitBillForm debitBillForm, @PathVariable("id") String id) throws WorkFlowException {
        if (debitBillForm.getAmountDebit() != null && !"".equals(String.valueOf(debitBillForm.getAmountDebit())) && !RegexUtils.checkDecimals2(String.valueOf(debitBillForm.getAmountDebit()))) {
            return Result.fail("借支金额必须大于0，最多可以输入两位小数点");
        }
        DebitBillEntity entity = JsonUtil.getJsonToBean(debitBillForm, DebitBillEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(debitBillForm.getStatus())) {
            debitBillService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        debitBillService.submit(id, entity, debitBillForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
