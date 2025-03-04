package com.taotao.cloud.workflow.biz.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

import com.taotao.cloud.workflow.biz.engine.entity.FlowTaskEntity;
import com.taotao.cloud.workflow.biz.engine.model.flowtask.FlowTaskListModel;
import org.apache.ibatis.annotations.Param;

/**
 * 流程任务
 *
 */
public interface FlowTaskMapper extends BaseMapper<FlowTaskEntity> {
    /**
     * 已办事宜
     * @param map 参数
     * @return
     */

	//SELECT  r.F_Id AS F_Id, t.F_ProcessId, t.F_EnCode,t.F_StartTime, t.F_FullName, t.F_FlowUrgent, t.F_FlowId , t.F_FlowCode , t.F_FlowName,
    //        t.F_FlowCategory, t.F_EndTime, r.F_NodeName AS F_ThisStep, r.F_TaskNodeId AS F_ThisStepId, r.F_HandleStatus AS F_Status,
    //        t.F_Completion, t.F_CreatorUserId, r.F_HandleTime AS F_CreatorTime, t.F_LastModifyUserId, t.F_LastModifyTime FROM flow_task t left join
    //        flow_taskoperatorrecord r on r.F_TaskId = t.F_Id WHERE 1=1 AND r.F_Status &lt;2  AND (r.F_HandleStatus = 0 OR r.F_HandleStatus = 1) AND r.F_TaskOperatorId is not null
    //        AND r.F_HandleId = #{map.handleId} ${map.sql}
    List<FlowTaskListModel> getTrialList(@Param("map") Map<String, Object> map);

    /**
     * 抄送事宜
     * @param sql 自定义sql语句
     * @return
     */
	//       SELECT t.F_Id, t.F_ProcessId,t.F_EnCode, t.F_FullName, t.F_FlowUrgent, t.F_FlowId , t.F_FlowCode , t.F_FlowName, t.F_FlowCategory,
    //       t.F_StartTime, t.F_EndTime, c.F_NodeName AS F_ThisStep, c.F_TaskNodeId AS F_ThisStepId, t.F_Status, t.F_Completion, t.F_CreatorUserId,
    //       c.F_CreatorTime, t.F_LastModifyUserId, t.F_LastModifyTime FROM flow_task t left join flow_taskcirculate c on c.F_TaskId = t.F_Id WHERE 1=1 ${sql}
    List<FlowTaskListModel> getCirculateList(@Param("sql") String sql);

    /**
     * 待办事宜
     * @param sql 自定义sql语句
     * @return
     */
	//        SELECT o.F_Id AS F_Id, t.F_ProcessId, t.F_EnCode, t.F_FullName, t.F_FlowUrgent, t.F_FlowId , t.F_FlowCode ,t.F_FlowName, t.F_FlowCategory,
    //        t.F_StartTime, t.F_EndTime, t.F_ThisStep, n.F_Id as F_ThisStepId, t.F_Status, t.F_Completion, t.F_CreatorUserId, o.F_CreatorTime, o.F_HandleId, t.F_LastModifyUserId,
    //        t.F_LastModifyTime, n.F_NodePropertyJson,o.F_Description FROM flow_taskoperator o left join flow_task t on o.F_TaskId = t.F_Id left join flow_tasknode n on o.F_TaskNodeId = n.F_Id
    //        WHERE 1=1 AND o.F_Completion = 0 AND t.F_Status = 1 AND o.F_State = '0'  ${sql}
    List<FlowTaskListModel> getWaitList(@Param("sql") String sql);
}
