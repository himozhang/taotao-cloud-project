package com.taotao.cloud.workflow.biz.form.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.workflow.biz.engine.service.FlowTaskService;
import com.taotao.cloud.workflow.biz.engine.util.ModelUtil;
import com.taotao.cloud.workflow.biz.form.entity.MaterialEntryEntity;
import com.taotao.cloud.workflow.biz.form.entity.MaterialRequisitionEntity;
import com.taotao.cloud.workflow.biz.form.mapper.MaterialRequisitionMapper;
import com.taotao.cloud.workflow.biz.form.model.materialrequisition.MaterialEntryEntityInfoModel;
import com.taotao.cloud.workflow.biz.form.model.materialrequisition.MaterialRequisitionForm;
import com.taotao.cloud.workflow.biz.form.service.MaterialEntryService;
import com.taotao.cloud.workflow.biz.form.service.MaterialRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 领料单
 *
 */
@Service
public class MaterialRequisitionServiceImpl extends ServiceImpl<MaterialRequisitionMapper, MaterialRequisitionEntity> implements MaterialRequisitionService {

    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private MaterialEntryService materialEntryService;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public List<MaterialEntryEntity> getMaterialEntryList(String id) {
        QueryWrapper<MaterialEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MaterialEntryEntity::getLeadeId, id).orderByDesc(MaterialEntryEntity::getSortCode);
        return materialEntryService.list(queryWrapper);
    }

    @Override
    public MaterialRequisitionEntity getInfo(String id) {
        QueryWrapper<MaterialRequisitionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MaterialRequisitionEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void save(String id, MaterialRequisitionEntity entity, List<MaterialEntryEntity> materialEntryEntityList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < materialEntryEntityList.size(); i++) {
                materialEntryEntityList.get(i).setId(RandomUtil.uuId());
                materialEntryEntityList.get(i).setLeadeId(entity.getId());
                materialEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                materialEntryService.save(materialEntryEntityList.get(i));
            }
            //创建
            this.save(entity);
            billRuleService.useBillNumber("WF_MaterialRequisitionNo");
        } else {
            entity.setId(id);
            QueryWrapper<MaterialEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(MaterialEntryEntity::getLeadeId, entity.getId());
            materialEntryService.remove(queryWrapper);
            for (int i = 0; i < materialEntryEntityList.size(); i++) {
                materialEntryEntityList.get(i).setId(RandomUtil.uuId());
                materialEntryEntityList.get(i).setLeadeId(entity.getId());
                materialEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                materialEntryService.save(materialEntryEntityList.get(i));
            }
            //编辑
            this.updateById(entity);
        }
        //流程信息
        ModelUtil.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    @DSTransactional
    public void submit(String id, MaterialRequisitionEntity entity, List<MaterialEntryEntity> materialEntryEntityList, Map<String, List<String>> candidateList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < materialEntryEntityList.size(); i++) {
                materialEntryEntityList.get(i).setId(RandomUtil.uuId());
                materialEntryEntityList.get(i).setLeadeId(entity.getId());
                materialEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                materialEntryService.save(materialEntryEntityList.get(i));
            }
            //创建
            this.save(entity);
            billRuleService.useBillNumber("WF_MaterialRequisitionNo");
        } else {
            entity.setId(id);
            QueryWrapper<MaterialEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(MaterialEntryEntity::getLeadeId, entity.getId());
            materialEntryService.remove(queryWrapper);
            for (int i = 0; i < materialEntryEntityList.size(); i++) {
                materialEntryEntityList.get(i).setId(RandomUtil.uuId());
                materialEntryEntityList.get(i).setLeadeId(entity.getId());
                materialEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                materialEntryService.save(materialEntryEntityList.get(i));
            }
            //编辑
            this.updateById(entity);
        }
        //流程信息
        ModelUtil.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity, null, candidateList);
    }

    @Override
    public void data(String id, String data) {
        MaterialRequisitionForm materialRequisitionForm = JsonUtil.getJsonToBean(data, MaterialRequisitionForm.class);
        MaterialRequisitionEntity entity = JsonUtil.getJsonToBean(materialRequisitionForm, MaterialRequisitionEntity.class);
        List<MaterialEntryEntityInfoModel> entryList = materialRequisitionForm.getEntryList() != null ? materialRequisitionForm.getEntryList() : new ArrayList<>();
        List<MaterialEntryEntity> materialEntryEntityList = JsonUtil.getJsonToList(entryList, MaterialEntryEntity.class);
        entity.setId(id);
        QueryWrapper<MaterialEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MaterialEntryEntity::getLeadeId, entity.getId());
        materialEntryService.remove(queryWrapper);
        for (int i = 0; i < materialEntryEntityList.size(); i++) {
            materialEntryEntityList.get(i).setId(RandomUtil.uuId());
            materialEntryEntityList.get(i).setLeadeId(entity.getId());
            materialEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
            materialEntryService.save(materialEntryEntityList.get(i));
        }
        this.saveOrUpdate(entity);
    }
}
