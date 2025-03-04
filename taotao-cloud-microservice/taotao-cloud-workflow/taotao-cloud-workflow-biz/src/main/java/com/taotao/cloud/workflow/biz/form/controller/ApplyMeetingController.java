package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.ApplyMeetingEntity;
import com.taotao.cloud.workflow.biz.form.model.applymeeting.ApplyMeetingForm;
import com.taotao.cloud.workflow.biz.form.model.applymeeting.ApplyMeetingInfoVO;
import com.taotao.cloud.workflow.biz.form.service.ApplyMeetingService;

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
 * 会议申请
 *
 */
@Tag(tags = "会议申请", value = "ApplyMeeting")
@RestController
@RequestMapping("/api/workflow/Form/ApplyMeeting")
public class ApplyMeetingController {

    @Autowired
    private ApplyMeetingService applyMeetingService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取会议申请信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取会议申请信息")
    @GetMapping("/{id}")
    public Result<ApplyMeetingInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        ApplyMeetingInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), ApplyMeetingInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            ApplyMeetingEntity entity = applyMeetingService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, ApplyMeetingInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建会议申请
     *
     * @param applyMeetingForm 表单对象
     * @return
     */
    @Operation("新建会议申请")
    @PostMapping
    public Result create(@RequestBody @Valid ApplyMeetingForm applyMeetingForm) throws WorkFlowException {
        if (applyMeetingForm.getStartDate() > applyMeetingForm.getEndDate()) {
            return Result.fail("结束时间不能小于开始时间");
        }
        if (applyMeetingForm.getEstimatePeople() != null && StringUtil.isNotEmpty(applyMeetingForm.getEstimatePeople()) && !RegexUtils.checkDigit2(applyMeetingForm.getEstimatePeople())) {
            return Result.fail("预计人数只能输入正整数");
        }
        if (applyMeetingForm.getEstimatedAmount() != null && !RegexUtils.checkDecimals2(String.valueOf(applyMeetingForm.getEstimatedAmount()))) {
            return Result.fail("预计金额必须大于0，最多精确小数点后两位");
        }
        ApplyMeetingEntity entity = JsonUtil.getJsonToBean(applyMeetingForm, ApplyMeetingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(applyMeetingForm.getStatus())) {
            applyMeetingService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        applyMeetingService.submit(entity.getId(), entity, applyMeetingForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改会议申请
     *
     * @param applyMeetingForm 表单对象
     * @param id               主键
     * @return
     */
    @Operation("修改会议申请")
    @PutMapping("/{id}")
    public Result update(@RequestBody @Valid ApplyMeetingForm applyMeetingForm, @PathVariable("id") String id) throws WorkFlowException {
        if (applyMeetingForm.getStartDate() > applyMeetingForm.getEndDate()) {
            return Result.fail("结束时间不能小于开始时间");
        }
        if (applyMeetingForm.getEstimatePeople() != null && StringUtil.isNotEmpty(applyMeetingForm.getEstimatePeople()) && !RegexUtils.checkDigit2(applyMeetingForm.getEstimatePeople())) {
            return Result.fail("预计人数只能输入正整数");
        }
        if (applyMeetingForm.getEstimatedAmount() != null && !RegexUtils.checkDecimals2(String.valueOf(applyMeetingForm.getEstimatedAmount()))) {
            return Result.fail("预计金额必须大于0，最多精确小数点后两位");
        }
        ApplyMeetingEntity entity = JsonUtil.getJsonToBean(applyMeetingForm, ApplyMeetingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(applyMeetingForm.getStatus())) {
            applyMeetingService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        applyMeetingService.submit(id, entity, applyMeetingForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
