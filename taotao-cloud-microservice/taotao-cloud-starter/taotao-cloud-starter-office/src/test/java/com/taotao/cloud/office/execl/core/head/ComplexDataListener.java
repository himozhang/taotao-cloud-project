package com.taotao.cloud.office.execl.core.head;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**

 */
public class ComplexDataListener extends AnalysisEventListener<ComplexHeadData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(
		    ComplexHeadData.class);
    List<ComplexHeadData> list = new ArrayList<ComplexHeadData>();

    @Override
    public void invoke(ComplexHeadData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assert.assertEquals(list.size(), 1);
        ComplexHeadData data = list.get(0);
        Assert.assertEquals(data.getString4(), "字符串4");
        LOGGER.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}
