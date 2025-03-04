package com.taotao.cloud.workflow.biz.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taotao.cloud.workflow.biz.form.entity.FinishedProductEntity;
import com.taotao.cloud.workflow.biz.form.entity.FinishedProductEntryEntity;

import java.util.List;
import java.util.Map;

/**
 * 成品入库单
 */
public interface FinishedProductService extends IService<FinishedProductEntity> {

    /**
     * 列表
     *
     * @param id 主键值
     * @return
     */
    List<FinishedProductEntryEntity> getFinishedEntryList(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FinishedProductEntity getInfo(String id);

    /**
     * 保存
     *
     * @param id                             主键值
     * @param entity                         实体对象
     * @param finishedProductEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void save(String id, FinishedProductEntity entity, List<FinishedProductEntryEntity> finishedProductEntryEntityList) throws WorkFlowException;

    /**
     * 提交
     *
     * @param id                             主键值
     * @param entity                         实体对象
     * @param finishedProductEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void submit(String id, FinishedProductEntity entity, List<FinishedProductEntryEntity> finishedProductEntryEntityList, Map<String, List<String>> candidateList) throws WorkFlowException;

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);
}
