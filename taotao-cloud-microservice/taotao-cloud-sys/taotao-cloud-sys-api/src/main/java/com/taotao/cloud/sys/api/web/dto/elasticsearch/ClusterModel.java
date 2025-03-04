package com.taotao.cloud.sys.api.web.dto.elasticsearch;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClusterModel {
    private JSONObject clusterState;
    private JSONObject status;
    private JSONObject nodeStats;
    private JSONObject clusterNodes;
    private JSONObject clusterHealth;
}
