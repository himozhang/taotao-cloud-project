package com.taotao.cloud.office.execl.core.noncamel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import com.taotao.cloud.common.utils.log.LogUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;

/**

 */

public class UnCamelDataListener extends AnalysisEventListener<UnCamelData> {
    List<UnCamelData> list = new ArrayList<>();

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        LogUtil.debug("Head is:{}", JSON.toJSONString(headMap));
        Assert.assertEquals(headMap.get(0), "string1");
        Assert.assertEquals(headMap.get(1), "string2");
        Assert.assertEquals(headMap.get(2), "STring3");
        Assert.assertEquals(headMap.get(3), "STring4");
        Assert.assertEquals(headMap.get(4), "STRING5");
        Assert.assertEquals(headMap.get(5), "STRing6");

    }

    @Override
    public void invoke(UnCamelData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assert.assertEquals(list.size(), 10);
        UnCamelData unCamelData = list.get(0);
        Assert.assertEquals(unCamelData.getString1(), "string1");
        Assert.assertEquals(unCamelData.getString2(), "string2");
        Assert.assertEquals(unCamelData.getSTring3(), "string3");
        Assert.assertEquals(unCamelData.getSTring4(), "string4");
        Assert.assertEquals(unCamelData.getSTRING5(), "string5");
        Assert.assertEquals(unCamelData.getSTRing6(), "string6");
        LogUtil.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}
