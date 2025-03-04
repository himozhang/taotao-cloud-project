package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.MonthlyReportEntity;
import com.taotao.cloud.workflow.biz.form.model.monthlyreport.MonthlyReportForm;
import com.taotao.cloud.workflow.biz.form.model.monthlyreport.MonthlyReportInfoVO;
import com.taotao.cloud.workflow.biz.form.service.MonthlyReportService;

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
 * 月工作总结
 *
 */
@Tag(tags = "月工作总结", value = "MonthlyReport")
@RestController
@RequestMapping("/api/workflow/Form/MonthlyReport")
public class MonthlyReportController {

    @Autowired
    private MonthlyReportService monthlyReportService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取月工作总结信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取月工作总结信息")
    @GetMapping("/{id}")
    public Result<MonthlyReportInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        MonthlyReportInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), MonthlyReportInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            MonthlyReportEntity entity = monthlyReportService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, MonthlyReportInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建月工作总结
     *
     * @param monthlyReportForm 表单对象
     * @return
     */
    @Operation("新建月工作总结")
    @PostMapping
    public Result create(@RequestBody MonthlyReportForm monthlyReportForm) throws WorkFlowException {
        MonthlyReportEntity entity = JsonUtil.getJsonToBean(monthlyReportForm, MonthlyReportEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(monthlyReportForm.getStatus())) {
            monthlyReportService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        monthlyReportService.submit(entity.getId(), entity,monthlyReportForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改月工作总结
     *
     * @param monthlyReportForm 表单对象
     * @param id                主键
     * @return
     */
    @Operation("修改月工作总结")
    @PutMapping("/{id}")
    public Result update(@RequestBody MonthlyReportForm monthlyReportForm, @PathVariable("id") String id) throws WorkFlowException {
        MonthlyReportEntity entity = JsonUtil.getJsonToBean(monthlyReportForm, MonthlyReportEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(monthlyReportForm.getStatus())) {
            monthlyReportService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        monthlyReportService.submit(id, entity,monthlyReportForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
