package com.taotao.cloud.office.execl.core.exception;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**

 */
public class ExceptionThrowDataListener implements ReadListener<ExceptionData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionData.class);
    List<ExceptionData> list = new ArrayList<ExceptionData>();

    @Override
    public void invoke(ExceptionData data, AnalysisContext context) {
        list.add(data);
        if (list.size() == 5) {
            int i = 5 / 0;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
