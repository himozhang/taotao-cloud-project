package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.StaffOvertimeEntity;
import com.taotao.cloud.workflow.biz.form.model.staffovertime.StaffOvertimeForm;
import com.taotao.cloud.workflow.biz.form.model.staffovertime.StaffOvertimeInfoVO;
import com.taotao.cloud.workflow.biz.form.service.StaffOvertimeService;

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
 * 员工加班申请表
 *
 */
@Tag(tags = "员工加班申请表", value = "StaffOvertime")
@RestController
@RequestMapping("/api/workflow/Form/StaffOvertime")
public class StaffOvertimeController {

    @Autowired
    private StaffOvertimeService staffOvertimeService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取员工加班申请表信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取员工加班申请表信息")
    @GetMapping("/{id}")
    public Result<StaffOvertimeInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        StaffOvertimeInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), StaffOvertimeInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            StaffOvertimeEntity entity = staffOvertimeService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, StaffOvertimeInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建员工加班申请表
     *
     * @param staffOvertimeForm 表单对象
     * @return
     */
    @Operation("新建员工加班申请表")
    @PostMapping
    public Result create(@RequestBody StaffOvertimeForm staffOvertimeForm) throws WorkFlowException {
        if (staffOvertimeForm.getStartTime() > staffOvertimeForm.getEndTime()) {
            return Result.fail("结束时间不能小于起始时间");
        }
        StaffOvertimeEntity entity = JsonUtil.getJsonToBean(staffOvertimeForm, StaffOvertimeEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(staffOvertimeForm.getStatus())) {
            staffOvertimeService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        staffOvertimeService.submit(entity.getId(), entity,staffOvertimeForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改
     *
     * @param staffOvertimeForm 表单对象
     * @param id                主键
     * @return
     */
    @Operation("修改员工加班申请表")
    @PutMapping("/{id}")
    public Result update(@RequestBody StaffOvertimeForm staffOvertimeForm, @PathVariable("id") String id) throws WorkFlowException {
        if (staffOvertimeForm.getStartTime() > staffOvertimeForm.getEndTime()) {
            return Result.fail("结束时间不能小于起始时间");
        }
        StaffOvertimeEntity entity = JsonUtil.getJsonToBean(staffOvertimeForm, StaffOvertimeEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(staffOvertimeForm.getStatus())) {
            staffOvertimeService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        staffOvertimeService.submit(id, entity,staffOvertimeForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
