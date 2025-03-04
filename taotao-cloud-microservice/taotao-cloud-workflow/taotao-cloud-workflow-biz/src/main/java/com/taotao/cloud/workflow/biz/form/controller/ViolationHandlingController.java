package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.ViolationHandlingEntity;
import com.taotao.cloud.workflow.biz.form.model.violationhandling.ViolationHandlingForm;
import com.taotao.cloud.workflow.biz.form.model.violationhandling.ViolationHandlingInfoVO;
import com.taotao.cloud.workflow.biz.form.service.ViolationHandlingService;

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
 * 违章处理申请表
 *
 */
@Tag(tags = "违章处理申请表", value = "ViolationHandling")
@RestController
@RequestMapping("/api/workflow/Form/ViolationHandling")
public class ViolationHandlingController {

    @Autowired
    private ViolationHandlingService violationHandlingService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取违章处理申请表信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取违章处理申请表信息")
    @GetMapping("/{id}")
    public Result<ViolationHandlingInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        ViolationHandlingInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), ViolationHandlingInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            ViolationHandlingEntity entity = violationHandlingService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, ViolationHandlingInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建违章处理申请表
     *
     * @param violationHandlingForm 表单对象
     * @return
     */
    @Operation("新建违章处理申请表")
    @PostMapping
    public Result create(@RequestBody ViolationHandlingForm violationHandlingForm) throws WorkFlowException {
        ViolationHandlingEntity entity = JsonUtil.getJsonToBean(violationHandlingForm, ViolationHandlingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(violationHandlingForm.getStatus())) {
            violationHandlingService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        violationHandlingService.submit(entity.getId(), entity,violationHandlingForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改违章处理申请表
     *
     * @param violationHandlingForm 表单对象
     * @param id                    主键
     * @return
     */
    @Operation("修改违章处理申请表")
    @PutMapping("/{id}")
    public Result update(@RequestBody ViolationHandlingForm violationHandlingForm, @PathVariable("id") String id) throws WorkFlowException {
        ViolationHandlingEntity entity = JsonUtil.getJsonToBean(violationHandlingForm, ViolationHandlingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(violationHandlingForm.getStatus())) {
            violationHandlingService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        violationHandlingService.submit(id, entity,violationHandlingForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
