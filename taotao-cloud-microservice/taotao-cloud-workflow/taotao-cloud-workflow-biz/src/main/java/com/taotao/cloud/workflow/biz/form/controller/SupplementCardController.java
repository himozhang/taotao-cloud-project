package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.SupplementCardEntity;
import com.taotao.cloud.workflow.biz.form.model.supplementcard.SupplementCardForm;
import com.taotao.cloud.workflow.biz.form.model.supplementcard.SupplementCardInfoVO;
import com.taotao.cloud.workflow.biz.form.service.SupplementCardService;

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
 * 补卡申请
 */
@Tag(tags = "补卡申请", value = "SupplementCard")
@RestController
@RequestMapping("/api/workflow/Form/SupplementCard")
public class SupplementCardController {

    @Autowired
    private SupplementCardService supplementCardService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取补卡申请信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("补卡申请信息")
    @GetMapping("/{id}")
    public Result<SupplementCardInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        SupplementCardInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), SupplementCardInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            SupplementCardEntity entity = supplementCardService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, SupplementCardInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建补卡申请
     *
     * @param supplementCardForm 表单对象
     * @return
     */
    @Operation("新建补卡申请")
    @PostMapping
    public Result create(@RequestBody SupplementCardForm supplementCardForm) throws WorkFlowException {
        if (supplementCardForm.getStartTime() > supplementCardForm.getEndTime()) {
            return Result.fail("结束时间不能小于起始时间");
        }
        SupplementCardEntity entity = JsonUtil.getJsonToBean(supplementCardForm, SupplementCardEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(supplementCardForm.getStatus())) {
            supplementCardService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        supplementCardService.submit(entity.getId(), entity,supplementCardForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改补卡申请
     *
     * @param supplementCardForm 表单对象
     * @param id                 主键
     * @return
     */
    @Operation("修改补卡申请")
    @PutMapping("/{id}")
    public Result update(@RequestBody SupplementCardForm supplementCardForm, @PathVariable("id") String id) throws WorkFlowException {
        if (supplementCardForm.getStartTime() > supplementCardForm.getEndTime()) {
            return Result.fail("结束时间不能小于起始时间");
        }
        SupplementCardEntity entity = JsonUtil.getJsonToBean(supplementCardForm, SupplementCardEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(supplementCardForm.getStatus())) {
            supplementCardService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        supplementCardService.submit(id, entity,supplementCardForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
