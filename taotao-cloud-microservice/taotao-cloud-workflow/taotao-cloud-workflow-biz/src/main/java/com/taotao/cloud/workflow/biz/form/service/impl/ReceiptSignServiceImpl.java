package com.taotao.cloud.workflow.biz.form.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.Map;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskService;
import com.taotao.cloud.workflow.biz.form.entity.ReceiptSignEntity;
import com.taotao.cloud.workflow.biz.form.mapper.ReceiptSignMapper;
import com.taotao.cloud.workflow.biz.form.service.ReceiptSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收文签呈单
 *
 */
@Service
public class ReceiptSignServiceImpl extends ServiceImpl<ReceiptSignMapper, ReceiptSignEntity> implements ReceiptSignService {

    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FileManageUtil fileManageUtil;

    @Override
    public ReceiptSignEntity getInfo(String id) {
        QueryWrapper<ReceiptSignEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ReceiptSignEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void save(String id, ReceiptSignEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            //解析
            entity.setReceiptPaper(entity.getReceiptPaper());
            this.save(entity);
            billRuleService.useBillNumber("WF_ReceiptSignNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            //解析
            entity.setReceiptPaper(entity.getReceiptPaper());
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
    public void submit(String id, ReceiptSignEntity entity, Map<String, List<String>> candidateList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            save(entity);
            billRuleService.useBillNumber("WF_ReceiptSignNo");
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
        ReceiptSignForm receiptSignForm = JsonUtil.getJsonToBean(data, ReceiptSignForm.class);
        ReceiptSignEntity entity = JsonUtil.getJsonToBean(receiptSignForm, ReceiptSignEntity.class);
        entity.setId(id);
        this.saveOrUpdate(entity);
    }
}
