package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.LeaveApplyEntity;
import com.taotao.cloud.workflow.biz.form.model.leaveapply.LeaveApplyForm;
import com.taotao.cloud.workflow.biz.form.model.leaveapply.LeaveApplyInfoVO;
import com.taotao.cloud.workflow.biz.form.service.LeaveApplyService;

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
 * 请假申请
 *
 */
@Tag(tags = "请假申请", value = "LeaveApply")
@RestController
@RequestMapping("/api/workflow/Form/LeaveApply")
public class LeaveApplyController {

    @Autowired
    private LeaveApplyService leaveApplyService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取请假申请信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取请假申请信息")
    @GetMapping("/{id}")
    public Result<LeaveApplyInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        LeaveApplyInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), LeaveApplyInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            LeaveApplyEntity entity = leaveApplyService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, LeaveApplyInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建请假申请
     *
     * @param leaveApplyForm 表单对象
     * @return
     */
    @Operation("新建请假申请")
    @PostMapping
    public Result create(@RequestBody @Valid LeaveApplyForm leaveApplyForm) throws WorkFlowException {
        if (leaveApplyForm.getLeaveStartTime() > leaveApplyForm.getLeaveEndTime()) {
            return Result.fail("结束时间不能小于起始时间");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveDayCount())) {
            return Result.fail("请假天数只能是0.5的倍数");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveHour())) {
            return Result.fail("请假小时只能是0.5的倍数");
        }
        LeaveApplyEntity entity = JsonUtil.getJsonToBean(leaveApplyForm, LeaveApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(leaveApplyForm.getStatus())) {
            leaveApplyService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        leaveApplyService.submit(entity.getId(), entity, leaveApplyForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改请假申请
     *
     * @param leaveApplyForm 表单对象
     * @param id             主键
     * @return
     */
    @Operation("修改请假申请")
    @PutMapping("/{id}")
    public Result update(@RequestBody @Valid LeaveApplyForm leaveApplyForm, @PathVariable("id") String id) throws WorkFlowException {
        if (leaveApplyForm.getLeaveStartTime() > leaveApplyForm.getLeaveEndTime()) {
            return Result.fail("结束时间不能小于起始时间");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveDayCount())) {
            return Result.fail("请假天数只能是0.5的倍数");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveHour())) {
            return Result.fail("请假小时只能是0.5的倍数");
        }
        LeaveApplyEntity entity = JsonUtil.getJsonToBean(leaveApplyForm, LeaveApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(leaveApplyForm.getStatus())) {
            leaveApplyService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        leaveApplyService.submit(id, entity, leaveApplyForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
