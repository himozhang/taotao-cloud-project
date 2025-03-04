package com.taotao.cloud.workflow.biz.form.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.Map;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskService;
import com.taotao.cloud.workflow.biz.engine.util.ModelUtil;
import com.taotao.cloud.workflow.biz.form.entity.DocumentApprovalEntity;
import com.taotao.cloud.workflow.biz.form.mapper.DocumentApprovalMapper;
import com.taotao.cloud.workflow.biz.form.service.DocumentApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 文件签批意见表
 *
 */
@Service
public class DocumentApprovalServiceImpl extends ServiceImpl<DocumentApprovalMapper, DocumentApprovalEntity> implements DocumentApprovalService {

    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FileManageUtil fileManageUtil;

    @Override
    public DocumentApprovalEntity getInfo(String id) {
        QueryWrapper<DocumentApprovalEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentApprovalEntity::getId, id);
        DocumentApprovalEntity entity = this.getOne(queryWrapper);
        if (entity != null) {
            entity.setModifyOpinion(entity.getModifyOpinion());
        }
        return entity;
    }

    @Override
    @DSTransactional
    public void save(String id, DocumentApprovalEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleService.useBillNumber("WF_DocumentApprovalNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            this.updateById(entity);
            //更新附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.updateFile(data);
        }
        //流程信息
        ModelUtil.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(),entity);
    }

    @Override
    @DSTransactional
    public void submit(String id, DocumentApprovalEntity entity, Map<String, List<String>> candidateList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleService.useBillNumber("WF_DocumentApprovalNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            this.updateById(entity);
            //更新附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.updateFile(data);
        }
        //流程信息
        ModelUtil.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity,null, candidateList);
    }

    @Override
    public void data(String id, String data) {
        DocumentApprovalForm documentApprovalForm = JsonUtil.getJsonToBean(data, DocumentApprovalForm.class);
        DocumentApprovalEntity entity = JsonUtil.getJsonToBean(documentApprovalForm, DocumentApprovalEntity.class);
        entity.setId(id);
        this.saveOrUpdate(entity);
    }
}
