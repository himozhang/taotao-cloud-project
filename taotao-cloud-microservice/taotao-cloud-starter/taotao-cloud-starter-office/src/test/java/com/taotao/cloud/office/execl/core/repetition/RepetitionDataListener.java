package com.taotao.cloud.office.execl.core.repetition;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import com.taotao.cloud.common.execl.core.simple.SimpleDataListener;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**

 */
public class RepetitionDataListener extends AnalysisEventListener<RepetitionData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataListener.class);
    List<RepetitionData> list = new ArrayList<RepetitionData>();

    @Override
    public void invoke(RepetitionData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getString(), "字符串0");
        Assert.assertEquals(list.get(1).getString(), "字符串0");
        LOGGER.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}
