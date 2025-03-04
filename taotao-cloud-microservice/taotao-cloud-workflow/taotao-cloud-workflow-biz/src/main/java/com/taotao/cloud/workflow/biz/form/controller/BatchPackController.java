package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.BatchPackEntity;
import com.taotao.cloud.workflow.biz.form.model.batchpack.BatchPackForm;
import com.taotao.cloud.workflow.biz.form.model.batchpack.BatchPackInfoVO;
import com.taotao.cloud.workflow.biz.form.service.BatchPackService;

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
 * 批包装指令
 */
@Tag(tags = "批包装指令", value = "BatchPack")
@RestController
@RequestMapping("/api/workflow/Form/BatchPack")
public class BatchPackController {

    @Autowired
    private BatchPackService batchPackService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取批包装指令信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取批包装指令信息")
    @GetMapping("/{id}")
    public Result<BatchPackInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        BatchPackInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), BatchPackInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            BatchPackEntity entity = batchPackService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, BatchPackInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建批包装指令
     *
     * @param batchPackForm 表单对象
     * @return
     */
    @Operation("新建批包装指令")
    @PostMapping
    public Result create(@RequestBody @Valid BatchPackForm batchPackForm) throws WorkFlowException {
        if (batchPackForm.getProductionQuty() != null && StringUtil.isNotEmpty(batchPackForm.getProductionQuty()) && !RegexUtils.checkDigit2(batchPackForm.getProductionQuty())) {
            return Result.fail("批产数量只能输入正整数");
        }
        BatchPackEntity entity = JsonUtil.getJsonToBean(batchPackForm, BatchPackEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(batchPackForm.getStatus())) {
            batchPackService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        batchPackService.submit(entity.getId(), entity, batchPackForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改批包装指令
     *
     * @param batchPackForm 表单对象
     * @param id            主键
     * @return
     */
    @Operation("修改批包装指令")
    @PutMapping("/{id}")
    public Result update(@RequestBody @Valid BatchPackForm batchPackForm, @PathVariable("id") String id) throws WorkFlowException {
        if (batchPackForm.getProductionQuty() != null && StringUtil.isNotEmpty(batchPackForm.getProductionQuty()) && !RegexUtils.checkDigit2(batchPackForm.getProductionQuty())) {
            return Result.fail("批产数量只能输入正整数");
        }
        BatchPackEntity entity = JsonUtil.getJsonToBean(batchPackForm, BatchPackEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(batchPackForm.getStatus())) {
            batchPackService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        batchPackService.submit(id, entity, batchPackForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
