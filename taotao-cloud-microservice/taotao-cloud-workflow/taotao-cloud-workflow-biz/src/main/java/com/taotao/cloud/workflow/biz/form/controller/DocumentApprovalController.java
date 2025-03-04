package com.taotao.cloud.workflow.biz.form.controller;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskOperatorEntity;
import com.taotao.cloud.workflow.biz.engine.enums.FlowStatusEnum;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskOperatorService;
import com.taotao.cloud.workflow.biz.form.entity.DocumentApprovalEntity;
import com.taotao.cloud.workflow.biz.form.model.documentapproval.DocumentApprovalForm;
import com.taotao.cloud.workflow.biz.form.model.documentapproval.DocumentApprovalInfoVO;
import com.taotao.cloud.workflow.biz.form.service.DocumentApprovalService;

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
 * 文件签批意见表
 *
 */
@Tag(tags = "文件签批意见表", value = "DocumentApproval")
@RestController
@RequestMapping("/api/workflow/Form/DocumentApproval")
public class DocumentApprovalController {

    @Autowired
    private DocumentApprovalService documentApprovalService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;

    /**
     * 获取文件签批意见表信息
     *
     * @param id 主键值
     * @return
     */
    @Operation("获取文件签批意见表信息")
    @GetMapping("/{id}")
    public Result<DocumentApprovalInfoVO> info(@PathVariable("id") String id, String taskOperatorId) throws DataException {
        DocumentApprovalInfoVO vo = null;
        boolean isData = true;
        if (StringUtil.isNotEmpty(taskOperatorId)) {
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
            if (operator != null) {
                if (StringUtil.isNotEmpty(operator.getDraftData())) {
                    vo = JsonUtil.getJsonToBean(operator.getDraftData(), DocumentApprovalInfoVO.class);
                    isData = false;
                }
            }
        }
        if (isData) {
            DocumentApprovalEntity entity = documentApprovalService.getInfo(id);
            vo = JsonUtil.getJsonToBean(entity, DocumentApprovalInfoVO.class);
        }
        return Result.success(vo);
    }

    /**
     * 新建文件签批意见表
     *
     * @param documentApprovalForm 表单对象
     * @return
     */
    @Operation("新建文件签批意见表")
    @PostMapping
    public Result create(@RequestBody @Valid DocumentApprovalForm documentApprovalForm) throws WorkFlowException {
        DocumentApprovalEntity entity = JsonUtil.getJsonToBean(documentApprovalForm, DocumentApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(documentApprovalForm.getStatus())) {
            documentApprovalService.save(entity.getId(), entity);
            return Result.success(MsgCode.SU002.get());
        }
        documentApprovalService.submit(entity.getId(), entity, documentApprovalForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }

    /**
     * 修改文件签批意见表
     *
     * @param documentApprovalForm 表单对象
     * @param id                   主键
     * @return
     */
    @Operation("修改文件签批意见表")
    @PutMapping("/{id}")
    public Result update(@RequestBody @Valid DocumentApprovalForm documentApprovalForm, @PathVariable("id") String id) throws WorkFlowException {
        DocumentApprovalEntity entity = JsonUtil.getJsonToBean(documentApprovalForm, DocumentApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(documentApprovalForm.getStatus())) {
            documentApprovalService.save(id, entity);
            return Result.success(MsgCode.SU002.get());
        }
        documentApprovalService.submit(id, entity, documentApprovalForm.getCandidateList());
        return Result.success(MsgCode.SU006.get());
    }
}
