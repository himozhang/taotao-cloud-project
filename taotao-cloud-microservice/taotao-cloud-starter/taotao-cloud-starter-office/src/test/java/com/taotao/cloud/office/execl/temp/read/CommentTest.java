package com.taotao.cloud.office.execl.temp.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 临时测试
 *

 */
@Ignore
public class CommentTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentTest.class);

    @Test
    public void comment() throws Exception {
        File file = new File("D:\\test\\d1.xlsx");
        List<Map<Integer, CellData>> datas = EasyExcel.read(file).doReadAllSync();
        for (Map<Integer, CellData> data : datas) {
            LOGGER.info("数据:{}", JSON.toJSONString(data));
        }
    }

}
